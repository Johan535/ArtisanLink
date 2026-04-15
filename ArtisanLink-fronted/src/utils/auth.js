const TOKEN_KEY = 'artisanlink_admin_token'
const ADMIN_KEY = 'artisanlink_admin_info'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getAdminInfo() {
  const raw = localStorage.getItem(ADMIN_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch (error) {
    return null
  }
}

export function setAdminInfo(info) {
  localStorage.setItem(ADMIN_KEY, JSON.stringify(info || {}))
}

export function clearAdminInfo() {
  localStorage.removeItem(ADMIN_KEY)
}
