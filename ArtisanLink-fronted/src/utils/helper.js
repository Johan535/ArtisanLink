/**
 * 防抖和节流工具函数
 */

/**
 * 防抖函数（debounce）
 * 在事件被触发n秒后再执行回调，如果n秒内又被触发，则重新计时
 * @param {Function} func - 要防抖的函数
 * @param {number} delay - 延迟时间（毫秒），默认300ms
 * @returns {Function} 防抖后的函数
 */
export function debounce(func, delay = 300) {
  let timer = null
  
  return function (...args) {
    // 清除之前的定时器
    if (timer) {
      clearTimeout(timer)
    }
    
    // 设置新的定时器
    timer = setTimeout(() => {
      func.apply(this, args)
    }, delay)
  }
}

/**
 * 节流函数（throttle）
 * 规定在一个单位时间内，只能触发一次函数。如果这个单位时间内触发多次函数，只有一次生效
 * @param {Function} func - 要节流的函数
 * @param {number} interval - 时间间隔（毫秒），默认300ms
 * @returns {Function} 节流后的函数
 */
export function throttle(func, interval = 300) {
  let lastTime = 0
  let timer = null
  
  return function (...args) {
    const now = Date.now()
    const remaining = interval - (now - lastTime)
    
    // 清除之前的定时器
    if (timer) {
      clearTimeout(timer)
    }
    
    if (remaining <= 0) {
      // 超过时间间隔，立即执行
      func.apply(this, args)
      lastTime = now
    } else {
      // 设置定时器，在剩余时间后执行
      timer = setTimeout(() => {
        func.apply(this, args)
        lastTime = Date.now()
      }, remaining)
    }
  }
}

/**
 * 防止重复提交
 * @param {Function} func - 要执行的函数
 * @param {number} lockTime - 锁定时间（毫秒），默认1000ms
 * @returns {Function} 防重复提交的函数
 */
export function preventDuplicateSubmit(func, lockTime = 1000) {
  let isLocked = false
  
  return async function (...args) {
    if (isLocked) {
      console.warn('请勿重复提交')
      return Promise.reject(new Error('请勿重复提交'))
    }
    
    isLocked = true
    
    try {
      const result = await func.apply(this, args)
      return result
    } catch (error) {
      throw error
    } finally {
      // 锁定时间结束后解锁
      setTimeout(() => {
        isLocked = false
      }, lockTime)
    }
  }
}

/**
 * 请求队列管理器（用于控制并发请求数）
 */
export class RequestQueue {
  constructor(maxConcurrent = 3) {
    this.maxConcurrent = maxConcurrent // 最大并发数
    this.currentRunning = 0 // 当前运行数
    this.queue = [] // 等待队列
  }

  /**
   * 添加请求到队列
   * @param {Function} requestFn - 返回Promise的请求函数
   * @returns {Promise} 请求结果
   */
  enqueue(requestFn) {
    return new Promise((resolve, reject) => {
      this.queue.push({
        requestFn,
        resolve,
        reject
      })
      this._processQueue()
    })
  }

  /**
   * 处理队列
   */
  _processQueue() {
    if (this.queue.length === 0 || this.currentRunning >= this.maxConcurrent) {
      return
    }

    const { requestFn, resolve, reject } = this.queue.shift()
    this.currentRunning++

    requestFn()
      .then(resolve)
      .catch(reject)
      .finally(() => {
        this.currentRunning--
        this._processQueue() // 处理下一个
      })
  }

  /**
   * 清空队列
   */
  clear() {
    this.queue = []
    this.currentRunning = 0
  }
}
