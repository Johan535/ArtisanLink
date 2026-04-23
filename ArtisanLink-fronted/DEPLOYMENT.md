# 匠心美业O2O平台 - 前端部署指南

## 环境要求

- Node.js >= 18.0.0
- npm >= 9.0.0 或 yarn >= 1.22.0

## 安装依赖

```bash
npm install
# 或
yarn install
```

## 开发环境运行

```bash
npm run dev
# 或
yarn dev
```

访问 http://localhost:5173

## 生产环境构建

```bash
npm run build
# 或
yarn build
```

构建产物位于 `dist` 目录

## 预览生产构建

```bash
npm run preview
# 或
yarn preview
```

## 代码规范检查

```bash
# ESLint检查并自动修复
npm run lint

# Prettier格式化代码
npm run format
```

## 环境变量配置

项目支持多环境配置：

- `.env.development` - 开发环境
- `.env.production` - 生产环境

主要配置项：

```env
# API基础URL
VITE_API_BASE_URL=http://localhost:8124/api

# WebSocket地址
VITE_WS_URL=ws://localhost:8080

# 应用标题
VITE_APP_TITLE=匠心美业O2O平台
```

## Nginx部署配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    root /var/www/artisanlink-fronted/dist;
    index index.html;

    # 支持Vue Router的history模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API代理
    location /api/ {
        proxy_pass http://localhost:8124/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # WebSocket代理
    location /ws/ {
        proxy_pass http://localhost:8080/ws/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

## Docker部署（可选）

### Dockerfile

```dockerfile
FROM node:18-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  frontend:
    build: .
    ports:
      - "80:80"
    environment:
      - VITE_API_BASE_URL=http://backend:8124/api
      - VITE_WS_URL=ws://backend:8080
    depends_on:
      - backend

  backend:
    image: artisanlink-backend:latest
    ports:
      - "8124:8124"
```

## 性能优化建议

1. **启用Gzip压缩**：在Nginx中配置gzip压缩
2. **使用CDN**：将静态资源托管到CDN
3. **懒加载路由**：已配置路由级代码分割
4. **图片优化**：使用WebP格式，添加lazy loading
5. **缓存策略**：合理设置HTTP缓存头

## 常见问题

### 1. 跨域问题

确保后端配置了正确的CORS头，或在Nginx中配置代理。

### 2. WebSocket连接失败

- 检查WebSocket服务是否正常运行
- 确认防火墙允许WebSocket端口
- 检查token是否正确传递

### 3. 白屏问题

- 检查浏览器控制台错误信息
- 确认构建产物路径正确
- 检查环境变量配置

## 监控与日志

建议使用以下工具进行监控：

- **Sentry**：错误追踪
- **Google Analytics**：用户行为分析
- **Lighthouse**：性能审计

## 更新日志

### v1.0.0 (2026-04-23)

- ✅ C端用户端完整实现
- ✅ B端店长管理端完整实现
- ✅ WebSocket实时通信
- ✅ 前端缓存策略
- ✅ 响应式设计
- ✅ 代码规范配置
