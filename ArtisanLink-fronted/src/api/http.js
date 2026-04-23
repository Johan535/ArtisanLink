import axios from 'axios'
import { clearAdminInfo, clearToken, getToken } from '../utils/auth'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8124/api',
  timeout: 10000, // 超时时间5s
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求计数器（用于管理多个请求的loading状态）
let requestCount = 0
let loadingElement = null

// 显示loading
function showLoading() {
  if (requestCount === 0) {
    loadingElement = document.createElement('div')
    loadingElement.className = 'global-loading'
    loadingElement.innerHTML = '<div class="loading-spinner"></div>'
    document.body.appendChild(loadingElement)
  }
  requestCount++
}

// 隐藏loading
function hideLoading() {
  requestCount--
  if (requestCount <= 0) {
    requestCount = 0
    if (loadingElement) {
      document.body.removeChild(loadingElement)
      loadingElement = null
    }
  }
}

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 可选：开启全局loading（默认关闭，需要时在config中设置showLoading: true）
    if (config.showLoading) {
      showLoading()
    }
    
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    if (error.config?.showLoading) {
      hideLoading()
    }
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    // 隐藏loading
    if (response.config.showLoading) {
      hideLoading()
    }
    
    const payload = response.data
    
    // 统一处理业务错误码
    if (typeof payload?.code !== 'undefined') {
      if (payload.code === 200) {
        return payload // 成功直接返回
      } else if (payload.code === 401) {
        // 未登录或token过期
        clearToken()
        clearAdminInfo()
        window.location.href = '/login'
        return Promise.reject(new Error(payload.msg || '未登录，请重新登录'))
      } else if (payload.code === 403) {
        // 无权限
        return Promise.reject(new Error(payload.msg || '无权限访问'))
      } else {
        // 其他业务错误
        return Promise.reject(new Error(payload.msg || '请求失败'))
      }
    }
    
    return payload
  },
  (error) => {
    // 隐藏loading
    if (error.config?.showLoading) {
      hideLoading()
    }
    
    // 处理HTTP错误状态码
    if (error.response) {
      const status = error.response.status
      switch (status) {
        case 401:
          clearToken()
          clearAdminInfo()
          window.location.href = '/login'
          break
        case 403:
          console.error('无权限访问')
          break
        case 404:
          console.error('请求资源不存在')
          break
        case 500:
          console.error('服务器内部错误')
          break
        default:
          console.error(`网络错误: ${status}`)
      }
    } else if (error.code === 'ECONNABORTED') {
      // 超时
      console.error('请求超时，请检查网络连接')
    } else {
      console.error('网络异常，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

export default service
