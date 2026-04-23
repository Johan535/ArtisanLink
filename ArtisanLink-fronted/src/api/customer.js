import { request } from './http'

const get = (url, params) => request({ url, method: 'get', params })
const post = (url, data) => request({ url, method: 'post', data })
const put = (url, data) => request({ url, method: 'put', data })

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
    return post('/customer/login', { phone, code })
  },
  
  /**
   * 发送短信验证码
   * @param {string} phone - 手机号
   */
  sendSmsCode(phone) {
    return post('/customer/sms/send', { phone })
  },
  
  /**
   * 获取服务分类列表
   */
  getServiceCategories() {
    return get('/customer/service/categories')
  },

  /**
   * 获取服务列表
   * @param {Object} params - 查询参数
   */
  getServiceList(params) {
    return get('/customer/service/list', params)
  },
  
  /**
   * 获取附近技师列表（基于GEO）
   * @param {Object} params - 查询参数
   */
  getNearbyTechnicians(params) {
    return get('/customer/technician/nearby', params)
  },
  
  /**
   * 获取技师详情
   * @param {number} id - 技师ID
   */
  getTechnicianDetail(id) {
    return get(`/customer/technician/${id}`)
  },
  
  /**
   * 获取技师可预约时间片
   * @param {number} technicianId - 技师ID
   * @param {string} date - 日期 YYYY-MM-DD
   */
  getAvailableTimeSlots(technicianId, date) {
    // 兼容旧调用：getAvailableTimeSlots({ technicianId, date })
    if (typeof technicianId === 'object' && technicianId !== null) {
      return get('/customer/technician/timeslots', technicianId)
    }
    return get(`/customer/technician/${technicianId}/timeslots`, { date })
  },
  
  /**
   * 创建预约订单
   * @param {Object} data - 预约数据
   */
  createBooking(data) {
    return post('/customer/order/create', data)
  },
  
  /**
   * 获取用户订单列表
   * @param {Object} params - 查询参数
   */
  getOrderList(params) {
    return get('/customer/order/list', params)
  },
  
  /**
   * 获取订单详情
   * @param {number} id - 订单ID
   */
  getOrderDetail(id) {
    return get(`/customer/order/${id}`)
  },

  /**
   * 创建评价
   * @param {Object} data - 评价数据
   */
  createReview(data) {
    return post('/customer/review/create', data)
  },

  /**
   * 更新评价
   * @param {Object} data - 评价数据
   */
  updateReview(data) {
    return put('/customer/review/update', data)
  },

  /**
   * 获取评价详情
   * @param {number} reviewId - 评价ID
   */
  getReviewDetail(reviewId) {
    return get(`/customer/review/${reviewId}`)
  },
  
  /**
   * 取消订单
   * @param {number} id - 订单ID
   */
  cancelOrder(id) {
    return post(`/customer/order/${id}/cancel`)
  },
  
  /**
   * 获取用户个人信息
   */
  getUserProfile() {
    return get('/customer/profile')
  },
  
  /**
   * 更新用户个人信息
   * @param {Object} data - 用户信息
   */
  updateProfile(data) {
    return put('/customer/profile', data)
  },
  
  /**
   * 获取用户消息列表
   * @param {Object} params - 查询参数
   */
  getMessageList(params) {
    return get('/customer/message/list', params)
  },
  
  /**
   * 标记消息已读
   * @param {number} id - 消息ID
   */
  markMessageRead(id) {
    return post(`/customer/message/${id}/read`)
  },
  
  /**
   * 获取未读消息数量
   */
  getUnreadMessageCount() {
    return get('/customer/message/unread-count')
  },

  // 获取案例列表
  getCaseList(params) {
    return get('/customer/case/list', params)
  },
  
  // 获取案例详情
  getCaseDetail(caseId) {
    return get(`/customer/case/${caseId}`)
  },
  
  // 增加案例浏览量
  incrementCaseView(caseId) {
    return post(`/customer/case/${caseId}/view`)
  },
  
  // 点赞/取消点赞案例
  toggleCaseLike(caseId) {
    return post(`/customer/case/${caseId}/like`)
  },
  
  // 获取案例评论列表
  getCaseComments(caseId, params) {
    return get(`/customer/case/${caseId}/comments`, params)
  },
  
  // 添加案例评论
  addCaseComment(caseId, data) {
    return post(`/customer/case/${caseId}/comments`, data)
  },
  
  // 点赞/取消点赞评论
  toggleCommentLike(commentId) {
    return post(`/customer/comment/${commentId}/like`)
  },

  // ===== 向下兼容旧页面方法名 =====
  getMessages(params) {
    return this.getMessageList(params)
  },
  updateUserProfile(data) {
    return this.updateProfile(data)
  },
  getMyOrders(params) {
    return this.getOrderList(params)
  },
  submitReview(data) {
    return this.createReview(data)
  },
  getReviewList(params) {
    return get('/customer/review/list', params)
  },
  getServiceCategoryList(params) {
    return get('/customer/service/categories', params)
  },
  searchTechnicians(params) {
    return get('/customer/technician/list', params)
  },
  submitAppointment(data) {
    return this.createBooking(data)
  }
}

export default customerApi