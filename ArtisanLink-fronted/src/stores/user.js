import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authUtils } from '../utils/auth'

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref(authUtils.getUserInfo())
  const token = ref(authUtils.getToken())
  
  // 是否已登录
  const isLoggedIn = computed(() => !!token.value)
  
  // 用户角色 (customer: C端用户, merchant: B端店长)
  const userRole = computed(() => userInfo.value?.role || 'customer')
  
  // 设置用户信息
  function setUserInfo(info) {
    userInfo.value = info
    authUtils.setUserInfo(info)
  }
  
  // 设置Token
  function setToken(newToken) {
    token.value = newToken
    authUtils.setToken(newToken)
  }
  
  // 登录
  function login(userData) {
    setToken(userData.token)
    setUserInfo(userData.userInfo)
  }
  
  // 登出
  function logout() {
    token.value = ''
    userInfo.value = null
    authUtils.clearAuth()
  }
  
  return {
    userInfo,
    token,
    isLoggedIn,
    userRole,
    setUserInfo,
    setToken,
    login,
    logout
  }
})