/**
 * WebSocket消息处理器
 * 用于处理订单通知、状态变更等实时消息
 */

import { ElNotification } from 'element-plus'
import { useOrderStore } from '../stores/order'

const listeners = new Map()

export const wsHandler = {
  on(type, handler) {
    if (!listeners.has(type)) {
      listeners.set(type, new Set())
    }
    listeners.get(type).add(handler)
  },
  off(type, handler) {
    if (!listeners.has(type)) return
    listeners.get(type).delete(handler)
  },
  emit(type, payload) {
    if (!listeners.has(type)) return
    listeners.get(type).forEach((handler) => handler(payload))
  }
}

/**
 * WebSocket消息类型枚举
 */
export const WS_MESSAGE_TYPE = {
  // 订单相关
  ORDER_CREATED: 'ORDER_CREATED',           // 新订单创建
  ORDER_ACCEPTED: 'ORDER_ACCEPTED',         // 订单已接单
  ORDER_REJECTED: 'ORDER_REJECTED',         // 订单已拒绝
  ORDER_COMPLETED: 'ORDER_COMPLETED',       // 订单已完成
  ORDER_CANCELLED: 'ORDER_CANCELLED',       // 订单已取消
  
  // 通知相关
  NOTIFICATION: 'NOTIFICATION',             // 通用通知
  SYSTEM_MESSAGE: 'SYSTEM_MESSAGE'          // 系统消息
}

/**
 * 处理WebSocket消息
 * @param {Object} message - WebSocket消息对象
 */
export function handleWSMessage(message) {
  console.log('[WS] 收到消息:', message)
  wsHandler.emit('message', message?.data || message)
  
  if (!message || !message.type) {
    console.warn('[WS] 无效的消息格式')
    return
  }
  
  const orderStore = useOrderStore()
  
  switch (message.type) {
    case WS_MESSAGE_TYPE.ORDER_CREATED:
      // B端：新订单通知
      wsHandler.emit('order_notification', message.data)
      handleNewOrder(message.data)
      break
      
    case WS_MESSAGE_TYPE.ORDER_ACCEPTED:
      // C端：订单已接单通知
      handleOrderAccepted(message.data)
      break
      
    case WS_MESSAGE_TYPE.ORDER_REJECTED:
      // C端：订单已拒绝通知
      handleOrderRejected(message.data)
      break
      
    case WS_MESSAGE_TYPE.ORDER_COMPLETED:
      // 订单已完成通知
      handleOrderCompleted(message.data)
      break
      
    case WS_MESSAGE_TYPE.ORDER_CANCELLED:
      // 订单已取消通知
      handleOrderCancelled(message.data)
      break
      
    case WS_MESSAGE_TYPE.NOTIFICATION:
      // 通用通知
      handleNotification(message.data)
      break
      
    case WS_MESSAGE_TYPE.SYSTEM_MESSAGE:
      // 系统消息
      handleSystemMessage(message.data)
      break
      
    default:
      console.warn('[WS] 未知的消息类型:', message.type)
  }
}

/**
 * 处理新订单（B端）
 */
function handleNewOrder(data) {
  const orderStore = useOrderStore()
  
  // 添加到未处理订单列表
  orderStore.addPendingOrder(data)
  
  // 显示通知
  ElNotification({
    title: '新订单提醒',
    message: `收到来自${data.customerName || '用户'}的新订单`,
    type: 'success',
    duration: 5000,
    onClick: () => {
      // 点击通知跳转到订单详情页
      window.location.href = `/admin/order/${data.id}`
    }
  })
  
  // 播放提示音（可选）
  playNotificationSound()
}

/**
 * 处理订单已接单（C端）
 */
function handleOrderAccepted(data) {
  const orderStore = useOrderStore()
  
  // 更新订单状态
  orderStore.updateOrderStatus(data.id, 'ACCEPTED', data)
  
  // 显示通知
  ElNotification({
    title: '订单已接单',
    message: `您的订单已被技师${data.technicianName || ''}接单，请准时到店`,
    type: 'success',
    duration: 5000
  })
}

/**
 * 处理订单已拒绝（C端）
 */
function handleOrderRejected(data) {
  const orderStore = useOrderStore()
  
  // 更新订单状态
  orderStore.updateOrderStatus(data.id, 'REJECTED', data)
  
  // 显示通知
  ElNotification({
    title: '订单已拒绝',
    message: data.reason || '抱歉，该订单已被拒绝，请选择其他时间或技师',
    type: 'warning',
    duration: 8000
  })
}

/**
 * 处理订单已完成
 */
function handleOrderCompleted(data) {
  const orderStore = useOrderStore()
  
  // 更新订单状态
  orderStore.updateOrderStatus(data.id, 'COMPLETED', data)
  
  // 显示通知
  ElNotification({
    title: '订单已完成',
    message: '服务已完成，欢迎再次光临！',
    type: 'info',
    duration: 3000
  })
}

/**
 * 处理订单已取消
 */
function handleOrderCancelled(data) {
  const orderStore = useOrderStore()
  
  // 更新订单状态
  orderStore.updateOrderStatus(data.id, 'CANCELLED', data)
  
  // 显示通知
  ElNotification({
    title: '订单已取消',
    message: data.reason || '订单已取消',
    type: 'info',
    duration: 3000
  })
}

/**
 * 处理通用通知
 */
function handleNotification(data) {
  ElNotification({
    title: data.title || '通知',
    message: data.content || '',
    type: data.type || 'info',
    duration: data.duration || 5000
  })
}

/**
 * 处理系统消息
 */
function handleSystemMessage(data) {
  console.log('[WS] 系统消息:', data)
  
  ElNotification({
    title: '系统消息',
    message: data.message || '',
    type: 'info',
    duration: 5000
  })
}

/**
 * 播放提示音
 */
function playNotificationSound() {
  try {
    const audio = new Audio('/notification.mp3')
    audio.volume = 0.5
    audio.play().catch(err => {
      console.warn('[WS] 播放提示音失败:', err)
    })
  } catch (err) {
    console.warn('[WS] 音频播放不支持:', err)
  }
}

/**
 * 格式化订单消息
 * @param {Object} order - 订单对象
 * @returns {string} 格式化的消息字符串
 */
export function formatOrderMessage(order) {
  const statusMap = {
    'PENDING': '待接单',
    'ACCEPTED': '已接单',
    'REJECTED': '已拒绝',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  
  return `订单#${order.orderNo || order.id} - ${statusMap[order.status] || order.status}`
}
