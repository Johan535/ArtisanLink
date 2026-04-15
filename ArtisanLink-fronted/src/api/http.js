import axios from 'axios'
import { clearAdminInfo, clearToken, getToken } from '../utils/auth'

const service = axios.create({
  baseURL: 'http://localhost:8124/api',
  timeout: 10000
})

service.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (typeof payload?.code !== 'undefined' && payload.code !== 200) {
      return Promise.reject(new Error(payload.msg || '请求失败'))
    }
    return payload
  },
  (error) => {
    if (error.response?.status === 401) {
      clearToken()
      clearAdminInfo()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default service
