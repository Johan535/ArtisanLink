<template>
  <admin-layout>
    <div class="message-container">
      <!-- 顶部操作栏 -->
      <div class="header-actions">
        <h2>消息中心</h2>
        <el-button-group>
          <el-button @click="markAllAsRead" :disabled="unreadCount === 0">
            <el-icon><Check /></el-icon>
            全部已读
          </el-button>
          <el-button @click="clearAllMessages" :disabled="messages.length === 0">
            <el-icon><Delete /></el-icon>
            清空消息
          </el-button>
        </el-button-group>
      </div>
      
      <!-- 消息列表 -->
      <div class="message-list" v-loading="loading">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="全部消息" name="all">
            <div class="message-count">共 {{ totalCount }} 条消息</div>
          </el-tab-pane>
          <el-tab-pane label="未读消息" name="unread">
            <div class="message-count">共 {{ unreadCount }} 条未读消息</div>
          </el-tab-pane>
          <el-tab-pane label="订单通知" name="order">
            <div class="message-count">共 {{ orderCount }} 条订单通知</div>
          </el-tab-pane>
        </el-tabs>
        
        <div class="messages-wrapper">
          <div
            v-for="message in filteredMessages"
            :key="message.id"
            class="message-item"
            :class="{ unread: !message.isRead }"
            @click="handleMessageClick(message)"
          >
            <div class="message-icon" :class="getMessageTypeClass(message.type)">
              <el-icon :size="24">
                <component :is="getMessageIcon(message.type)" />
              </el-icon>
            </div>
            
            <div class="message-content">
              <div class="message-header">
                <span class="message-title">{{ message.title }}</span>
                <span class="message-time">{{ formatTime(message.createTime) }}</span>
              </div>
              <div class="message-body">{{ message.content }}</div>
              <div class="message-footer">
                <el-tag size="small" :type="getMessageTagType(message.type)">
                  {{ getMessageTypeName(message.type) }}
                </el-tag>
                <span v-if="!message.isRead" class="unread-badge">未读</span>
              </div>
            </div>
            
            <div class="message-action" v-if="!message.isRead">
              <el-button size="small" type="primary" @click.stop="markAsRead(message)">
                标记已读
              </el-button>
            </div>
          </div>
          
          <el-empty v-if="!loading && filteredMessages.length === 0" description="暂无消息" />
          
          <!-- 加载更多 -->
          <div class="load-more" v-if="hasMore && !loading">
            <el-button @click="loadMore" :loading="loadingMore">
              {{ loadingMore ? '加载中...' : '加载更多' }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </admin-layout>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Delete, Bell, Document, Warning, CircleCheck } from '@element-plus/icons-vue'
import AdminLayout from '@/components/AdminLayout.vue'
import { adminApi } from '@/api/index'
import { useWebSocket } from '@/utils/websocket'
import { wsHandler } from '@/utils/wsHandler'

// WebSocket实例
const wsInstance = useWebSocket()

// 状态
const activeTab = ref('all')
const messages = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const hasMore = ref(true)

/**
 * 计算属性
 */
const totalCount = computed(() => messages.value.length)
const unreadCount = computed(() => messages.value.filter(m => !m.isRead).length)
const orderCount = computed(() => messages.value.filter(m => m.type === 'order').length)

const filteredMessages = computed(() => {
  let filtered = [...messages.value]
  
  if (activeTab.value === 'unread') {
    filtered = filtered.filter(m => !m.isRead)
  } else if (activeTab.value === 'order') {
    filtered = filtered.filter(m => m.type === 'order')
  }
  
  return filtered
})

/**
 * 获取消息类型样式类
 */
function getMessageTypeClass(type) {
  const typeMap = {
    'order': 'order-type',
    'system': 'system-type',
    'warning': 'warning-type',
    'success': 'success-type'
  }
  return typeMap[type] || 'default-type'
}

/**
 * 获取消息图标
 */
function getMessageIcon(type) {
  const iconMap = {
    'order': 'Document',
    'system': 'Bell',
    'warning': 'Warning',
    'success': 'CircleCheck'
  }
  return iconMap[type] || 'Bell'
}

/**
 * 获取消息标签类型
 */
function getMessageTagType(type) {
  const typeMap = {
    'order': 'primary',
    'system': 'info',
    'warning': 'warning',
    'success': 'success'
  }
  return typeMap[type] || 'info'
}

/**
 * 获取消息类型名称
 */
function getMessageTypeName(type) {
  const nameMap = {
    'order': '订单通知',
    'system': '系统消息',
    'warning': '预警通知',
    'success': '成功通知'
  }
  return nameMap[type] || '其他'
}

/**
 * 格式化时间
 */
function formatTime(timeStr) {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  // 小于1分钟
  if (diff < 60000) {
    return '刚刚'
  }
  // 小于1小时
  if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  }
  // 小于24小时
  if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  }
  // 大于24小时
  return date.toLocaleDateString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/**
 * 加载消息列表
 */
async function loadMessages(isRefresh = false) {
  if (isRefresh) {
    currentPage.value = 1
    messages.value = []
  }
  
  if (loading.value || loadingMore.value) return
  
  if (currentPage.value === 1) {
    loading.value = true
  } else {
    loadingMore.value = true
  }
  
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      type: activeTab.value === 'all' ? undefined : activeTab.value,
      isRead: activeTab.value === 'unread' ? false : undefined
    }
    
    const res = await adminApi.getMessageList(params)
    
    if (res.code === 200) {
      const newMessages = res.data?.list || []
      
      if (isRefresh) {
        messages.value = newMessages
      } else {
        messages.value.push(...newMessages)
      }
      
      hasMore.value = newMessages.length >= pageSize.value
      currentPage.value++
    } else {
      ElMessage.error(res.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载消息列表失败:', error)
    ElMessage.error('加载失败，请稍后重试')
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

/**
 * 加载更多
 */
function loadMore() {
  loadMessages(false)
}

/**
 * 处理Tab切换
 */
function handleTabChange() {
  loadMessages(true)
}

/**
 * 处理消息点击
 */
async function handleMessageClick(message) {
  if (!message.isRead) {
    await markAsRead(message)
  }
  
  // 根据消息类型执行不同操作
  if (message.type === 'order' && message.orderId) {
    // 跳转到订单详情
    window.location.href = `/admin/order/${message.orderId}`
  }
}

/**
 * 标记单条消息为已读
 */
async function markAsRead(message) {
  try {
    const res = await adminApi.markMessageAsRead(message.id)
    
    if (res.code === 200) {
      message.isRead = true
      ElMessage.success('标记成功')
    } else {
      ElMessage.error(res.msg || '标记失败')
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('标记失败，请稍后重试')
  }
}

/**
 * 全部标记为已读
 */
async function markAllAsRead() {
  try {
    await ElMessageBox.confirm('确定要将所有消息标记为已读吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    const res = await adminApi.markAllMessagesAsRead()
    
    if (res.code === 200) {
      messages.value.forEach(m => m.isRead = true)
      ElMessage.success('全部标记成功')
    } else {
      ElMessage.error(res.msg || '标记失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('全部标记已读失败:', error)
      ElMessage.error('标记失败，请稍后重试')
    }
  }
}

/**
 * 清空所有消息
 */
async function clearAllMessages() {
  try {
    await ElMessageBox.confirm('确定要清空所有消息吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await adminApi.clearAllMessages()
    
    if (res.code === 200) {
      messages.value = []
      ElMessage.success('清空成功')
    } else {
      ElMessage.error(res.msg || '清空失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空消息失败:', error)
      ElMessage.error('清空失败，请稍后重试')
    }
  }
}

/**
 * 处理WebSocket新消息
 */
function handleNewMessage(data) {
  // 将新消息添加到列表顶部
  messages.value.unshift({
    id: data.messageId || Date.now(),
    title: data.title || '新消息',
    content: data.content || '',
    type: data.type || 'order',
    orderId: data.orderId,
    isRead: false,
    createTime: new Date().toISOString()
  })
  
  // 显示通知
  ElMessage({
    message: data.title || '收到新消息',
    type: 'info',
    duration: 3000
  })
}

onMounted(() => {
  loadMessages(true)
  
  // 注册WebSocket消息处理器
  wsHandler.on('message', handleNewMessage)
  wsHandler.on('order_notification', handleNewMessage)
})

onUnmounted(() => {
  // 移除WebSocket消息处理器
  wsHandler.off('message', handleNewMessage)
  wsHandler.off('order_notification', handleNewMessage)
})
</script>

<style scoped>
.message-container {
  padding: 24px;
  background: #fff;
  border-radius: 8px;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-actions h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.message-list {
  min-height: calc(100vh - 300px);
}

.message-count {
  font-size: 14px;
  color: #999;
  margin-bottom: 16px;
}

.messages-wrapper {
  margin-top: 16px;
}

.message-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.message-item:hover {
  background: #e4e7ed;
}

.message-item.unread {
  background: #ecf5ff;
  border-left: 3px solid #409eff;
}

.message-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.order-type {
  background: #ecf5ff;
  color: #409eff;
}

.system-type {
  background: #f4f4f5;
  color: #909399;
}

.warning-type {
  background: #fdf6ec;
  color: #e6a23c;
}

.success-type {
  background: #f0f9eb;
  color: #67c23a;
}

.default-type {
  background: #f4f4f5;
  color: #909399;
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.message-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.message-time {
  font-size: 12px;
  color: #909399;
}

.message-body {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.message-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.unread-badge {
  font-size: 12px;
  color: #409eff;
  font-weight: 600;
}

.message-action {
  display: flex;
  align-items: center;
}

.load-more {
  text-align: center;
  padding: 16px 0;
}
</style>
