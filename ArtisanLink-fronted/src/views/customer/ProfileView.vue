<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { customerApi } from '@/api/customer'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/utils/auth'

const router = useRouter()

// 用户信息
const userInfo = reactive({
  avatar: '',
  nickname: '',
  phone: '',
  gender: 0,
  birthday: ''
})

// 编辑对话框
const editDialogVisible = ref(false)
const editForm = reactive({
  nickname: '',
  phone: '',
  gender: 0,
  birthday: ''
})

// 未读消息数
const unreadCount = ref(0)

// 加载状态
const loading = reactive({
  profile: false,
  submitting: false
})

// 加载用户信息
async function loadUserProfile() {
  loading.profile = true
  try {
    const res = await customerApi.getUserProfile()
    if (res.code === 200 && res.data) {
      Object.assign(userInfo, res.data)
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  } finally {
    loading.profile = false
  }
}

// 加载未读消息数
async function loadUnreadCount() {
  try {
    const res = await customerApi.getUnreadMessageCount()
    if (res.code === 200 && res.data) {
      unreadCount.value = res.data.count || 0
    }
  } catch (error) {
    console.error('加载未读消息数失败:', error)
  }
}

// 打开编辑对话框
function openEditDialog() {
  Object.assign(editForm, userInfo)
  editDialogVisible.value = true
}

// 提交编辑
async function submitEdit() {
  if (!editForm.nickname?.trim()) {
    ElMessage.warning('请输入昵称')
    return
  }
  
  loading.submitting = true
  try {
    const res = await customerApi.updateUserProfile(editForm)
    if (res.code === 200) {
      ElMessage.success('更新成功')
      Object.assign(userInfo, editForm)
      editDialogVisible.value = false
    } else {
      ElMessage.error(res.msg || '更新失败')
    }
  } catch (error) {
    console.error('更新用户信息失败:', error)
    ElMessage.error('更新失败')
  } finally {
    loading.submitting = false
  }
}

// 跳转到消息中心
function goToMessages() {
  router.push('/customer/messages')
}

// 跳转到我的评价
function goToReviews() {
  router.push('/customer/reviews')
}

// 退出登录
function handleLogout() {
  removeToken()
  ElMessage.success('已退出登录')
  router.replace('/login')
}

onMounted(async () => {
  await Promise.all([
    loadUserProfile(),
    loadUnreadCount()
  ])
})
</script>

<template>
  <div class="profile-container">
    <!-- 顶部导航 -->
    <header class="profile-header">
      <h2 class="header-title">个人中心</h2>
    </header>

    <!-- 用户信息卡片 -->
    <section class="user-info-card">
      <el-skeleton v-if="loading.profile" :rows="3" animated />
      
      <div v-else class="user-info-content">
        <div class="user-avatar-wrapper">
          <img 
            :src="userInfo.avatar || '/images/default-avatar.png'" 
            :alt="userInfo.nickname"
            class="user-avatar"
          />
          <el-button 
            size="small" 
            type="primary"
            @click="openEditDialog"
          >
            编辑资料
          </el-button>
        </div>
        
        <div class="user-details">
          <h3 class="user-nickname">{{ userInfo.nickname || '未设置昵称' }}</h3>
          <p class="user-phone">{{ userInfo.phone || '未绑定手机' }}</p>
          <div class="user-meta">
            <span v-if="userInfo.gender === 1">♂ 男</span>
            <span v-else-if="userInfo.gender === 2">♀ 女</span>
            <span v-else>性别未设置</span>
            <span v-if="userInfo.birthday">· {{ userInfo.birthday }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 功能菜单 -->
    <section class="menu-section">
      <div class="menu-item" @click="goToMessages">
        <div class="menu-icon">
          <el-icon><Bell /></el-icon>
        </div>
        <div class="menu-content">
          <span class="menu-label">消息中心</span>
          <span class="menu-desc">系统通知、订单提醒</span>
        </div>
        <div class="menu-extra">
          <el-badge v-if="unreadCount > 0" :value="unreadCount" :max="99" />
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="menu-item" @click="goToReviews">
        <div class="menu-icon">
          <el-icon><Star /></el-icon>
        </div>
        <div class="menu-content">
          <span class="menu-label">我的评价</span>
          <span class="menu-desc">查看和管理我的评价</span>
        </div>
        <div class="menu-extra">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="menu-item" @click="router.push('/customer/orders')">
        <div class="menu-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="menu-content">
          <span class="menu-label">我的订单</span>
          <span class="menu-desc">查看订单记录</span>
        </div>
        <div class="menu-extra">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="menu-item" @click="router.push('/customer/favorites')">
        <div class="menu-icon">
          <el-icon><StarFilled /></el-icon>
        </div>
        <div class="menu-content">
          <span class="menu-label">我的收藏</span>
          <span class="menu-desc">收藏的技师和服务</span>
        </div>
        <div class="menu-extra">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </section>

    <!-- 退出登录 -->
    <section class="logout-section">
      <el-button 
        type="danger" 
        plain
        @click="handleLogout"
        class="logout-btn"
      >
        退出登录
      </el-button>
    </section>

    <!-- 编辑资料对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑个人资料"
      width="90%"
      max-width="400px"
    >
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" maxlength="20" />
        </el-form-item>
        
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        
        <el-form-item label="性别">
          <el-radio-group v-model="editForm.gender">
            <el-radio :label="0">保密</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="生日">
          <el-date-picker
            v-model="editForm.birthday"
            type="date"
            placeholder="选择生日"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="submitEdit"
          :loading="loading.submitting"
        >
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.profile-container {
  min-height: 100vh;
  background: #f8f9fa;
  padding-bottom: 20px;
}

/* 顶部导航 */
.profile-header {
  background: linear-gradient(135deg, #d4a574 0%, #c9956b 100%);
  padding: 16px;
  text-align: center;
}

.header-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

/* 用户信息卡片 */
.user-info-card {
  background: #fff;
  margin: 16px;
  border-radius: 12px;
  overflow: hidden;
}

.user-info-content {
  padding: 24px;
  display: flex;
  gap: 20px;
  align-items: center;
}

.user-avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #d4a574;
}

.user-details {
  flex: 1;
}

.user-nickname {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.user-phone {
  font-size: 14px;
  color: #999;
  margin: 0 0 8px 0;
}

.user-meta {
  font-size: 13px;
  color: #666;
}

/* 功能菜单 */
.menu-section {
  background: #fff;
  margin: 0 16px 16px;
  border-radius: 12px;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background: #fef8f3;
}

.menu-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #f5e6d3 0%, #e8d5c0 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #d4a574;
  font-size: 20px;
}

.menu-content {
  flex: 1;
}

.menu-label {
  display: block;
  font-size: 16px;
  color: #333;
  margin-bottom: 4px;
}

.menu-desc {
  font-size: 12px;
  color: #999;
}

.menu-extra {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #ccc;
}

/* 退出登录 */
.logout-section {
  padding: 0 16px;
}

.logout-btn {
  width: 100%;
}

/* 响应式适配 */
@media (max-width: 750px) {
  .user-info-content {
    flex-direction: column;
    text-align: center;
  }
  
  .user-details {
    width: 100%;
  }
}

@media (min-width: 751px) {
  .profile-container {
    max-width: 750px;
    margin: 0 auto;
  }
}
</style>
