# 匠心美业O2O平台 - 快速启动指南

## 🚀 5分钟快速启动

### 前置条件

确保已安装：
- Node.js >= 18.0.0
- npm >= 9.0.0 或 yarn >= 1.22.0

### 步骤1：克隆项目

```bash
git clone <repository-url>
cd ArtisanLink-fronted
```

### 步骤2：安装依赖

```bash
npm install
# 或
yarn install
```

### 步骤3：配置环境变量

```bash
# 复制环境变量示例文件
cp .env.example .env.development

# 编辑 .env.development，修改为实际的后端地址
VITE_API_BASE_URL=http://localhost:8124/api
VITE_WS_URL=ws://localhost:8080
VITE_APP_TITLE=匠心美业O2O平台
```

### 步骤4：启动开发服务器

```bash
npm run dev
# 或
yarn dev
```

### 步骤5：访问应用

打开浏览器访问：**http://localhost:5173**

---

## 📱 页面路由

### C端用户端

| 路径 | 页面 | 说明 |
|------|------|------|
| `/customer/home` | 首页 | 搜索、附近技师、服务分类、案例展示 |
| `/customer/technicians` | 技师列表 | 所有技师列表 |
| `/customer/technician/:id` | 技师详情 | 技师信息、时间片选择、评价 |
| `/customer/booking` | 预约下单 | 确认预约信息并提交 |
| `/customer/orders` | 订单列表 | 查看我的订单 |
| `/customer/order/:id` | 订单详情 | 订单详细信息 |
| `/customer/profile` | 个人中心 | 用户信息编辑 |
| `/customer/messages` | 消息中心 | 系统通知和订单通知 |
| `/customer/reviews` | 我的评价 | 查看和提交评价 |
| `/customer/services/categories` | 服务分类 | 所有服务分类 |
| `/customer/services` | 服务列表 | 服务项目列表 |
| `/customer/search` | 搜索结果 | 技师和服务搜索结果 |
| `/customer/cases` | 案例列表 | 案例种草内容 |
| `/customer/case/:id` | 案例详情 | 案例详细信息 |

### B端店长管理端

| 路径 | 页面 | 说明 |
|------|------|------|
| `/login` | 登录页 | 管理员登录 |
| `/admin/dashboard` | 仪表盘 | 数据概览和统计 |
| `/admin/staff` | 员工管理 | 技师CRUD操作 |
| `/admin/schedule` | 排班管理 | 批量设置排班 |
| `/admin/services` | 服务管理 | 服务上下架和配置 |
| `/admin/orders` | 订单管理 | 接单、拒绝、完成订单 |
| `/admin/customers` | 客户管理 | 客户列表查询 |
| `/admin/statistics` | 数据统计 | 营收趋势和图表分析 |
| `/admin/messages` | 消息中心 | 订单通知管理 |
| `/admin/merchant` | 商户管理 | 商户信息管理 |

---

## 🔑 测试账号

### C端用户

```
手机号：13800138000
密码：123456
```

### B端管理员

```
用户名：admin
密码：123456
```

---

## 🛠️ 常用命令

```bash
# 开发环境
npm run dev              # 启动开发服务器

# 构建
npm run build            # 生产环境构建
npm run preview          # 预览构建结果

# 代码质量
npm run lint             # ESLint检查
npm run lint:fix         # 自动修复ESLint错误
npm run format           # Prettier格式化

# 测试（如果配置了）
npm run test             # 运行单元测试
npm run test:e2e         # 运行E2E测试
```

---

## 📦 项目结构速览

```
ArtisanLink-fronted/
├── src/
│   ├── api/              # API接口层
│   │   ├── http.js       # Axios封装
│   │   ├── index.js      # B端API
│   │   └── customer.js   # C端API
│   ├── components/       # 公共组件
│   │   ├── admin/        # B端组件
│   │   ├── customer/     # C端组件
│   │   └── *.vue         # 通用组件
│   ├── router/           # 路由配置
│   ├── stores/           # 状态管理
│   ├── utils/            # 工具类
│   ├── views/            # 页面视图
│   │   ├── customer/     # C端页面
│   │   └── *.vue         # B端页面
│   ├── App.vue           # 根组件
│   ├── main.js           # 入口文件
│   └── style.css         # 全局样式
├── .env.development      # 开发环境变量
├── .env.production       # 生产环境变量
├── package.json          # 项目配置
└── vite.config.js        # Vite配置
```

---

## 🔧 常见问题

### 1. 端口被占用

**问题**：启动时报错 "Port 5173 is already in use"

**解决**：
```bash
# 方法1：杀死占用端口的进程
# Windows
netstat -ano | findstr :5173
taskkill /PID <PID> /F

# Mac/Linux
lsof -ti:5173 | xargs kill -9

# 方法2：使用其他端口
npm run dev -- --port 3000
```

### 2. 依赖安装失败

**问题**：`npm install` 报错

**解决**：
```bash
# 清除npm缓存
npm cache clean --force

# 删除node_modules和package-lock.json
rm -rf node_modules package-lock.json

# 重新安装
npm install
```

### 3. 跨域问题

**问题**：控制台显示CORS错误

**解决**：
- 方案1：后端配置CORS允许跨域
- 方案2：在 `vite.config.js` 中配置代理

```javascript
// vite.config.js
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8124',
        changeOrigin: true
      }
    }
  }
})
```

### 4. Token过期

**问题**：登录后刷新页面需要重新登录

**解决**：
检查 `src/utils/auth.js` 中的token存储逻辑，确保正确保存到localStorage

### 5. WebSocket连接失败

**问题**：WebSocket无法连接

**解决**：
1. 检查后端WebSocket服务是否启动
2. 确认 `.env.development` 中的 `VITE_WS_URL` 配置正确
3. 检查防火墙设置

---

## 📚 相关文档

- [README.md](./README.md) - 项目说明
- [DEPLOYMENT.md](./DEPLOYMENT.md) - 部署指南
- [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) - API接口文档
- [ARCHITECTURE.md](./ARCHITECTURE.md) - 架构说明
- [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md) - 开发指南
- [CHANGELOG.md](./CHANGELOG.md) - 更新日志
- [PROJECT_SUMMARY.md](./PROJECT_SUMMARY.md) - 项目总结
- [CHECKLIST.md](./CHECKLIST.md) - 项目清单

---

## 💡 开发建议

### 1. 使用Vue DevTools

安装 [Vue DevTools](https://devtools.vuejs.org/) 浏览器扩展，可以：
- 查看组件树
- 检查props和state
- 追踪事件
- 性能分析

### 2. 开启Source Map

开发环境下默认开启Source Map，便于调试。生产环境建议关闭以提升性能。

### 3. 使用ESLint插件

在VSCode中安装以下插件：
- ESLint
- Prettier
- Volar（Vue官方推荐）

### 4. 配置Git Hook（可选）

使用husky和lint-staged实现提交前自动检查和格式化：

```bash
npm install -D husky lint-staged
npx husky init
```

创建 `.husky/pre-commit`：
```bash
#!/bin/sh
. "$(dirname "$0")/_/husky.sh"

npx lint-staged
```

配置 `package.json`：
```json
{
  "lint-staged": {
    "*.{js,vue}": [
      "eslint --fix",
      "prettier --write"
    ]
  }
}
```

---

## 🎯 下一步

1. **熟悉项目结构**：阅读 [ARCHITECTURE.md](./ARCHITECTURE.md)
2. **了解API接口**：阅读 [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)
3. **开始开发**：阅读 [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md)
4. **准备部署**：阅读 [DEPLOYMENT.md](./DEPLOYMENT.md)

---

## 🆘 获取帮助

遇到问题时：

1. 查看本文档的"常见问题"部分
2. 查看相关框架的官方文档
3. 在项目Issues中搜索类似问题
4. 联系项目负责人

**联系方式**：
- 项目负责人：小嘎
- 项目地址：D:\code\匠心美业全栈项目\ArtisanLink\ArtisanLink-fronted

---

**祝您开发顺利！** 🎉
