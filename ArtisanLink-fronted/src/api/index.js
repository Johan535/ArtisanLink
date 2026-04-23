/**
 * B端管理员API接口
 */
import { request } from './http'

export const adminApi = {
  /**
   * 管理员登录
   * @param {Object} data - 登录表单数据
   */
  login(data) {
    return request({
      url: '/admin/login',
      method: 'post',
      data
    })
  },
  
  // 获取技师列表
  getStaffList(params) {
    return http.get('/admin/staff/list', { params })
  },
  
  // 获取技师排班
  getTechnicianSchedule(params) {
    return http.get('/admin/schedule/technician', { params })
  },
  
  // 更新技师排班
  updateTechnicianSchedule(data) {
    return http.put('/admin/schedule/update', data)
  },
  
  // 批量设置排班
  batchSetSchedule(data) {
    return http.post('/admin/schedule/batch-set', data)
  },
  
  // 新增技师
  saveStaff(data) {
    return http.post('/admin/staff/save', data)
  },
  
  // 更新技师
  updateStaff(id, data) {
    return http.put(`/admin/staff/update/${id}`, data)
  },
  
  // 删除技师
  deleteStaff(id) {
    return http.delete(`/admin/staff/delete/${id}`)
  },
  
  // 获取时间片列表
  getTimeSlots(staffId, date) {
    return http.get(`/admin/appointment/time-slots`, {
      params: { staffId, date }
    })
  },
  
  // 批量创建时间片
  batchCreateTimeSlots(data) {
    return http.post('/admin/appointment/time-slots/batch', data)
  },
  
  // 禁用时间片
  disableTimeSlot(id) {
    return http.put(`/admin/appointment/time-slot/disable/${id}`)
  },
  
  // 启用时间片
  enableTimeSlot(id) {
    return http.put(`/admin/appointment/time-slot/enable/${id}`)
  },
  
  /**
   * 设置技师排班
   * @param {number} staffId - 技师ID
   * @param {Object} data - 排班数据
   */
  setStaffSchedule(staffId, data) {
    return request({
      url: `/admin/staff/${staffId}/schedule`,
      method: 'post',
      data
    })
  },
  
  /**
   * 获取服务列表
   * @param {Object} params - 查询参数
   */
  getServiceList(params) {
    return request({
      url: '/admin/service/list',
      method: 'get',
      params
    })
  },
  
  /**
   * 新增服务
   * @param {Object} data - 服务信息
   */
  saveService(data) {
    return request({
      url: '/admin/service/save',
      method: 'post',
      data
    })
  },
  
  /**
   * 更新服务信息
   * @param {number} id - 服务ID
   * @param {Object} data - 服务信息
   */
  updateService(id, data) {
    return request({
      url: `/admin/service/${id}`,
      method: 'put',
      data
    })
  },
  
  /**
   * 删除服务
   * @param {number} id - 服务ID
   */
  deleteService(id) {
    return request({
      url: `/admin/service/${id}`,
      method: 'delete'
    })
  },
  
  /**
   * 获取订单列表
   * @param {Object} params - 查询参数
   */
  getOrderList(params) {
    return request({
      url: '/admin/order/list',
      method: 'get',
      params
    })
  },
  
  /**
   * 获取订单详情
   * @param {number} id - 订单ID
   */
  getOrderDetail(id) {
    return request({
      url: `/admin/order/${id}`,
      method: 'get'
    })
  },
  
  /**
   * 接单
   * @param {number} id - 订单ID
   */
  acceptOrder(id) {
    return request({
      url: `/admin/order/${id}/accept`,
      method: 'post'
    })
  },
  
  /**
   * 拒绝订单
   * @param {number} id - 订单ID
   * @param {string} reason - 拒绝原因
   */
  rejectOrder(id, reason) {
    return request({
      url: `/admin/order/${id}/reject`,
      method: 'post',
      data: { reason }
    })
  },
  
  /**
   * 完成订单
   * @param {number} id - 订单ID
   */
  completeOrder(id) {
    return request({
      url: `/admin/order/${id}/complete`,
      method: 'post'
    })
  },
  
  /**
   * 获取客户列表
   * @param {Object} params - 查询参数
   */
  getCustomerList(params) {
    return request({
      url: '/admin/customer/list',
      method: 'get',
      params
    })
  },
  
  // 获取统计数据
  getStatistics(params) {
    return http.get('/admin/statistics/merchant', { params })
  },
  
  // 获取消息列表
  getMessageList(params) {
    return http.get('/admin/message/list', { params })
  },
  
  // 标记消息为已读
  markMessageAsRead(messageId) {
    return http.put(`/admin/message/${messageId}/read`)
  },
  
  // 全部标记为已读
  markAllMessagesAsRead() {
    return http.put('/admin/message/read-all')
  },
  
  // 清空所有消息
  clearAllMessages() {
    return http.delete('/admin/message/clear')
  },
  
  /**
   * 获取门店统计数据
   * @param {Object} params - 查询参数
   */
  getMerchantStatistics(params) {
    return request({
      url: '/admin/statistics/merchant',
      method: 'get',
      params
    })
  },
  
  /**
   * 获取营收趋势数据
   * @param {Object} params - 查询参数
   */
  getRevenueTrend(params) {
    return request({
      url: '/admin/statistics/revenue-trend',
      method: 'get',
      params
    })
  },
  
  /**
   * 获取订单来源统计
   */
  getOrderSourceStats() {
    return request({
      url: '/admin/statistics/order-source',
      method: 'get'
    })
  },
  
  /**
   * 获取门店信息
   */
  getMerchantInfo() {
    return request({
      url: '/admin/merchant/info',
      method: 'get'
    })
  },
  
  /**
   * 更新门店信息
   * @param {Object} data - 门店信息
   */
  updateMerchantInfo(data) {
    return request({
      url: '/admin/merchant/info',
      method: 'put',
      data
    })
  }
}

// C端用户端API
export const customerApi = {
  // 首页数据
  getHomeData: (params) => http.get('/customer/home', { params }),
  getNearbyTechnicians: (params) => http.get('/customer/technician/nearby', { params }),
  
  // 技师相关
  getTechnicianList: (params) => http.get('/customer/technician/list', { params }),
  getTechnicianDetail: (id) => http.get(`/customer/technician/${id}`),
  getTechnicianTimeSlots: (params) => http.get('/customer/technician/timeslots', { params }),
  
  // 预约下单
  createBooking: (data) => http.post('/customer/booking/create', data),
  checkTimeSlot: (data) => http.post('/customer/booking/check', data),
  
  // 订单管理
  getOrderList: (params) => http.get('/customer/order/list', { params }),
  getOrderDetail: (id) => http.get(`/customer/order/${id}`),
  cancelOrder: (id) => http.put(`/customer/order/${id}/cancel`),
  
  // 评价
  submitReview: (data) => http.post('/customer/review/submit', data),
  
  // 消息中心
  getMessages: (params) => http.get('/customer/message/list', { params }),
  markMessageRead: (id) => http.put(`/customer/message/${id}/read`),
  
  // 个人中心
  getUserProfile: () => http.get('/customer/profile'),
  updateUserProfile: (data) => http.put('/customer/profile', data)
}