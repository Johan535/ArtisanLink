<template>
  <div class="customer-layout">
    <!-- 顶部导航栏 -->
    <el-header class="customer-header">
      <div class="header-content">
        <div class="logo" @click="goHome">
          <span class="logo-text">匠心美业</span>
        </div>
        
        <div class="nav-menu">
          <el-button link @click="goHome">首页</el-button>
          <el-button link @click="goToOrders">我的订单</el-button>
          <el-button link @click="goToProfile">个人中心</el-button>
        </div>
        
        <div class="user-info">
          <el-dropdown>
            <span class="user-avatar">
              <el-avatar :src="userInfo?.avatar || defaultAvatar" />
              <span class="user-name">{{ userInfo?.nickname || '未登录' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToProfile">个人中心</el-dropdown-item>
                <el-dropdown-item @click="goToMessages">消息中心</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    
    <!-- 主内容区 -->
    <el-main class="customer-main">
      <router-view />
    </el-main>
    
    <!-- 底部版权 -->
    <el-footer class="customer-footer">
      <p>© 2026 匠心美业O2O平台 - 中高端美业服务专家</p>
    </el-footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92fef6a581png.png'

const goHome = () => {
  router.push('/customer/home')
}

const goToOrders = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push('/customer/orders')
}

const goToProfile = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push('/customer/profile')
}

const goToMessages = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push('/customer/messages')
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.customer-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.customer-header {
  background: linear-gradient(135deg, #d4a574 0%, #c9945f 100%);
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: white;
  letter-spacing: 2px;
}

.nav-menu {
  display: flex;
  gap: 20px;
}

.nav-menu .el-button {
  color: white;
  font-size: 16px;
}

.nav-menu .el-button:hover {
  color: #fff5e6;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  color: white;
}

.user-name {
  font-size: 14px;
}

.customer-main {
  flex: 1;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.customer-footer {
  text-align: center;
  padding: 20px;
  background-color: white;
  border-top: 1px solid #e0e0e0;
  color: #999;
  font-size: 14px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 10px;
  }
  
  .logo-text {
    font-size: 18px;
  }
  
  .nav-menu {
    display: none;
  }
  
  .user-name {
    display: none;
  }
}
</style>