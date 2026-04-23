import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/DashboardView.vue'
import StaffView from '../views/StaffView.vue'
import ServiceView from '../views/ServiceView.vue'
import OrderView from '../views/OrderView.vue'
import CustomerView from '../views/CustomerView.vue'
import StatisticsView from '../views/StatisticsView.vue'
import MerchantView from '../views/MerchantView.vue'

// C端用户端页面
import CustomerLayout from '../components/CustomerLayout.vue'
import HomeView from '../views/customer/HomeView.vue'
import TechnicianListView from '../views/customer/TechnicianListView.vue'
import TechnicianDetailView from '../views/customer/TechnicianDetailView.vue'
import BookingView from '../views/customer/BookingView.vue'
import OrderListView from '../views/customer/OrderListView.vue'
import OrderDetailView from '../views/customer/OrderDetailView.vue'
import ProfileView from '../views/customer/ProfileView.vue'
import MessageView from '../views/customer/MessageView.vue'
import { getToken } from '@/utils/auth'
import AdminLayout from '../components/AdminLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    redirect: '/login'
  },
  // B端管理端路由
  {
    path: '/admin',
    component: () => import('@/components/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/DashboardView.vue'),
        meta: { title: '数据概览' }
      },
      {
        path: 'staff',
        name: 'Staff',
        component: () => import('@/views/StaffView.vue'),
        meta: { title: '技师管理' }
      },
      {
        path: 'schedule',
        name: 'Schedule',
        component: () => import('@/views/ScheduleView.vue'),
        meta: { title: '排班管理' }
      },
      {
        path: 'service',
        name: 'Service',
        component: ServiceView,
        meta: { title: '服务管理' }
      },
      {
        path: 'order',
        name: 'Order',
        component: OrderView,
        meta: { title: '预约管理' }
      },
      {
        path: 'customer',
        name: 'Customer',
        component: CustomerView,
        meta: { title: '客户管理' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/StatisticsView.vue'),
        meta: { title: '数据统计', requiresAuth: true }
      },
      {
        path: 'message',
        name: 'Message',
        component: () => import('@/views/MessageView.vue'),
        meta: { title: '消息中心', requiresAuth: true }
      },
      {
        path: 'merchant',
        name: 'Merchant',
        component: MerchantView,
        meta: { title: '门店管理' }
      }
    ]
  },
  // C端用户端路由
  {
    path: '/customer',
    component: () => import('@/components/CustomerLayout.vue'),
    children: [
      {
        path: 'home',
        name: 'CustomerHome',
        component: () => import('@/views/customer/HomeView.vue'),
        meta: { title: '首页', requiresAuth: false }
      },
      {
        path: 'technicians',
        name: 'TechnicianList',
        component: () => import('@/views/customer/TechnicianListView.vue'),
        meta: { title: '技师列表', requiresAuth: false }
      },
      {
        path: 'services',
        name: 'ServiceList',
        component: () => import('@/views/customer/ServiceListView.vue'),
        meta: { title: '服务列表', requiresAuth: false }
      },
      {
        path: 'categories',
        name: 'ServiceCategory',
        component: () => import('@/views/customer/ServiceCategoryView.vue'),
        meta: { title: '服务分类', requiresAuth: false }
      },
      {
        path: 'search',
        name: 'SearchResult',
        component: () => import('@/views/customer/SearchResultView.vue'),
        meta: { title: '搜索结果', requiresAuth: false }
      },
      {
        path: 'technician/:id',
        name: 'TechnicianDetail',
        component: () => import('@/views/customer/TechnicianDetailView.vue'),
        meta: { title: '技师详情', requiresAuth: true }
      },
      {
        path: 'booking',
        name: 'Booking',
        component: () => import('@/views/customer/BookingView.vue'),
        meta: { title: '预约下单', requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'CustomerOrders',
        component: () => import('@/views/customer/OrderListView.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'order/:id',
        name: 'CustomerOrderDetail',
        component: () => import('@/views/customer/OrderDetailView.vue'),
        meta: { title: '订单详情', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'CustomerProfile',
        component: () => import('@/views/customer/ProfileView.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'review',
        name: 'CustomerReview',
        component: () => import('@/views/customer/ReviewView.vue'),
        meta: { title: '发表评价', requiresAuth: true }
      },
      {
        path: 'messages',
        name: 'CustomerMessages',
        component: () => import('@/views/customer/MessageView.vue'),
        meta: { title: '消息中心', requiresAuth: true }
      },
      {
        path: '/customer/cases',
        name: 'CustomerCaseList',
        component: () => import('@/views/customer/CaseListView.vue'),
        meta: { title: '精选案例', requiresAuth: true }
      },
      {
        path: '/customer/case/:id',
        name: 'CustomerCaseDetail',
        component: () => import('@/views/customer/CaseDetailView.vue'),
        meta: { title: '案例详情', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  
  // 需要登录的页面
  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  
  // 角色验证
  if (to.meta.role) {
    const userRole = userInfo.role
    if (userRole !== to.meta.role) {
      // 角色不匹配，跳转到对应首页
      if (userRole === 'merchant') {
        next('/admin/dashboard')
      } else if (userRole === 'customer') {
        next('/customer/home')
      } else {
        next('/login')
      }
      return
    }
  }
  
  // 已登录用户访问登录页，重定向到对应首页
  if (to.path === '/login' && token) {
    if (userInfo.role === 'merchant') {
      next('/admin/dashboard')
    } else if (userInfo.role === 'customer') {
      next('/customer/home')
    } else {
      next()
    }
    return
  }
  
  next()
})

export default router