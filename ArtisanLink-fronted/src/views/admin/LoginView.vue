<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>匠心美业O2O平台</h2>
          <p class="subtitle">中高端美业服务专家</p>
        </div>
      </template>
      
      <!-- 登录类型选择 -->
      <el-tabs v-model="loginType" class="login-tabs">
        <el-tab-pane label="店长登录" name="merchant">
          <el-form :model="merchantForm" label-width="80px" @submit.prevent="handleMerchantLogin">
            <el-form-item label="用户名">
              <el-input
                v-model="merchantForm.username"
                placeholder="请输入用户名"
                prefix-icon="User"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="密码">
              <el-input
                v-model="merchantForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
                clearable
              />
            </el-form-item>
            
            <el-form-item label="验证码">
              <el-row :gutter="10">
                <el-col :span="16">
                  <el-input
                    v-model="merchantForm.captcha"
                    placeholder="请输入验证码"
                    prefix-icon="Key"
                    clearable
                  />
                </el-col>
                <el-col :span="8">
                  <el-button @click="refreshCaptcha('merchant')" :loading="captchaLoading.merchant">
                    获取验证码
                  </el-button>
                </el-col>
              </el-row>
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                class="login-btn"
                @click="handleMerchantLogin"
                :loading="loading.merchant"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="用户登录" name="customer">
          <el-form :model="customerForm" label-width="80px" @submit.prevent="handleCustomerLogin">
            <el-form-item label="手机号">
              <el-input
                v-model="customerForm.phone"
                placeholder="请输入手机号"
                prefix-icon="Phone"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="验证码">
              <el-row :gutter="10">
                <el-col :span="16">
                  <el-input
                    v-model="customerForm.code"
                    placeholder="请输入验证码"
                    prefix-icon="Key"
                    clearable
                  />
                </el-col>
                <el-col :span="8">
                  <el-button @click="sendSmsCode" :loading="smsLoading">
                    发送验证码
                  </el-button>
                </el-col>
              </el-row>
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                class="login-btn"
                @click="handleCustomerLogin"
                :loading="loading.customer"
              >
                登录/注册
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      
      <div class="error-msg" v-if="errorMsg">
        <el-alert :title="errorMsg" type="error" show-icon :closable="false" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi, customerApi } from '../../api'
import { setAdminInfo, setToken, setUserInfo } from '../../utils/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 登录类型：merchant(店长) / customer(用户)
const loginType = ref('merchant')

// 错误信息
const errorMsg = ref('')

// 加载状态
const loading = reactive({
  merchant: false,
  customer: false
})

const captchaLoading = reactive({
  merchant: false,
  customer: false
})

const smsLoading = ref(false)

// 店长登录表单
const merchantForm = reactive({
  username: '',
  password: '',
  captcha: '',
  captchaKey: ''
})

// 用户登录表单
const customerForm = reactive({
  phone: '',
  code: ''
})

// 验证手机号格式
function validatePhone(phone) {
  return /^1[3-9]\d{9}$/.test(phone)
}

// 获取验证码（店长）
async function refreshCaptcha(type) {
  captchaLoading[type] = true
  try {
    // TODO: 调用后端获取验证码接口
    // const res = await adminApi.getCaptcha()
    // merchantForm.captchaKey = res.data.key
    ElMessage.info('验证码功能待实现')
  } catch (error) {
    ElMessage.error('获取验证码失败')
  } finally {
    captchaLoading[type] = false
  }
}

// 发送短信验证码（用户）
async function sendSmsCode() {
  if (!validatePhone(customerForm.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  
  smsLoading.value = true
  try {
    // TODO: 调用后端发送短信验证码接口
    // await customerApi.sendSmsCode(customerForm.phone)
    ElMessage.success('验证码已发送（模拟）')
  } catch (error) {
    ElMessage.error('发送验证码失败')
  } finally {
    smsLoading.value = false
  }
}

// 店长登录
async function handleMerchantLogin() {
  errorMsg.value = ''
  
  if (!merchantForm.username || !merchantForm.password) {
    errorMsg.value = '请输入用户名和密码'
    return
  }
  
  loading.merchant = true
  try {
    const res = await adminApi.login(merchantForm)
    
    if (res.data?.token) {
      setToken(res.data.token)
      setAdminInfo(res.data.adminInfo || { username: merchantForm.username })
      
      // 设置用户信息到store
      const userInfo = {
        ...res.data.adminInfo,
        role: 'merchant',
        type: 'merchant'
      }
      setUserInfo(userInfo)
      
      ElMessage.success('登录成功')
      router.push('/admin/dashboard')
    } else {
      errorMsg.value = '登录失败，请检查账号密码'
    }
  } catch (error) {
    errorMsg.value = error.message || '登录失败，请稍后重试'
    console.error(error)
  } finally {
    loading.merchant = false
  }
}

// 用户登录/注册
async function handleCustomerLogin() {
  errorMsg.value = ''
  
  if (!validatePhone(customerForm.phone)) {
    errorMsg.value = '请输入正确的手机号'
    return
  }
  
  if (!customerForm.code) {
    errorMsg.value = '请输入验证码'
    return
  }
  
  loading.customer = true
  try {
    // TODO: 调用后端用户登录接口
    // const res = await customerApi.loginWithCode(customerForm.phone, customerForm.code)
    
    // 模拟登录成功
    const mockData = {
      token: 'mock_customer_token_' + Date.now(),
      userInfo: {
        id: Date.now(),
        phone: customerForm.phone,
        nickname: '用户' + customerForm.phone.slice(-4),
        avatar: '',
        role: 'customer',
        type: 'customer'
      }
    }
    
    setToken(mockData.token)
    setUserInfo(mockData.userInfo)
    
    ElMessage.success('登录成功')
    router.push('/customer/home')
  } catch (error) {
    errorMsg.value = error.message || '登录失败，请稍后重试'
    console.error(error)
  } finally {
    loading.customer = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #d4a574 0%, #c9945f 50%, #b8834f 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 450px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.card-header {
  text-align: center;
  padding: 20px 0;
}

.card-header h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  color: #d4a574;
  font-weight: bold;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.login-tabs {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  background: linear-gradient(135deg, #d4a574 0%, #c9945f 100%);
  border: none;
}

.login-btn:hover {
  background: linear-gradient(135deg, #c9945f 0%, #b8834f 100%);
}

.error-msg {
  margin-top: 20px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .login-card {
    max-width: 100%;
  }
  
  .card-header h2 {
    font-size: 24px;
  }
}
</style>
