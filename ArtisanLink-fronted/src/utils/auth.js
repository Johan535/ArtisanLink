/**
 * 认证工具类
 * 处理token和用户信息的存储与获取
 */

const TOKEN_KEY = 'artisan_token'
const USER_INFO_KEY = 'artisan_user_info'
const ADMIN_INFO_KEY = 'artisan_admin_info'

/**
 * 设置Token
 * @param {string} token - JWT Token
 */
export function setToken(token) {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token)
  }
}

/**
 * 获取Token
 * @returns {string|null} Token字符串
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 移除Token
 */
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

/**
 * 设置用户信息（通用）
 * @param {Object} userInfo - 用户信息对象
 */
export function setUserInfo(userInfo) {
  if (userInfo) {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo))
  }
}

/**
 * 获取用户信息（通用）
 * @returns {Object|null} 用户信息对象
 */
export function getUserInfo() {
  const info = localStorage.getItem(USER_INFO_KEY)
  return info ? JSON.parse(info) : null
}

/**
 * 移除用户信息
 */
export function removeUserInfo() {
  localStorage.removeItem(USER_INFO_KEY)
}

/**
 * 设置管理员信息（兼容旧版）
 * @param {Object} adminInfo - 管理员信息对象
 */
export function setAdminInfo(adminInfo) {
  if (adminInfo) {
    localStorage.setItem(ADMIN_INFO_KEY, JSON.stringify(adminInfo))
    // 同时保存到通用用户信息
    setUserInfo({ ...adminInfo, role: 'merchant', type: 'merchant' })
  }
}

/**
 * 获取管理员信息（兼容旧版）
 * @returns {Object|null} 管理员信息对象
 */
export function getAdminInfo() {
  const info = localStorage.getItem(ADMIN_INFO_KEY)
  return info ? JSON.parse(info) : null
}

/**
 * 检查是否已登录
 * @returns {boolean}
 */
export function isLoggedIn() {
  return !!getToken()
}

/**
 * 获取用户角色
 * @returns {string|null} 'merchant' | 'customer' | null
 */
export function getUserRole() {
  const userInfo = getUserInfo()
  return userInfo?.role || null
}

/**
 * 清除所有认证信息
 */
export function clearAuth() {
  removeToken()
  removeUserInfo()
  localStorage.removeItem(ADMIN_INFO_KEY)
}