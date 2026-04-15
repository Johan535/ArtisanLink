<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '../api'
import { setAdminInfo, setToken } from '../utils/auth'

const router = useRouter()
const loading = ref(false)
const errorMsg = ref('')

const form = reactive({
  username: 'admin',
  password: '123456',
  captcha: '1234',
  captchaKey: 'demo-captcha-key'
})

async function submitLogin() {
  errorMsg.value = ''
  loading.value = true
  try {
    const res = await adminApi.login(form)
    setToken(res.data?.token || '')
    setAdminInfo(res.data?.adminInfo || { username: form.username })
    router.push('/dashboard')
  } catch (error) {
    errorMsg.value = error?.response?.data?.msg || error?.message || '登录失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-panel">
      <h1>匠心美业管理系统</h1>
      <p class="sub-title">商户、员工、订单、经营数据一站式管理</p>

      <div class="form-grid">
        <label>账号</label>
        <input v-model="form.username" placeholder="请输入账号" />

        <label>密码</label>
        <input v-model="form.password" type="password" placeholder="请输入密码" />

        <label>验证码</label>
        <input v-model="form.captcha" placeholder="请输入验证码" />
      </div>

      <p v-if="errorMsg" class="error-tip">{{ errorMsg }}</p>
      <button class="btn btn-primary login-btn" :disabled="loading" @click="submitLogin">
        {{ loading ? '登录中...' : '立即登录' }}
      </button>
    </div>
  </div>
</template>
