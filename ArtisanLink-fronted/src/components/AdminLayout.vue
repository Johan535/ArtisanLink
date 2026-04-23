<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Bell, User, Setting, Document, DataAnalysis, Calendar, Shop, Service, Tickets, Message } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api/index'
import { useWebSocket } from '@/utils/websocket'
import { wsHandler } from '@/utils/wsHandler'
import NotificationBell from './NotificationBell.vue'
import { adminApi } from '../api'
import { clearAdminInfo, clearToken, getAdminInfo } from '../utils/auth'

const route = useRoute()
const router = useRouter()

// WebSocket实例
const wsInstance = useWebSocket()

// 状态
const activeMenu = computed(() => route.path)
const unreadMessageCount = ref(0)

const menus = [
  { label: '数据看板', path: '/dashboard' },
  { label: '商户管理', path: '/merchants' },
  { label: '员工管理', path: '/staff' },
  { label: '服务管理', path: '/services' },
  { label: '订单管理', path: '/orders' },
  { label: '客户管理', path: '/customers' },
  { label: '经营统计', path: '/statistics' }
]

const adminName = computed(() => getAdminInfo()?.username || '管理员')

function go(path) {
  router.push(path)
}

async function logout() {
  try {
    await adminApi.logout()
  } catch (error) {
    // 如果后端退出失败，前端仍然清理登录状态。
  } finally {
    clearToken()
    clearAdminInfo()
    router.push('/login')
  }
}

/**
 * 处理WebSocket新消息
 */
function handleNewMessage(data) {
  unreadMessageCount.value++
  
  // 显示通知
  ElMessage({
    message: data.title || '收到新消息',
    type: 'info',
    duration: 3000
  })
}

/**
 * 加载未读消息数
 */
async function loadUnreadMessageCount() {
  try {
    const res = await adminApi.getMessageList({ page: 1, pageSize: 1, isRead: false })
    if (res.code === 200) {
      unreadMessageCount.value = res.data?.total || 0
    }
  } catch (error) {
    console.error('加载未读消息数失败:', error)
  }
}

onMounted(() => {
  // 加载未读消息数
  loadUnreadMessageCount()
  
  // 注册WebSocket消息处理器
  wsHandler.on('message', handleNewMessage)
  wsHandler.on('order_notification', handleNewMessage)
})

onUnmounted(() => {
  // 移除WebSocket消息处理器
  wsHandler.off('message', handleNewMessage)
  wsHandler.off('order_notification', handleNewMessage)
})
</script>

<template>
  <div class="admin-shell">
    <aside class="sidebar">
      <div class="logo">匠心美业管理端</div>
      <nav>
        <button
          v-for="item in menus"
          :key="item.path"
          class="menu-item"
          :class="{ active: route.path === item.path }"
          @click="go(item.path)"
        >
          {{ item.label }}
        </button>
        <el-menu-item index="/admin/message">
          <el-icon><Bell /></el-icon>
          <template #title>消息中心</template>
          <el-badge v-if="unreadMessageCount > 0" :value="unreadMessageCount" class="menu-badge" />
        </el-menu-item>
      </nav>
    </aside>

    <main class="main-area">
      <header class="topbar">
        <div class="welcome">欢迎你，{{ adminName }}</div>
        <button class="btn btn-light" @click="logout">退出登录</button>
      </header>
      <section class="content-card">
        <slot />
      </section>
    </main>
  </div>
</template>