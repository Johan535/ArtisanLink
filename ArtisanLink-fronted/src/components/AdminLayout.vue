<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { adminApi } from '../api'
import { clearAdminInfo, clearToken, getAdminInfo } from '../utils/auth'

const route = useRoute()
const router = useRouter()

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
