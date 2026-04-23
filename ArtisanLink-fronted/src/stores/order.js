import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useOrderStore = defineStore('order', () => {
  // 未读订单通知数量
  const unreadCount = ref(0)
  
  // 最新订单列表（用于实时展示）
  const recentOrders = ref([])
  
  // WebSocket连接状态
  const wsConnected = ref(false)
  
  // 增加未读数
  function incrementUnread() {
    unreadCount.value++
  }
  
  // 清空未读数
  function clearUnread() {
    unreadCount.value = 0
  }
  
  // 添加新订单到列表
  function addRecentOrder(order) {
    recentOrders.value.unshift(order)
    if (recentOrders.value.length > 10) {
      recentOrders.value.pop()
    }
    incrementUnread()
  }
  
  // 更新订单状态
  function updateOrderStatus(orderId, status) {
    const order = recentOrders.value.find(o => o.id === orderId)
    if (order) {
      order.status = status
    }
  }
  
  // 设置WebSocket连接状态
  function setWsConnected(connected) {
    wsConnected.value = connected
  }
  
  return {
    unreadCount,
    recentOrders,
    wsConnected,
    incrementUnread,
    clearUnread,
    addRecentOrder,
    updateOrderStatus,
    setWsConnected
  }
})