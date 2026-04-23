<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import wsClient from '../utils/websocket'
import { getToken } from '../utils/auth'

const notifications = ref([])
const unreadCount = ref(0)
const showPanel = ref(false)

// WebSocket连接
let isConnected = false

onMounted(() => {
  // 注册WebSocket事件监听
  wsClient.on('order_notification', handleOrderNotification)
  wsClient.on('order_status_change', handleOrderStatusChange)
  wsClient.on('message', handleMessage)
  
  // 尝试连接WebSocket（如果已登录）
  const token = getToken()
  if (token) {
    connectWebSocket()
  }
})

onUnmounted(() => {
  // 移除事件监听
  wsClient.off('order_notification', handleOrderNotification)
  wsClient.off('order_status_change', handleOrderStatusChange)
  wsClient.off('message', handleMessage)
})

/**
 * 连接WebSocket
 */
function connectWebSocket() {
  if (isConnected) return
  
  const shopId = 1 // TODO: 从用户信息中获取门店ID
  const wsUrl = `ws://localhost:8124/ws/shop/${shopId}`
  
  wsClient.connect(wsUrl)
  isConnected = true
}

/**
 * 处理订单通知
 */
function handleOrderNotification(data) {
  console.log('收到订单通知:', data)
  
  const notification = {
    id: Date.now(),
    type: 'order',
    title: '新订单提醒',
    content: data.message || `收到新订单：${data.orderNo}`,
    time: new Date().toLocaleString(),
    read: false,
    data: data
  }
  
  notifications.value.unshift(notification)
  unreadCount.value++
  
  // 显示Toast提示
  showToast(notification.content)
}

/**
 * 处理订单状态变更
 */
function handleOrderStatusChange(data) {
  console.log('订单状态变更:', data)
  
  const notification = {
    id: Date.now(),
    type: 'status',
    title: '订单状态更新',
    content: data.message || `订单 ${data.orderNo} 状态已更新`,
    time: new Date().toLocaleString(),
    read: false,
    data: data
  }
  
  notifications.value.unshift(notification)
  unreadCount.value++
  
  showToast(notification.content)
}

/**
 * 处理普通消息
 */
function handleMessage(data) {
  console.log('收到消息:', data)
}

/**
 * 显示Toast提示
 */
function showToast(message) {
  // 创建临时Toast元素
  const toast = document.createElement('div')
  toast.className = 'notification-toast'
  toast.textContent = message
  document.body.appendChild(toast)
  
  setTimeout(() => {
    toast.classList.add('show')
  }, 10)
  
  setTimeout(() => {
    toast.classList.remove('show')
    setTimeout(() => {
      document.body.removeChild(toast)
    }, 300)
  }, 3000)
}

/**
 * 标记为已读
 */
function markAsRead(notification) {
  if (!notification.read) {
    notification.read = true
    unreadCount.value--
  }
}

/**
 * 清空所有通知
 */
function clearAll() {
  notifications.value = []
  unreadCount.value = 0
}

/**
 * 切换面板显示
 */
function togglePanel() {
  showPanel.value = !showPanel.value
}

// 暴露方法供外部调用
defineExpose({
  connectWebSocket,
  notifications,
  unreadCount
})
</script>

<template>
  <div class="notification-bell" @click="togglePanel">
    <span class="bell-icon">🔔</span>
    <span v-if="unreadCount > 0" class="badge">{{ unreadCount }}</span>
    
    <!-- 通知面板 -->
    <div v-if="showPanel" class="notification-panel">
      <div class="panel-header">
        <h3>消息通知</h3>
        <button class="clear-btn" @click="clearAll">清空</button>
      </div>
      
      <div class="notification-list">
        <div v-if="notifications.length === 0" class="empty-tip">
          暂无通知
        </div>
        
        <div
          v-for="item in notifications"
          :key="item.id"
          class="notification-item"
          :class="{ unread: !item.read }"
          @click="markAsRead(item)"
        >
          <div class="item-header">
            <span class="item-title">{{ item.title }}</span>
            <span class="item-time">{{ item.time }}</span>
          </div>
          <div class="item-content">{{ item.content }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.notification-bell {
  position: relative;
  cursor: pointer;
  padding: 8px 12px;
}

.bell-icon {
  font-size: 20px;
}

.badge {
  position: absolute;
  top: 0;
  right: 0;
  background: #ff4d4f;
  color: white;
  border-radius: 10px;
  padding: 2px 6px;
  font-size: 12px;
  min-width: 18px;
  text-align: center;
}

.notification-panel {
  position: absolute;
  top: 40px;
  right: 0;
  width: 350px;
  max-height: 500px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
}

.clear-btn {
  background: none;
  border: none;
  color: #1890ff;
  cursor: pointer;
  font-size: 14px;
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;
}

.empty-tip {
  padding: 40px;
  text-align: center;
  color: #999;
}

.notification-item {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.3s;
}

.notification-item:hover {
  background: #f5f5f5;
}

.notification-item.unread {
  background: #e6f7ff;
}

.item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.item-title {
  font-weight: 500;
  font-size: 14px;
}

.item-time {
  font-size: 12px;
  color: #999;
}

.item-content {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}
</style>
