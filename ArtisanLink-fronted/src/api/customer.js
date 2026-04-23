import { request } from './http'

/**
 * C端用户API接口
 */
export const customerApi = {
  /**
   * 用户手机号验证码登录/注册
   * @param {string} phone - 手机号
   * @param {string} code - 验证码
   */
  loginWithCode(phone, code) {
    return request({
      url: '/customer/login',
      method: 'post',
      data: { phone, code }
    })
  },
  
  /**
   * 发送短信验证码
   * @param {string} phone - 手机号
   */
  sendSmsCode(phone) {
    return request({
      url: '/customer/sms/send',
      method: 'post',
      data: { phone }
    })
  },
  
  /**
   * 获取服务分类列表
   */
  getServiceCategories() {
    return request({
      url: '/customer/service/categories',
      method: 'get'
    })
  },

  /**
   * 获取服务列表
   * @param {Object} params - 查询参数
   */
  getServiceList(params) {
    return request({
      url: '/customer/service/list',
      method: 'get',
      params
    })
  },
  
  /**
   * 获取附近技师列表（基于GEO）
   * @param {Object} params - 查询参数
   */
  getNearbyTechnicians(params) {
    return request({
      url: '/customer/technician/nearby',
      method: 'get',
      params
    })
  },
  
  /**
   * 获取技师详情
   * @param {number} id - 技师ID
   */
  getTechnicianDetail(id) {
    return request({
      url: `/customer/technician/${id}`,
      method: 'get'
    })
  },
  
  /**
   * 获取技师可预约时间片
   * @param {number} technicianId - 技师ID
   * @param {string} date - 日期 YYYY-MM-DD
   */
  getAvailableTimeSlots(technicianId, date) {
    return request({
      url: `/customer/technician/${technicianId}/timeslots`,
      method: 'get',
      params: { date }
    })
  },
  
  /**
   * 创建预约订单
   * @param {Object} data - 预约数据
   */
  createBooking(data) {
    return request({
      url: '/customer/order/create',
      method: 'post',
      data
    })
  },
  
  /**
   * 获取用户订单列表
   * @param {Object} params - 查询参数
   */
  getOrderList(params) {
    return request({
      url: '/customer/order/list',
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
      url: `/customer/order/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建评价
   * @param {Object} data - 评价数据
   */
  createReview(data) {
    return request({
      url: '/customer/review/create',
      method: 'post',
      data
    })
  },

  /**
   * 更新评价
   * @param {Object} data - 评价数据
   */
  updateReview(data) {
    return request({
      url: '/customer/review/update',
      method: 'put',
      data
    })
  },

  /**
   * 获取评价详情
   * @param {number} reviewId - 评价ID
   */
  getReviewDetail(reviewId) {
    return request({
      url: `/customer/review/${reviewId}`,
      method: 'get'
    })
  },
  
  /**
   * 取消订单
   * @param {number} id - 订单ID
   */
  cancelOrder(id) {
    return request({
      url: `/customer/order/${id}/cancel`,
      method: 'post'
    })
  },
  
  /**
   * 获取用户个人信息
   */
  getUserProfile() {
    return request({
      url: '/customer/profile',
      method: 'get'
    })
  },
  
  /**
   * 更新用户个人信息
   * @param {Object} data - 用户信息
   */
  updateProfile(data) {
    return request({
      url: '/customer/profile',
      method: 'put',
      data
    })
  },
  
  /**
   * 获取用户消息列表
   * @param {Object} params - 查询参数
   */
  getMessageList(params) {
    return request({
      url: '/customer/message/list',
      method: 'get',
      params
    })
  },
  
  /**
   * 标记消息已读
   * @param {number} id - 消息ID
   */
  markMessageRead(id) {
    return request({
      url: `/customer/message/${id}/read`,
      method: 'post'
    })
  },
  
  /**
   * 获取未读消息数量
   */
  getUnreadMessageCount() {
    return request({
      url: '/customer/message/unread-count',
      method: 'get'
    })
  },

  // 获取案例列表
  getCaseList(params) {
    return http.get('/customer/case/list', { params })
  },
  
  // 获取案例详情
  getCaseDetail(caseId) {
    return http.get(`/customer/case/${caseId}`)
  },
  
  // 增加案例浏览量
  incrementCaseView(caseId) {
    return http.post(`/customer/case/${caseId}/view`)
  },
  
  // 点赞/取消点赞案例
  toggleCaseLike(caseId) {
    return http.post(`/customer/case/${caseId}/like`)
  },
  
  // 获取案例评论列表
  getCaseComments(caseId, params) {
    return http.get(`/customer/case/${caseId}/comments`, { params })
  },
  
  // 添加案例评论
  addCaseComment(caseId, data) {
    return http.post(`/customer/case/${caseId}/comments`, data)
  },
  
  // 点赞/取消点赞评论
  toggleCommentLike(commentId) {
    return http.post(`/customer/comment/${commentId}/like`)
  }
}

export default customerApi