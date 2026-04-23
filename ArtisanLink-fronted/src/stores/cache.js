import { defineStore } from 'pinia'
import { ref } from 'vue'
import { cacheManager } from '../utils/cache'

export const useCacheStore = defineStore('cache', () => {
  // 技师列表缓存
  const technicianCache = ref({})
  
  // 服务分类缓存
  const serviceCategoryCache = ref({})
  
  // 门店信息缓存
  const merchantCache = ref({})
  
  // 获取技师列表（带缓存）
  function getTechnicianList(key, fetchFn) {
    const cached = cacheManager.get(`technician_${key}`)
    if (cached) {
      return Promise.resolve(cached)
    }
    
    return fetchFn().then(data => {
      cacheManager.set(`technician_${key}`, data, 30 * 60 * 1000) // 30分钟过期
      return data
    })
  }
  
  // 获取服务分类（带缓存）
  function getServiceCategories(fetchFn) {
    const cached = cacheManager.get('service_categories')
    if (cached) {
      return Promise.resolve(cached)
    }
    
    return fetchFn().then(data => {
      cacheManager.set('service_categories', data, 30 * 60 * 1000)
      return data
    })
  }
  
  // 获取门店信息（带缓存）
  function getMerchantInfo(merchantId, fetchFn) {
    const cached = cacheManager.get(`merchant_${merchantId}`)
    if (cached) {
      return Promise.resolve(cached)
    }
    
    return fetchFn().then(data => {
      cacheManager.set(`merchant_${merchantId}`, data, 30 * 60 * 1000)
      return data
    })
  }
  
  // 清除所有缓存
  function clearAllCache() {
    cacheManager.clear()
    technicianCache.value = {}
    serviceCategoryCache.value = {}
    merchantCache.value = {}
  }
  
  return {
    technicianCache,
    serviceCategoryCache,
    merchantCache,
    getTechnicianList,
    getServiceCategories,
    getMerchantInfo,
    clearAllCache
  }
})