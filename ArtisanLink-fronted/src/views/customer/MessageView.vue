<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { customerApi } from '@/api/customer'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 消息列表
const messages = ref([])

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0,
  hasMore: true
})

// 加载状态
const loading = reactive({
  list: false,
  more: false
})

// 当前筛选条件
const filterType = ref('all') // all-全部, unread-未读

// 加载消息列表
async function loadMessages(isRefresh = false) {
  if (isRefresh) {
    pagination.pageNum = 1
    pagination.hasMore = true
    messages.value = []
  }
  
  if (!pagination.hasMore) return
  
  const isLoading = isRefresh ? loading.list : loading.more
  if (isLoading.value) return
  
  isLoading.value = true
  
  try {
    const isRead = filterType.value === 'unread' ? false : null
    
    const res = await customerApi.getMessages({
      isRead,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    
    if (res.code === 200 && res.data) {
      const newList = res.data.list || []
      
      if (isRefresh) {
        messages.value = newList
      } else {
        messages.value.push(...newList)
      }
      
      pagination.total = res.data.total || 0
      pagination.hasMore = newList.length >= pagination.pageSize
      pagination.pageNum++
    }
  } catch (error) {
    console.error('加载消息失败:', error)
    ElMessage.error('加载消息失败')
  } finally {
    isLoading.value = false
  }
}

// 切换筛选类型
function handleFilterChange(type) {
  filterType.value = type
  loadMessages(true)
}

// 下拉刷新
async function handleRefresh() {
  await loadMessages(true)
}

// 加载更多
function loadMore() {
  if (!loading.more && pagination.hasMore) {
    loadMessages(false)
  }
}

// 标记消息已读
async function markAsRead(messageId) {
  try {
    const res = await customerApi.markMessageRead(messageId)
    if (res.code === 200) {
      // 更新本地消息状态
      const message = messages.value.find(m => m.id === messageId)
      if (message) {
        message.isRead = true
      }
    }
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

// 点击消息项
function handleMessageClick(message) {
  if (!message.isRead) {
    markAsRead(message.id)
  }
  
  // 根据消息类型跳转
  if (message.type === 'order' && message.orderId) {
    router.push(`/customer/order/${message.orderId}`)
  } else if (message.type === 'system') {
    // 系统消息，可以显示详情对话框
    showMessageDetail(message)
  }
}

// 显示消息详情
function showMessageDetail(message) {
  // 这里可以使用ElMessageBox或自定义对话框显示详情
  console.log('消息详情:', message)
}

// 获取消息类型文本
function getTypeText(type) {
  const typeMap = {
    order: '订单通知',
    system: '系统消息',
    promotion: '优惠活动'
  }
  return typeMap[type] || '其他'
}

// 获取消息类型样式类
function getTypeClass(type) {
  const classMap = {
    order: 'type-order',
    system: 'type-system',
    promotion: 'type-promotion'
  }
  return classMap[type] || ''
}

onMounted(async () => {
  await loadMessages(true)
})
</script>

<template>
  <div class="message-container">
    <!-- 顶部导航 -->
    <header class="message-header">
      <el-button 
        :icon="'ArrowLeft'" 
        circle 
        @click="router.back()"
        class="back-btn"
      />
      <h2 class="header-title">消息中心</h2>
    </header>

    <!-- 筛选标签 -->
    <div class="filter-tabs">
      <el-radio-group v-model="filterType" @change="handleFilterChange">
        <el-radio-button label="all">全部</el-radio-button>
        <el-radio-button label="unread">未读</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 消息列表 -->
    <div class="message-content">
      <!-- 加载状态 -->
      <el-skeleton v-if="loading.list && messages.length === 0" :rows="5" animated />
      
      <!-- 消息列表 -->
      <div v-else class="message-list">
        <div 
          v-for="msg in messages" 
          :key="msg.id"
          :class="['message-item', { unread: !msg.isRead }]"
          @click="handleMessageClick(msg)"
        >
          <!-- 消息图标 -->
          <div :class="['message-icon', getTypeClass(msg.type)]">
            <el-icon>
              <component :is="msg.type === 'order' ? 'Document' : msg.type === 'promotion' ? 'Present' : 'Bell'" />
            </el-icon>
          </div>
          
          <!-- 消息内容 -->
          <div class="message-body">
            <div class="message-header-info">
              <span class="message-type">{{ getTypeText(msg.type) }}</span>
              <span class="message-time">{{ msg.createTime }}</span>
            </div>
            <h4 class="message-title">{{ msg.title }}</h4>
            <p class="message-content-text">{{ msg.content }}</p>
          </div>
          
          <!-- 未读标记 -->
          <div v-if="!msg.isRead" class="unread-dot"></div>
        </div>
        
        <!-- 空状态 -->
        <el-empty 
          v-if="!loading.list && messages.length === 0" 
          description="暂无消息"
        />
        
        <!-- 加载更多 -->
        <div v-if="pagination.hasMore" class="load-more" @click="loadMore">
          <span v-if="!loading.more">加载更多</span>
          <el-loading v-else />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.message-container {
  min-height: 100vh;
  background: #f8f9fa;
}

/* 顶部导航 */
.message-header {
  background: linear-gradient(135deg, #d4a574 0%, #c9956b 100%);
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: #fff;
}

.header-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

/* 筛选标签 */
.filter-tabs {
  background: #fff;
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
}

/* 消息内容区 */
.message-content {
  padding: 0 16px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 消息项 */
.message-item {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  gap: 12px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.message-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.message-item.unread {
  background: #fef8f3;
}

/* 消息图标 */
.message-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.type-order {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  color: #2196f3;
}

.type-system {
  background: linear-gradient(135deg, #f3e5f5 0%, #e1bee7 100%);
  color: #9c27b0;
}

.type-promotion {
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
  color: #ff9800;
}

/* 消息主体 */
.message-body {
  flex: 1;
  min-width: 0;
}

.message-header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.message-type {
  font-size: 12px;
  color: #999;
}

.message-time {
  font-size: 12px;
  color: #ccc;
}

.message-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-content-text {
  font-size: 13px;
  color: #666;
  margin: 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 未读标记 */
.unread-dot {
  width: 8px;
  height: 8px;
  background: #f56c6c;
  border-radius: 50%;
  position: absolute;
  top: 16px;
  right: 16px;
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 16px;
  color: #d4a574;
  cursor: pointer;
  font-size: 14px;
}

.load-more:hover {
  color: #c9956b;
}

/* 响应式适配 */
@media (max-width: 750px) {
  .message-content {
    padding: 0 12px;
  }
}

@media (min-width: 751px) {
  .message-container {
    max-width: 750px;
    margin: 0 auto;
  }
}
</style>
