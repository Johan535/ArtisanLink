import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/DashboardView.vue'
import MerchantView from '../views/MerchantView.vue'
import StaffView from '../views/StaffView.vue'
import ServiceView from '../views/ServiceView.vue'
import OrderView from '../views/OrderView.vue'
import CustomerView from '../views/CustomerView.vue'
import StatisticsView from '../views/StatisticsView.vue'
import { getToken } from '../utils/auth'

const routes = [
  { path: '/login', name: 'login', component: LoginView },
  {
    path: '/',
    redirect: '/dashboard'
  },
  { path: '/dashboard', name: 'dashboard', component: DashboardView, meta: { auth: true } },
  { path: '/merchants', name: 'merchants', component: MerchantView, meta: { auth: true } },
  { path: '/staff', name: 'staff', component: StaffView, meta: { auth: true } },
  { path: '/services', name: 'services', component: ServiceView, meta: { auth: true } },
  { path: '/orders', name: 'orders', component: OrderView, meta: { auth: true } },
  { path: '/customers', name: 'customers', component: CustomerView, meta: { auth: true } },
  { path: '/statistics', name: 'statistics', component: StatisticsView, meta: { auth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.auth && !getToken()) {
    return '/login'
  }
  if (to.path === '/login' && getToken()) {
    return '/dashboard'
  }
  return true
})

export default router
