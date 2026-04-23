/**
 * 前端缓存工具类
 * 支持localStorage和内存缓存，带过期时间
 */

const CACHE_PREFIX = 'artisanlink_cache_'

/**
 * 缓存管理器
 */
class CacheManager {
  constructor() {
    this.memoryCache = new Map() // 内存缓存
  }

  /**
   * 生成缓存key
   */
  _generateKey(key) {
    return CACHE_PREFIX + key
  }

  /**
   * 设置localStorage缓存（带过期时间）
   * @param {string} key - 缓存键
   * @param {any} value - 缓存值
   * @param {number} expire - 过期时间（毫秒），默认30分钟
   */
  setLocalStorage(key, value, expire = 30 * 60 * 1000) {
    const cacheKey = this._generateKey(key)
    const data = {
      value,
      expire: Date.now() + expire,
      timestamp: Date.now()
    }
    
    try {
      localStorage.setItem(cacheKey, JSON.stringify(data))
    } catch (error) {
      console.error('localStorage存储失败:', error)
    }
  }

  /**
   * 获取localStorage缓存
   * @param {string} key - 缓存键
   * @returns {any|null} 缓存值，过期或不存在返回null
   */
  getLocalStorage(key) {
    const cacheKey = this._generateKey(key)
    
    try {
      const raw = localStorage.getItem(cacheKey)
      if (!raw) return null
      
      const data = JSON.parse(raw)
      
      // 检查是否过期
      if (Date.now() > data.expire) {
        this.removeLocalStorage(key)
        return null
      }
      
      return data.value
    } catch (error) {
      console.error('localStorage读取失败:', error)
      return null
    }
  }

  /**
   * 删除localStorage缓存
   */
  removeLocalStorage(key) {
    const cacheKey = this._generateKey(key)
    localStorage.removeItem(cacheKey)
  }

  /**
   * 清空所有localStorage缓存
   */
  clearAllLocalStorage() {
    Object.keys(localStorage).forEach(key => {
      if (key.startsWith(CACHE_PREFIX)) {
        localStorage.removeItem(key)
      }
    })
  }

  /**
   * 设置内存缓存（带过期时间）
   * @param {string} key - 缓存键
   * @param {any} value - 缓存值
   * @param {number} expire - 过期时间（毫秒），默认5分钟
   */
  setMemory(key, value, expire = 5 * 60 * 1000) {
    const cacheKey = this._generateKey(key)
    
    this.memoryCache.set(cacheKey, {
      value,
      expire: Date.now() + expire,
      timer: setTimeout(() => {
        this.removeMemory(key)
      }, expire)
    })
  }

  /**
   * 获取内存缓存
   * @param {string} key - 缓存键
   * @returns {any|null} 缓存值，过期或不存在返回null
   */
  getMemory(key) {
    const cacheKey = this._generateKey(key)
    const data = this.memoryCache.get(cacheKey)
    
    if (!data) return null
    
    // 检查是否过期
    if (Date.now() > data.expire) {
      this.removeMemory(key)
      return null
    }
    
    return data.value
  }

  /**
   * 删除内存缓存
   */
  removeMemory(key) {
    const cacheKey = this._generateKey(key)
    const data = this.memoryCache.get(cacheKey)
    
    if (data) {
      clearTimeout(data.timer)
      this.memoryCache.delete(cacheKey)
    }
  }

  /**
   * 清空所有内存缓存
   */
  clearAllMemory() {
    this.memoryCache.forEach(data => {
      clearTimeout(data.timer)
    })
    this.memoryCache.clear()
  }

  /**
   * 清空所有缓存
   */
  clearAll() {
    this.clearAllLocalStorage()
    this.clearAllMemory()
  }
}

// 创建单例
const cacheManager = new CacheManager()

export default cacheManager
