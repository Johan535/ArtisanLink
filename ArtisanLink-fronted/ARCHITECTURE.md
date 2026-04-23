# 匠心美业O2O平台 - 前端架构说明

## 项目概述

匠心美业O2O平台是一个中高端美业垂直O2O平台，仿大众点评风格，包含C端用户端和B端店长管理端。

### 技术栈

- **框架**: Vue 3.5.18（组合式API）
- **构建工具**: Vite 6.3.5
- **状态管理**: Pinia 2.3.1
- **路由**: Vue Router 4.5.1
- **UI组件库**: Element Plus 2.9.1
- **HTTP客户端**: Axios 1.11.0
- **图表库**: ECharts 5.6.0
- **实时通信**: WebSocket原生API

### 项目结构

```
ArtisanLink-fronted/
├── public/                    # 静态资源
├── src/                       # 源代码
│   ├── api/                   # API接口层
│   │   ├── http.js           # Axios封装
│   │   ├── index.js          # B端API接口
│   │   └── customer.js       # C端API接口
│   ├── components/            # 公共组件
│   │   ├── admin/            # B端专用组件
│   │   │   └── TechnicianSchedule.vue  # 技师排班组件
│   │   ├── customer/         # C端专用组件
│   │   │   ├── SearchBar.vue         # 搜索栏
│   │   │   ├── TechnicianCard.vue    # 技师卡片
│   │   │   ├── TimeSlotPicker.vue    # 时间片选择器
│   │   │   ├── ReviewList.vue        # 评价列表
│   │   │   └── CaseShowcase.vue      # 案例展示
│   │   ├── AdminLayout.vue   # B端布局
│   │   ├── CustomerLayout.vue # C端布局
│   │   └── NotificationBell.vue # 消息铃铛
│   ├── router/               # 路由配置
│   │   └── index.js
│   ├── stores/               # Pinia状态管理
│   │   ├── user.js           # 用户状态
│   │   ├── order.js          # 订单状态
│   │   └── cache.js          # 缓存状态
│   ├── utils/                # 工具类
│   │   ├── auth.js           # 认证工具
│   │   ├── cache.js          # 缓存工具
│   │   ├── helper.js         # 辅助函数
│   │   ├── websocket.js      # WebSocket封装
│   │   └── wsHandler.js      # WebSocket消息处理
│   ├── views/                # 页面视图
│   │   ├── customer/         # C端页面
│   │   │   ├── HomeView.vue           # 首页
│   │   │   ├── TechnicianListView.vue # 技师列表
│   │   │   ├── TechnicianDetailView.vue # 技师详情
│   │   │   ├── BookingView.vue        # 预约下单
│   │   │   ├── OrderListView.vue      # 订单列表
│   │   │   ├── OrderDetailView.vue    # 订单详情
│   │   │   ├── ProfileView.vue        # 个人中心
│   │   │   ├── MessageView.vue        # 消息中心
│   │   │   ├── ReviewView.vue         # 我的评价
│   │   │   ├── ServiceCategoryView.vue # 服务分类
│   │   │   ├── ServiceListView.vue    # 服务列表
│   │   │   ├── SearchResultView.vue   # 搜索结果
│   │   │   ├── CaseListView.vue       # 案例列表
│   │   │   └── CaseDetailView.vue     # 案例详情
│   │   ├── LoginView.vue              # 登录页
│   │   ├── DashboardView.vue          # 仪表盘
│   │   ├── StaffView.vue              # 员工管理
│   │   ├── ServiceView.vue            # 服务管理
│   │   ├── OrderView.vue              # 订单管理
│   │   ├── CustomerView.vue           # 客户管理
│   │   ├── StatisticsView.vue         # 数据统计
│   │   ├── ScheduleView.vue           # 排班管理
│   │   ├── MessageView.vue            # 消息中心
│   │   └── MerchantView.vue           # 商户管理
│   ├── App.vue               # 根组件
│   ├── main.js               # 入口文件
│   └── style.css             # 全局样式
├── .env.development          # 开发环境变量
├── .env.production           # 生产环境变量
├── .eslintrc.js              # ESLint配置
├── .prettierrc               # Prettier配置
├── .gitignore                # Git忽略文件
├── index.html                # HTML模板
├── package.json              # 项目配置
├── vite.config.js            # Vite配置
├── README.md                 # 项目说明
├── DEPLOYMENT.md             # 部署指南
└── API_DOCUMENTATION.md      # API文档
```

## 核心模块说明

### 1. HTTP请求层（src/api/http.js）

**功能特性**:
- 统一的请求拦截器：自动添加token
- 统一的响应拦截器：处理通用错误码
- 401自动跳转登录
- 5秒超时设置
- Loading状态管理

**关键代码**:
```javascript
// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      // 处理错误
      if (res.code === 401) {
        // 未登录，跳转登录页
        ElMessage.error('登录已过期，请重新登录')
        removeToken()
        router.push('/login')
      } else {
        ElMessage.error(res.msg || 'Error')
      }
      return Promise.reject(new Error(res.msg || 'Error'))
    }
    return res
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)
```

### 2. WebSocket实时通信（src/utils/websocket.js）

**功能特性**:
- 自动重连机制（最多5次）
- 心跳保活（30秒间隔）
- 消息分发处理
- 断线提示

**关键代码**:
```javascript
class WebSocketClient {
  connect(shopId, userType = 'merchant') {
    const wsBaseUrl = import.meta.env.VITE_WS_URL
    this.url = `${wsBaseUrl}/ws/${userType}/${shopId}?token=${getToken()}`
    
    this.ws = new WebSocket(this.url)
    this.setupEventListeners()
  }
  
  setupEventListeners() {
    this.ws.onopen = () => this.startHeartbeat()
    this.ws.onmessage = (event) => this.handleMessage(event)
    this.ws.onerror = () => this.handleError()
    this.ws.onclose = () => this.handleClose()
  }
  
  handleMessage(event) {
    const message = JSON.parse(event.data)
    if (message.type === 'HEARTBEAT') {
      this.resetHeartbeat()
      return
    }
    // 触发对应的事件处理器
    this.emit(message.type, message.data)
  }
}
```

### 3. 缓存策略（src/utils/cache.js + src/stores/cache.js）

**功能特性**:
- 内存缓存 + localStorage双缓存
- 30分钟过期策略
- 支持自定义过期时间
- 缓存预热机制

**关键代码**:
```javascript
// 内存缓存
const memoryCache = new Map()

// 获取缓存
export function getCache(key) {
  // 先查内存
  if (memoryCache.has(key)) {
    const item = memoryCache.get(key)
    if (!isExpired(item.expireTime)) {
      return item.data
    }
    memoryCache.delete(key)
  }
  
  // 再查localStorage
  const localData = localStorage.getItem(`cache_${key}`)
  if (localData) {
    const item = JSON.parse(localData)
    if (!isExpired(item.expireTime)) {
      memoryCache.set(key, item)
      return item.data
    }
    localStorage.removeItem(`cache_${key}`)
  }
  
  return null
}

// 设置缓存
export function setCache(key, data, expireMinutes = 30) {
  const expireTime = Date.now() + expireMinutes * 60 * 1000
  const item = { data, expireTime }
  
  memoryCache.set(key, item)
  localStorage.setItem(`cache_${key}`, JSON.stringify(item))
}
```

### 4. 时间片选择器（src/components/customer/TimeSlotPicker.vue）

**功能特性**:
- 防并发重复提交
- 禁用已占用时间片
- 点击后立即禁用
- 冲突提示

**关键代码**:
```javascript
// 选择时间片
const handleSelectSlot = async (slot) => {
  if (slot.status === 1) {
    ElMessage.warning('该时间已被占用，请重新选择')
    return
  }
  
  if (selectingSlotId.value) {
    return // 防止重复点击
  }
  
  selectingSlotId.value = slot.id
  
  try {
    // 调用接口锁定时间片
    await lockTimeSlot(slot.id)
    selectedSlot.value = slot
    ElMessage.success('时间片已锁定')
  } catch (error) {
    ElMessage.error(error.message || '锁定失败')
  } finally {
    selectingSlotId.value = null
  }
}
```

### 5. 状态管理（Pinia Stores）

#### user.js - 用户状态
```javascript
export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: getUserInfo() || null,
    userType: getUserType() || '' // 'customer' | 'merchant'
  }),
  
  actions: {
    login(token, userInfo, userType) {
      this.token = token
      this.userInfo = userInfo
      this.userType = userType
      setToken(token)
      setUserInfo(userInfo)
      setUserType(userType)
    },
    
    logout() {
      this.token = ''
      this.userInfo = null
      this.userType = ''
      removeToken()
      removeUserInfo()
      removeUserType()
    }
  }
})
```

#### order.js - 订单状态
```javascript
export const useOrderStore = defineStore('order', {
  state: () => ({
    pendingOrders: [], // 待处理订单
    unreadCount: 0     // 未读消息数
  }),
  
  actions: {
    addPendingOrder(order) {
      this.pendingOrders.unshift(order)
      this.unreadCount++
      // 播放提示音
      playNotificationSound()
    },
    
    markAsRead(orderId) {
      const index = this.pendingOrders.findIndex(o => o.id === orderId)
      if (index > -1) {
        this.pendingOrders.splice(index, 1)
        this.unreadCount--
      }
    }
  }
})
```

### 6. 路由守卫（src/router/index.js）

**功能特性**:
- 登录验证
- 角色权限控制
- 动态标题设置

**关键代码**:
```javascript
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 匠心美业` : '匠心美业O2O平台'
  
  const token = getToken()
  const userType = getUserType()
  
  // 需要登录的页面
  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  
  // B端页面权限控制
  if (to.path.startsWith('/admin') && userType !== 'merchant') {
    next({ path: '/' })
    return
  }
  
  // C端页面权限控制
  if (to.path.startsWith('/customer') && userType !== 'customer') {
    next({ path: '/login' })
    return
  }
  
  next()
})
```

## 性能优化策略

### 1. 路由懒加载

所有页面组件都采用动态导入，实现代码分割：

```javascript
{
  path: '/customer/home',
  name: 'CustomerHome',
  component: () => import('@/views/customer/HomeView.vue'),
  meta: { title: '首页' }
}
```

### 2. 图片懒加载

使用Element Plus的lazy属性：

```vue
<el-image
  :src="technician.avatar"
  lazy
  fit="cover"
/>
```

### 3. 列表虚拟滚动

对于长列表（如订单列表、技师列表），使用虚拟滚动技术：

```vue
<el-table
  :data="orderList"
  height="600"
  virtual-scroll
>
```

### 4. 接口缓存

对不常变化的数据（如服务分类、技师基础信息）进行缓存：

```javascript
// 获取服务分类（带缓存）
export async function getServiceCategories() {
  const cached = getCache('service_categories')
  if (cached) {
    return cached
  }
  
  const res = await request({
    url: '/customer/service/category/list',
    method: 'get'
  })
  
  setCache('service_categories', res.data, 30) // 缓存30分钟
  return res.data
}
```

### 5. 防抖节流

对频繁触发的操作（如搜索、滚动）使用防抖节流：

```javascript
// 搜索防抖
const handleSearch = debounce((keyword) => {
  searchTechnicians(keyword)
}, 300)

// 滚动节流
const handleScroll = throttle(() => {
  loadMoreData()
}, 200)
```

## 响应式设计

### C端移动端适配

使用viewport单位和媒体查询：

```css
/* 移动端适配 */
@media screen and (max-width: 750px) {
  .container {
    padding: 0 16px;
  }
  
  .card {
    margin-bottom: 12px;
  }
}

/* PC端适配 */
@media screen and (min-width: 751px) {
  .container {
    max-width: 1200px;
    margin: 0 auto;
  }
}
```

### B端PC端优化

固定侧边栏，内容区自适应：

```vue
<el-container>
  <el-aside width="200px">
    <!-- 侧边栏 -->
  </el-aside>
  <el-main>
    <!-- 主内容区 -->
  </el-main>
</el-container>
```

## 安全策略

### 1. XSS防护

- 所有用户输入进行转义
- 使用v-text而非v-html
- Content-Security-Policy头配置

### 2. CSRF防护

- Token存储在HttpOnly Cookie中（生产环境建议）
- 请求头携带Authorization token

### 3. 敏感信息脱敏

手机号、身份证等敏感信息进行脱敏显示：

```javascript
export function maskPhone(phone) {
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}
```

## 监控与埋点

### 1. 错误监控

使用window.onerror捕获全局错误：

```javascript
window.onerror = function(message, source, lineno, colno, error) {
  // 上报错误到监控系统
  reportError({
    message,
    source,
    lineno,
    colno,
    stack: error?.stack,
    userAgent: navigator.userAgent,
    url: window.location.href
  })
}
```

### 2. 性能监控

使用Performance API监控页面性能：

```javascript
window.addEventListener('load', () => {
  const perfData = performance.getEntriesByType('navigation')[0]
  console.log('页面加载时间:', perfData.loadEventEnd - perfData.fetchStart)
})
```

### 3. 用户行为埋点

关键操作进行埋点：

```javascript
// 预约成功埋点
trackEvent('booking_success', {
  technicianId: technician.id,
  serviceId: service.id,
  timeSlotId: timeSlot.id
})
```

## 开发规范

### 1. 命名规范

- 组件名：PascalCase（如TechnicianCard.vue）
- 变量名：camelCase（如userInfo）
- 常量名：UPPER_SNAKE_CASE（如API_BASE_URL）
- 文件名：kebab-case或PascalCase

### 2. 注释规范

- 函数必须有JSDoc注释
- 复杂逻辑必须添加行内注释
- TODO/FIXME标记待办事项

### 3. 代码审查

- 所有代码提交前必须通过ESLint检查
- 重要功能需要至少一人review
- 使用Git Flow工作流

## 未来优化方向

1. **服务端渲染（SSR）**: 提升首屏加载速度和SEO
2. **PWA支持**: 离线访问和推送通知
3. **微前端架构**: 解耦C端和B端，独立部署
4. **国际化（i18n）**: 支持多语言
5. **主题定制**: 支持深色模式和自定义主题
6. **单元测试**: 提高代码质量和可维护性
7. **E2E测试**: 自动化端到端测试
