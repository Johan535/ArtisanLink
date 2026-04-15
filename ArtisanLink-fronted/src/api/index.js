import http from './http'

export const adminApi = {
  login: (data) => http.post('/admin/login', data),
  logout: () => http.post('/admin/logout'),
  getMerchants: (params) => http.get('/admin/merchant/list', { params }),
  getMerchantDetail: (id) => http.get(`/admin/merchant/${id}`),
  saveMerchant: (data) => http.post('/admin/merchant/save', data),
  updateMerchant: (data) => http.put('/admin/merchant/update', data),
  getStaff: (params) => http.get('/admin/staff/list', { params }),
  saveStaff: (data) => http.post('/admin/staff/save', data),
  getServiceCategories: (params) => http.get('/admin/service/category/list', { params }),
  getServices: (params) => http.get('/admin/service/list', { params }),
  getOrders: (params) => http.get('/admin/order/list', { params }),
  getOrderDetail: (id) => http.get(`/admin/order/${id}`),
  getCustomers: (params) => http.get('/admin/customer/list', { params }),
  getStatistics: (params) => http.get('/admin/statistics/merchant', { params })
}
