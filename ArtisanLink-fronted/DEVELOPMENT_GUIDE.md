# 匠心美业O2O平台 - 前端开发指南

## 快速开始

### 1. 环境准备

确保已安装以下软件：

- Node.js >= 18.0.0
- npm >= 9.0.0 或 yarn >= 1.22.0
- Git

### 2. 克隆项目

```bash
git clone <repository-url>
cd ArtisanLink-fronted
```

### 3. 安装依赖

```bash
npm install
# 或
yarn install
```

### 4. 配置环境变量

复制 `.env.example` 为 `.env.development`：

```bash
cp .env.example .env.development
```

修改 `.env.development` 中的配置：

```env
VITE_API_BASE_URL=http://localhost:8124/api
VITE_WS_URL=ws://localhost:8080
VITE_APP_TITLE=匠心美业O2O平台
```

### 5. 启动开发服务器

```bash
npm run dev
# 或
yarn dev
```

访问 http://localhost:5173

## 开发流程

### 1. 创建新页面

#### 步骤1：创建页面组件

在 `src/views/` 目录下创建新的Vue组件：

```vue
<!-- src/views/NewPageView.vue -->
<template>
  <div class="new-page">
    <h1>新页面</h1>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// 状态定义
const data = ref([])

// 生命周期
onMounted(() => {
  fetchData()
})

// 方法定义
async function fetchData() {
  try {
    // 调用API
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped>
.new-page {
  padding: 20px;
}
</style>
```

#### 步骤2：添加路由配置

在 `src/router/index.js` 中添加路由：

```javascript
{
  path: '/new-page',
  name: 'NewPage',
  component: () => import('@/views/NewPageView.vue'),
  meta: { 
    title: '新页面',
    requiresAuth: true // 是否需要登录
  }
}
```

#### 步骤3：添加菜单项（如需要）

在对应的布局组件中添加菜单项。

### 2. 创建新组件

#### 步骤1：创建组件文件

在 `src/components/` 目录下创建组件：

```vue
<!-- src/components/CustomComponent.vue -->
<template>
  <div class="custom-component">
    <slot></slot>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

// 定义props
const props = defineProps({
  title: {
    type: String,
    required: true
  }
})

// 定义emits
const emit = defineEmits(['update'])

// 方法
function handleClick() {
  emit('update', { /* data */ })
}
</script>

<style scoped>
.custom-component {
  /* styles */
}
</style>
```

#### 步骤2：使用组件

在页面中导入并使用：

```vue
<template>
  <CustomComponent :title="'标题'" @update="handleUpdate">
    内容
  </CustomComponent>
</template>

<script setup>
import CustomComponent from '@/components/CustomComponent.vue'

function handleUpdate(data) {
  console.log(data)
}
</script>
```

### 3. 添加API接口

#### 步骤1：在API文件中添加方法

在 `src/api/customer.js` 或 `src/api/index.js` 中添加：

```javascript
// C端API
export function getNewData(params) {
  return request({
    url: '/customer/new-data',
    method: 'get',
    params
  })
}

// B端API
export function saveNewData(data) {
  return request({
    url: '/admin/new-data/save',
    method: 'post',
    data
  })
}
```

#### 步骤2：在页面中调用

```vue
<script setup>
import { getNewData } from '@/api/customer'

async function fetchData() {
  const res = await getNewData({ pageNum: 1, pageSize: 10 })
  dataList.value = res.data.list
}
</script>
```

### 4. 添加状态管理

#### 步骤1：创建Store

在 `src/stores/` 目录下创建store：

```javascript
// src/stores/newModule.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useNewModuleStore = defineStore('newModule', () => {
  // State
  const items = ref([])
  const loading = ref(false)
  
  // Actions
  async function fetchItems() {
    loading.value = true
    try {
      // API调用
      items.value = await getItems()
    } finally {
      loading.value = false
    }
  }
  
  function addItem(item) {
    items.value.push(item)
  }
  
  // Getters
  const itemCount = computed(() => items.value.length)
  
  return {
    items,
    loading,
    fetchItems,
    addItem,
    itemCount
  }
})
```

#### 步骤2：在组件中使用

```vue
<script setup>
import { useNewModuleStore } from '@/stores/newModule'

const store = useNewModuleStore()

// 访问state
console.log(store.items)

// 调用action
await store.fetchItems()

// 访问getter
console.log(store.itemCount)
</script>
```

## 代码规范

### 1. Vue组件规范

#### 推荐使用 `<script setup>` 语法

```vue
<script setup>
import { ref, computed, onMounted } from 'vue'

// 状态
const count = ref(0)

// 计算属性
const doubleCount = computed(() => count.value * 2)

// 方法
function increment() {
  count.value++
}

// 生命周期
onMounted(() => {
  console.log('mounted')
})
</script>
```

#### Props定义

```javascript
const props = defineProps({
  title: {
    type: String,
    required: true,
    default: ''
  },
  count: {
    type: Number,
    default: 0
  },
  items: {
    type: Array,
    default: () => []
  }
})
```

#### Emits定义

```javascript
const emit = defineEmits(['update:modelValue', 'change', 'submit'])

// 触发事件
emit('update:modelValue', newValue)
emit('change', { id: 1, value: 'test' })
```

### 2. 样式规范

#### 使用Scoped样式

```vue
<style scoped>
.container {
  padding: 20px;
}

.title {
  font-size: 18px;
  color: #333;
}
</style>
```

#### 使用CSS变量

```css
:root {
  --primary-color: #409EFF;
  --success-color: #67C23A;
  --warning-color: #E6A23C;
  --danger-color: #F56C6C;
  --text-color: #303133;
  --border-color: #DCDFE6;
}

.button {
  background-color: var(--primary-color);
  color: white;
}
```

#### 响应式设计

```css
/* 移动端 */
@media screen and (max-width: 750px) {
  .container {
    padding: 16px;
  }
}

/* 平板 */
@media screen and (min-width: 751px) and (max-width: 1024px) {
  .container {
    padding: 24px;
  }
}

/* PC端 */
@media screen and (min-width: 1025px) {
  .container {
    max-width: 1200px;
    margin: 0 auto;
  }
}
```

### 3. JavaScript规范

#### 使用ES6+语法

```javascript
// 箭头函数
const handleClick = () => {
  console.log('clicked')
}

// 解构赋值
const { name, age } = user

// 模板字符串
const message = `Hello, ${name}!`

// 展开运算符
const newObj = { ...oldObj, newProp: 'value' }

// 可选链
const city = user?.address?.city

// 空值合并
const value = input ?? 'default'
```

#### 异步处理

```javascript
// async/await
async function fetchData() {
  try {
    const res = await api.getData()
    data.value = res.data
  } catch (error) {
    ElMessage.error(error.message)
  }
}

// Promise.all
async function fetchMultipleData() {
  const [users, orders] = await Promise.all([
    getUsers(),
    getOrders()
  ])
}
```

### 4. 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件名 | PascalCase | TechnicianCard.vue |
| 文件名 | kebab-case或PascalCase | technician-card.vue 或 TechnicianCard.vue |
| 变量名 | camelCase | userInfo, orderList |
| 常量名 | UPPER_SNAKE_CASE | API_BASE_URL, MAX_RETRY_COUNT |
| 函数名 | camelCase | getUserInfo, handleSubmit |
| CSS类名 | kebab-case | user-info, order-list |

## 调试技巧

### 1. Vue DevTools

安装Vue DevTools浏览器扩展，可以：

- 查看组件树
- 检查组件props和state
- 追踪事件
- 性能分析

### 2. 控制台日志

```javascript
// 基础日志
console.log('message')
console.warn('warning')
console.error('error')

// 分组日志
console.group('User Data')
console.log('Name:', user.name)
console.log('Age:', user.age)
console.groupEnd()

// 表格日志
console.table(userList)

// 性能测量
console.time('fetchData')
await fetchData()
console.timeEnd('fetchData')
```

### 3. Element Plus消息提示

```javascript
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'

// 消息提示
ElMessage.success('操作成功')
ElMessage.warning('请注意')
ElMessage.error('操作失败')

// 确认框
ElMessageBox.confirm('确定要删除吗？', '提示', {
  confirmButtonText: '确定',
  cancelButtonText: '取消',
  type: 'warning'
}).then(() => {
  // 用户点击确定
}).catch(() => {
  // 用户点击取消
})

// 通知
ElNotification({
  title: '新订单',
  message: '您有一个新的预约订单',
  type: 'success',
  duration: 5000
})
```

### 4. 网络请求调试

在浏览器开发者工具的Network面板中：

- 查看所有HTTP请求
- 检查请求头和响应头
- 查看请求参数和响应数据
- 模拟慢速网络

### 5. WebSocket调试

```javascript
// 在控制台中监听WebSocket消息
wsClient.on('message', (data) => {
  console.log('WS Message:', data)
})

// 手动发送消息
wsClient.send({ type: 'TEST', data: { test: 'value' } })
```

## 常见问题解决

### 1. 跨域问题

**症状**: 控制台显示CORS错误

**解决方案**:

方案1：后端配置CORS

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
```

方案2：Vite代理配置

```javascript
// vite.config.js
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8124',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

### 2. Token过期处理

**症状**: 登录后刷新页面需要重新登录

**解决方案**:

确保在main.js中正确初始化token：

```javascript
const token = localStorage.getItem('token')
if (token) {
  config.headers['Authorization'] = `Bearer ${token}`
}
```

### 3. WebSocket连接失败

**症状**: WebSocket无法连接或频繁断开

**解决方案**:

1. 检查WebSocket服务是否正常运行
2. 确认token是否正确传递
3. 检查防火墙设置
4. 增加重连间隔时间

```javascript
// websocket.js
this.reconnectDelay = Math.min(this.reconnectDelay * 2, 30000) // 最大30秒
```

### 4. 页面白屏

**症状**: 页面加载后显示空白

**解决方案**:

1. 检查浏览器控制台错误信息
2. 确认路由配置正确
3. 检查组件是否正确导入
4. 确认环境变量配置正确

### 5. 样式不生效

**症状**: CSS样式没有应用

**解决方案**:

1. 检查是否使用了scoped，如果是，确保选择器正确
2. 检查CSS优先级
3. 使用浏览器开发者工具检查元素样式
4. 确认样式文件是否正确导入

## 性能优化建议

### 1. 减少不必要的渲染

```javascript
// 使用v-memo缓存子树
<div v-memo="[item.id, item.name]">
  {{ item.name }}
</div>

// 使用shallowRef替代ref（对于大型对象）
import { shallowRef } from 'vue'
const largeObject = shallowRef({ /* ... */ })
```

### 2. 图片优化

```vue
<!-- 懒加载 -->
<el-image :src="imageUrl" lazy />

<!-- 使用WebP格式 -->
<picture>
  <source srcset="image.webp" type="image/webp">
  <img src="image.jpg" alt="description">
</picture>

<!-- 响应式图片 -->
<img 
  srcset="small.jpg 480w, medium.jpg 800w, large.jpg 1200w"
  sizes="(max-width: 480px) 480px, (max-width: 800px) 800px, 1200px"
  src="medium.jpg"
  alt="description"
>
```

### 3. 列表优化

```vue
<!-- 虚拟滚动 -->
<el-table
  :data="largeList"
  height="600"
  virtual-scroll
/>

<!-- 分页加载 -->
<el-pagination
  v-model:current-page="pageNum"
  v-model:page-size="pageSize"
  :total="total"
  @current-change="handlePageChange"
/>
```

### 4. 防抖节流

```javascript
import { debounce, throttle } from 'lodash-es'

// 防抖：适合搜索、输入等场景
const handleSearch = debounce((keyword) => {
  searchApi(keyword)
}, 300)

// 节流：适合滚动、resize等场景
const handleScroll = throttle(() => {
  loadMoreData()
}, 200)
```

## 测试

### 1. 单元测试（可选）

安装测试框架：

```bash
npm install -D vitest @vue/test-utils jsdom
```

创建测试文件：

```javascript
// src/components/__tests__/TechnicianCard.test.js
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import TechnicianCard from '../TechnicianCard.vue'

describe('TechnicianCard', () => {
  it('renders technician name', () => {
    const wrapper = mount(TechnicianCard, {
      props: {
        technician: {
          id: 1,
          name: '李师傅',
          avatar: 'avatar.jpg'
        }
      }
    })
    
    expect(wrapper.text()).toContain('李师傅')
  })
})
```

运行测试：

```bash
npm run test
```

### 2. E2E测试（可选）

使用Playwright或Cypress进行端到端测试。

## 部署前检查清单

- [ ] 所有功能测试通过
- [ ] 代码通过ESLint检查
- [ ] 代码已格式化
- [ ] 环境变量配置正确
- [ ] 移除所有console.log（生产环境）
- [ ] 构建无错误
- [ ] 预览构建结果正常
- [ ] 更新CHANGELOG.md
- [ ] 提交代码并打标签

## 资源链接

- [Vue 3官方文档](https://cn.vuejs.org/)
- [Vite官方文档](https://cn.vitejs.dev/)
- [Element Plus官方文档](https://element-plus.org/zh-CN/)
- [Pinia官方文档](https://pinia.vuejs.org/zh/)
- [Vue Router官方文档](https://router.vuejs.org/zh/)
- [Axios官方文档](https://axios-http.com/zh/docs/intro)
- [ECharts官方文档](https://echarts.apache.org/zh/index.html)

## 获取帮助

遇到问题时：

1. 查看本文档的"常见问题解决"部分
2. 查看相关框架的官方文档
3. 在项目Issues中搜索类似问题
4. 联系项目负责人
