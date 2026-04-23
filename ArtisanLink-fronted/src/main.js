import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import router from './router'

// 导入stores
import { useUserStore } from './stores/user'
import { useOrderStore } from './stores/order'
import { useCacheStore } from './stores/cache'

const app = createApp(App)
const pinia = createPinia()

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus)

// 初始化stores
const userStore = useUserStore()
const orderStore = useOrderStore()
const cacheStore = useCacheStore()

// 将stores挂载到全局属性
app.config.globalProperties.$userStore = userStore
app.config.globalProperties.$orderStore = orderStore
app.config.globalProperties.$cacheStore = cacheStore

app.mount('#app')
