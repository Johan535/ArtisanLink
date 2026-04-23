# 匠心美业O2O平台 - 前端项目

## 项目简介

匠心美业O2O平台是一个中高端美业垂直O2O平台，仿大众点评风格，包含C端用户端和B端店长管理端。

## 技术栈

- **框架**: Vue 3 (Composition API)
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **UI组件库**: Element Plus
- **构建工具**: Vite
- **HTTP请求**: Axios
- **实时通信**: WebSocket
- **图表库**: ECharts
- **代码规范**: ESLint + Prettier

## 项目结构

```
ArtisanLink-fronted/
├── public/                    # 静态资源
├── src/
│   ├── api/                   # API接口封装
│   │   ├── http.js           # Axios封装
│   │   ├── index.js          # B端API接口
│   │   └── customer.js       # C端API接口
│   ├── components/            # 公共组件
│   │   ├── admin/            # B端组件
│   │   │   └── TechnicianSchedule.vue  # 技师排班组件
│   │   ├── customer/         # C端组件
│   │   │   ├── SearchBar.vue          # 搜索栏
│   │   │   ├── TechnicianCard.vue     # 技师卡片
│   │   │   ├── TimeSlotPicker.vue     # 时间片选择器
│   │   │   ├── ReviewList.vue         # 评价列表
│   │   │   └── CaseShowcase.vue       # 案例展示
│   │   ├── AdminLayout.vue    # B端布局
│   │   ├── CustomerLayout.vue # C端布局
│   │   └── NotificationBell.vue # 消息通知铃铛
│   ├── router/                # 路由配置
│   │   └── index.js
│   ├── stores/                # Pinia状态管理
│   │   ├── user.js           # 用户状态
│   │   ├── order.js          # 订单状态
│   │   └── cache.js          # 缓存状态
│   ├── utils/                 # 工具类
│   │   ├── auth.js           # 认证工具
│   │   ├── cache.js          # 缓存工具
│   │   ├── helper.js         # 辅助工具（防抖、节流等）
│   │   ├── websocket.js      # WebSocket封装
│   │   └── wsHandler.js      # WebSocket消息处理
│   ├── views/                 # 页面视图
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
│   │   │   ├── CaseListView.vue       # 案例列表
│   │   │   ├── CaseDetailView.vue     # 案例详情
│   │   │   └── SearchResultView.vue   # 搜索结果
│   │   ├── LoginView.vue      # 登录页
│   │   ├── DashboardView.vue  # B端仪表盘
│   │   ├── StaffView.vue      # 员工管理
│   │   ├── ServiceView.vue    # 服务管理
│   │   ├── OrderView.vue      # 订单管理
│   │   ├── CustomerView.vue   # 客户管理
│   │   ├── StatisticsView.vue # 数据统计
│   │   ├── ScheduleView.vue   # 排班管理
│   │   ├── MessageView.vue    # 消息中心
│   │   └── MerchantView.vue   # 商户管理
│   ├── App.vue               # 根组件
│   ├── main.js               # 入口文件
│   └── style.css             # 全局样式
├── index.html                # HTML模板
├── package.json              # 项目依赖
├── vite.config.js            # Vite配置
└── README.md                 # 项目说明
```

## 安装依赖

```bash
npm install
```

## 开发环境运行

```bash
npm run dev
```

访问 http://localhost:5173

## 生产环境构建

```bash
npm run build
```

## 预览生产构建

```bash
npm run preview
```

## 核心功能模块

### C端用户端

1. **首页**
   - 顶部搜索栏（支持技师/服务名称搜索）
   - Banner轮播
   - 附近技师模块（按距离排序）
   - 服务分类导航
   - 案例种草板块

2. **技师详情页**
   - 技师头像、技能标签、评分
   - 可预约时间片展示
   - 服务列表
   - 用户评价

3. **预约下单页**
   - 预约信息确认表单
   - 时间片选择（防并发校验）
   - 支付集成

4. **订单列表页**
   - 按订单状态分类展示
   - 实时更新订单进度
   - 下拉加载更多

5. **个人中心**
   - 用户头像/昵称
   - 我的评价
   - 消息中心
   - 个人信息编辑

### B端店长管理端

1. **登录页**
   - 用户名/密码/验证码登录
   - Token存储与携带

2. **首页（数据概览）**
   - 门店订单量、营收、预约数统计
   - 时间范围筛选（今日/本周/本月）
   - 实时订单通知

3. **技师管理页**
   - 技师列表
   - 新增/编辑技师
   - 排班设置

4. **服务管理页**
   - 服务分类列表
   - 服务上下架
   - 价格/时长配置

5. **预约管理页**
   - 排期表展示（按技师+日期维度）
   - 实时接单/拒绝操作
   - 冲突预警

6. **数据统计页**
   - 营收趋势图表
   - 订单来源分析
   - 多维度数据可视化

7. **消息中心**
   - WebSocket推送的订单通知
   - 标记已读功能

## 接口规范

所有接口响应格式统一为：

```json
{
  "code": 200,
  "msg": "成功",
  "data": {}
}
```

错误码说明：
- 200: 成功
- 401: 未登录
- 403: 无权限
- 500: 服务器错误

## WebSocket连接

连接地址：`ws://{域名}/ws/shop/{shopId}`

监听事件：
- `order_notification`: 订单通知
- `order_status_change`: 订单状态变更
- `message`: 普通消息

## 性能优化

1. **前端缓存策略**
   - localStorage + 内存缓存
   - 门店/技师基础信息缓存30分钟

2. **接口请求优化**
   - 超时时间设置为5s
   - 防抖/节流处理

3. **构建优化**
   - 代码分割（vendor、elementPlus、echarts）
   - Tree shaking
   - 压缩混淆

## 兼容性

- **C端**: 兼容微信浏览器、主流手机浏览器（375px-750px宽度）
- **B端**: 兼容Chrome/Firefox/Safari最新版

## UI/UX设计

- **风格**: 中高端轻奢风格
- **主色调**: 莫兰迪色系（浅咖/米白/低饱和玫瑰金）
- **响应式**: 适配PC（B端）、H5（C端）
- **动效**: 关键操作添加轻量级动效

## 开发规范

1. 使用Vue 3 Composition API
2. 使用TypeScript类型约束（可选）
3. 遵循ESLint + Prettier规范
4. 组件命名采用PascalCase
5. 文件名采用kebab-case或PascalCase

## 注意事项

1. 所有接口请求需携带token（登录后）
2. 401错误自动跳转登录页
3. WebSocket断开时自动重连
4. 预约时间片选择时需做防并发校验
5. 敏感操作需二次确认

## 作者

小嘎

## 许可证

MIT
