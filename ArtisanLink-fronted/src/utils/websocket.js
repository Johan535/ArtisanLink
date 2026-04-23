/**
 * WebSocket实时通信封装
 * 用于订单状态、接单通知等实时推送
 */

import { handleWSMessage, WS_MESSAGE_TYPE } from './wsHandler'
import { getUserInfo, getToken } from './auth'

const RECONNECT_DELAY = 3000 // 重连延迟（毫秒）
const MAX_RECONNECT_ATTEMPTS = 10 // 最大重连次数

class WebSocketClient {
  constructor() {
    this.ws = null
    this.url = ''
    this.reconnectAttempts = 0
    this.isManualClose = false
    this.messageHandlers = new Map()
    this.heartbeatTimer = null
    this.heartbeatInterval = 30000 // 心跳间隔30秒
  }

  /**
   * 连接WebSocket
   * @param {string} shopId - 门店ID（B端）或用户ID（C端）
   * @param {string} userType - 用户类型：'merchant' | 'customer'
   */
  connect(shopId, userType = 'merchant') {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      console.log('[WS] 已存在活跃连接')
      return
    }

    this.isManualClose = false
    const token = getToken()
    
    if (!token) {
      console.warn('[WS] 未登录，无法建立WebSocket连接')
      return
    }

    // 构建WebSocket URL
    const wsBaseUrl = import.meta.env.VITE_WS_URL || 'ws://localhost:8080'
    this.url = `${wsBaseUrl}/ws/${userType}/${shopId}?token=${token}`

    try {
      this.ws = new WebSocket(this.url)
      this.setupEventListeners()
    } catch (error) {
      console.error('[WS] 创建WebSocket连接失败:', error)
      this.scheduleReconnect(shopId, userType)
    }
  }

  /**
   * 设置事件监听器
   */
  setupEventListeners() {
    this.ws.onopen = () => {
      console.log('[WS] 连接成功')
      this.reconnectAttempts = 0
      this.startHeartbeat()
    }

    this.ws.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data)
        console.log('[WS] 收到消息:', message)
        
        // 使用统一的消息处理器
        handleWSMessage(message)
        
        // 触发自定义处理器
        if (message.type && this.messageHandlers.has(message.type)) {
          this.messageHandlers.get(message.type).forEach(handler => {
            handler(message.data)
          })
        }
      } catch (error) {
        console.error('[WS] 解析消息失败:', error)
      }
    }

    this.ws.onerror = (error) => {
      console.error('[WS] 连接错误:', error)
    }

    this.ws.onclose = (event) => {
      console.log('[WS] 连接关闭:', event.code, event.reason)
      this.stopHeartbeat()
      
      if (!this.isManualClose) {
        // 非主动关闭，尝试重连
        const userInfo = getUserInfo()
        if (userInfo) {
          const shopId = userInfo.shopId || userInfo.id
          const userType = userInfo.role === 'merchant' ? 'merchant' : 'customer'
          this.scheduleReconnect(shopId, userType)
        }
      }
    }
  }

  /**
   * 发送消息
   * @param {Object} data - 要发送的数据
   */
  send(data) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(data))
      return true
    }
    console.warn('[WS] 连接未打开，无法发送消息')
    return false
  }

  /**
   * 注册消息处理器
   * @param {string} type - 消息类型
   * @param {Function} handler - 处理函数
   */
  on(type, handler) {
    if (!this.messageHandlers.has(type)) {
      this.messageHandlers.set(type, [])
    }
    this.messageHandlers.get(type).push(handler)
  }

  /**
   * 移除消息处理器
   * @param {string} type - 消息类型
   * @param {Function} handler - 处理函数
   */
  off(type, handler) {
    if (this.messageHandlers.has(type)) {
      const handlers = this.messageHandlers.get(type)
      const index = handlers.indexOf(handler)
      if (index > -1) {
        handlers.splice(index, 1)
      }
    }
  }

  /**
   * 开始心跳
   */
  startHeartbeat() {
    this.heartbeatTimer = setInterval(() => {
      this.send({ type: 'PING', timestamp: Date.now() })
    }, this.heartbeatInterval)
  }

  /**
   * 停止心跳
   */
  stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }

  /**
   * 安排重连
   */
  scheduleReconnect(shopId, userType) {
    if (this.reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
      console.error('[WS] 达到最大重连次数，停止重连')
      return
    }

    this.reconnectAttempts++
    const delay = RECONNECT_DELAY * Math.pow(2, this.reconnectAttempts - 1)
    
    console.log(`[WS] ${delay}ms后尝试第${this.reconnectAttempts}次重连`)
    
    setTimeout(() => {
      if (!this.isManualClose) {
        this.connect(shopId, userType)
      }
    }, delay)
  }

  /**
   * 断开连接
   */
  disconnect() {
    this.isManualClose = true
    this.stopHeartbeat()
    
    if (this.ws) {
      this.ws.close(1000, '主动断开')
      this.ws = null
    }
    
    this.messageHandlers.clear()
  }

  /**
   * 获取连接状态
   * @returns {number} WebSocket就绪状态
   */
  getReadyState() {
    return this.ws ? this.ws.readyState : WebSocket.CLOSED
  }
}

// 单例模式
const wsClient = new WebSocketClient()

export default wsClient
export { WS_MESSAGE_TYPE }