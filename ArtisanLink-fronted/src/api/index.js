/**
 * B端管理员API接口（向下兼容历史方法名）
 */
import { request } from './http'
export { customerApi } from './customer'

const get = (url, params) => request({ url, method: 'get', params })
const post = (url, data) => request({ url, method: 'post', data })
const put = (url, data) => request({ url, method: 'put', data })
const del = (url) => request({ url, method: 'delete' })

export const adminApi = {
  login(data) {
    return post('/admin/login', data)
  },
  getCaptcha() {
    return get('/admin/captcha')
  },
  logout() {
    return post('/admin/logout')
  },

  // 商户
  getMerchantInfo() {
    return get('/admin/merchant/info')
  },
  updateMerchantInfo(data) {
    return put('/admin/merchant/info', data)
  },
  getMerchants(params) {
    return get('/admin/merchant/list', params)
  },
  saveMerchant(data) {
    return post('/admin/merchant/save', data)
  },
  updateMerchant(data) {
    return put('/admin/merchant/update', data)
  },
  deleteMerchant(id) {
    return del(`/admin/merchant/${id}`)
  },

  // 员工
  getStaffList(params) {
    return get('/admin/staff/list', params)
  },
  saveStaff(data) {
    return post('/admin/staff/save', data)
  },
  updateStaff(id, data) {
    // 兼容旧调用: updateStaff(data)
    if (typeof id === 'object' && id !== null) {
      return put(`/admin/staff/update/${id.id}`, id)
    }
    return put(`/admin/staff/update/${id}`, data)
  },
  deleteStaff(id) {
    return del(`/admin/staff/delete/${id}`)
  },

  // 排班与时间片（后端待完善）
  getTechnicianSchedule(params) {
    return get('/admin/schedule/technician', params)
  },
  updateTechnicianSchedule(data) {
    return put('/admin/schedule/update', data)
  },
  batchSetSchedule(data) {
    return post('/admin/schedule/batch-set', data)
  },
  getTimeSlots(staffId, date) {
    return get('/admin/appointment/time-slots', { staffId, date })
  },
  batchCreateTimeSlots(data) {
    return post('/admin/appointment/time-slots/batch', data)
  },
  disableTimeSlot(id) {
    return put(`/admin/appointment/time-slot/disable/${id}`)
  },
  enableTimeSlot(id) {
    return put(`/admin/appointment/time-slot/enable/${id}`)
  },

  // 服务
  getServiceList(params) {
    return get('/admin/service/list', params)
  },
  saveService(data) {
    return post('/admin/service/save', data)
  },
  updateService(id, data) {
    return put(`/admin/service/${id}`, data)
  },
  deleteService(id) {
    return del(`/admin/service/${id}`)
  },
  getServiceCategories(params) {
    return get('/admin/service/category/list', params)
  },

  // 订单
  getOrderList(params) {
    return get('/admin/order/list', params)
  },
  saveOrder(data) {
    return post('/admin/order/save', data)
  },
  updateOrder(data) {
    return put('/admin/order/update', data)
  },
  deleteOrder(id) {
    return del(`/admin/order/${id}`)
  },
  getOrderDetail(id) {
    return get(`/admin/order/${id}`)
  },
  acceptOrder(id) {
    return post(`/admin/order/${id}/accept`)
  },
  rejectOrder(id, reason) {
    return post(`/admin/order/${id}/reject`, { reason })
  },
  completeOrder(id) {
    return post(`/admin/order/${id}/complete`)
  },

  // 客户
  getCustomerList(params) {
    return get('/admin/customer/list', params)
  },
  saveCustomer(data) {
    return post('/admin/customer/save', data)
  },
  updateCustomer(data) {
    return put('/admin/customer/update', data)
  },
  deleteCustomer(id) {
    return del(`/admin/customer/${id}`)
  },

  // 统计
  getStatistics(params) {
    return get('/admin/statistics/merchant', params)
  },
  getMerchantStatistics(params) {
    return get('/admin/statistics/merchant', params)
  },
  getRevenueTrend(params) {
    return get('/admin/statistics/revenue-trend', params)
  },
  getOrderSourceStats() {
    return get('/admin/statistics/order-source')
  },

  // 消息（后端待完善）
  getMessageList(params) {
    return get('/admin/message/list', params)
  },
  markMessageAsRead(messageId) {
    return put(`/admin/message/${messageId}/read`)
  },
  markAllMessagesAsRead() {
    return put('/admin/message/read-all')
  },
  clearAllMessages() {
    return del('/admin/message/clear')
  },

  // ===== 向下兼容旧页面方法名 =====
  getStaff(params) {
    return this.getStaffList(params)
  },
  createStaff(data) {
    return this.saveStaff(data)
  },
  getStaffTimeSlots(params) {
    return get('/admin/appointment/time-slots', params)
  },
  batchUpdateTimeSlots(data) {
    return post('/admin/appointment/time-slots/batch', data)
  },
  getServices(params) {
    return this.getServiceList(params)
  },
  getOrders(params) {
    return this.getOrderList(params)
  },
  getCustomers(params) {
    return this.getCustomerList(params)
  }
}