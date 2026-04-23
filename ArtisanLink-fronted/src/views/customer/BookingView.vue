<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { customerApi } from '@/api/customer'
import { ElMessage, ElMessageBox } from 'element-plus'
import wsClient from '@/utils/websocket'

const route = useRoute()
const router = useRouter()

// 从路由参数获取预约信息
const technicianId = computed(() => Number(route.query.technicianId))
const serviceId = computed(() => Number(route.query.serviceId))
const timeSlotId = computed(() => Number(route.query.timeSlotId))
const appointmentTime = computed(() => route.query.appointmentTime)

// 技师信息
const technician = ref({})

// 服务信息
const service = ref({})

// 预约表单
const bookingForm = reactive({
  technicianId: technicianId.value,
  serviceId: serviceId.value,
  timeSlotId: timeSlotId.value,
  appointmentTime: appointmentTime.value,
  remark: '',
  contactPhone: ''
})

// 加载状态
const loading = reactive({
  detail: false,
  submitting: false
})

// 防重复提交标记
let isSubmitting = false

// 加载技师和服务详情
async function loadDetails() {
  loading.detail = true
  try {
    // 加载技师详情
    const techRes = await customerApi.getTechnicianDetail(technicianId.value)
    if (techRes.code === 200 && techRes.data) {
      technician.value = techRes.data
    }
    
    // 从技师的服务列表中查找对应服务
    const foundService = techRes.data.services?.find(s => s.id === serviceId.value)
    if (foundService) {
      service.value = foundService
    }
  } catch (error) {
    console.error('加载详情失败:', error)
    ElMessage.error('加载详情失败')
  } finally {
    loading.detail = false
  }
}

// 验证手机号
function validatePhone(phone) {
  return /^1[3-9]\d{9}$/.test(phone)
}

// 提交预约
async function submitBooking() {
  // 防止重复提交
  if (isSubmitting || loading.submitting) {
    return
  }
  
  // 表单验证
  if (!bookingForm.contactPhone) {
    ElMessage.warning('请输入联系电话')
    return
  }
  
  if (!validatePhone(bookingForm.contactPhone)) {
    ElMessage.warning('请输入正确的手机号码')
    return
  }
  
  // 设置提交状态
  isSubmitting = true
  loading.submitting = true
  
  try {
    const res = await customerApi.submitAppointment(bookingForm)
    
    if (res.code === 200) {
      ElMessage.success('预约成功！等待商家接单')
      
      // 连接WebSocket接收订单状态通知
      wsClient.connect()
      
      // 延迟跳转到订单列表
      setTimeout(() => {
        router.replace('/customer/orders')
      }, 1500)
    } else if (res.code === 409) {
      // 时间片冲突
      ElMessageBox.alert(
        '该时间已被占用，请重新选择其他时间',
        '预约冲突',
        {
          confirmButtonText: '重新选择',
          type: 'warning'
        }
      ).then(() => {
        router.back()
      })
    } else {
      ElMessage.error(res.msg || '预约失败')
    }
  } catch (error) {
    console.error('提交预约失败:', error)
    
    // 处理超时或网络错误
    if (error.message?.includes('timeout')) {
      ElMessageBox.confirm(
        '请求超时，是否重试？',
        '提示',
        {
          confirmButtonText: '重试',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        submitBooking()
      }).catch(() => {
        router.back()
      })
    } else {
      ElMessage.error('预约失败，请稍后重试')
    }
  } finally {
    isSubmitting = false
    loading.submitting = false
  }
}

onMounted(async () => {
  await loadDetails()
  
  // 监听WebSocket订单通知
  wsClient.on('order_status_change', (data) => {
    if (data.orderId) {
      ElMessage.success(`订单状态已更新：${data.status}`)
    }
  })
})
</script>

<template>
  <div class="booking-container">
    <!-- 顶部导航 -->
    <header class="booking-header">
      <el-button 
        :icon="'ArrowLeft'" 
        circle 
        @click="router.back()"
        class="back-btn"
      />
      <h2 class="header-title">确认预约</h2>
    </header>

    <!-- 加载状态 -->
    <el-skeleton v-if="loading.detail" :rows="8" animated />

    <!-- 预约信息确认 -->
    <section v-else class="booking-info-section">
      <!-- 技师信息 -->
      <div class="info-card">
        <h3 class="card-title">技师信息</h3>
        <div class="tech-info">
          <img 
            :src="technician.avatar || '/images/default-avatar.png'" 
            :alt="technician.name"
            class="tech-avatar"
          />
          <div class="tech-detail">
            <h4>{{ technician.name }}</h4>
            <p class="tech-skills">{{ technician.skills?.join('、') }}</p>
          </div>
        </div>
      </div>

      <!-- 服务信息 -->
      <div class="info-card">
        <h3 class="card-title">服务项目</h3>
        <div class="service-info">
          <div class="service-name">{{ service.name }}</div>
          <div class="service-meta">
            <span class="duration">⏱️ {{ service.duration }}分钟</span>
            <span class="price">¥{{ service.price }}</span>
          </div>
          <p class="service-desc">{{ service.description }}</p>
        </div>
      </div>

      <!-- 预约时间 -->
      <div class="info-card">
        <h3 class="card-title">预约时间</h3>
        <div class="time-info">
          <el-icon><Calendar /></el-icon>
          <span>{{ appointmentTime }}</span>
        </div>
      </div>

      <!-- 联系信息 -->
      <div class="info-card">
        <h3 class="card-title">联系信息</h3>
        <el-form :model="bookingForm" label-width="0">
          <el-form-item prop="contactPhone">
            <el-input
              v-model="bookingForm.contactPhone"
              placeholder="请输入联系电话"
              maxlength="11"
              clearable
            >
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
      </div>

      <!-- 备注 -->
      <div class="info-card">
        <h3 class="card-title">备注（选填）</h3>
        <el-input
          v-model="bookingForm.remark"
          type="textarea"
          :rows="3"
          placeholder="如有特殊需求，请在此说明"
          maxlength="200"
          show-word-limit
        />
      </div>
    </section>

    <!-- 底部操作栏 -->
    <footer class="bottom-bar">
      <div class="total-price">
        <span class="label">应付金额：</span>
        <span class="price">¥{{ service.price }}</span>
      </div>
      <el-button 
        type="primary" 
        size="large"
        @click="submitBooking"
        :loading="loading.submitting"
        :disabled="loading.submitting"
      >
        确认预约
      </el-button>
    </footer>
  </div>
</template>

<style scoped>
.booking-container {
  min-height: 100vh;
  background: #f8f9fa;
  padding-bottom: 80px;
}

/* 顶部导航 */
.booking-header {
  background: linear-gradient(135deg, #d4a574 0%, #c9956b 100%);
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: #fff;
}

.header-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

/* 预约信息区 */
.booking-info-section {
  padding: 16px;
}

.info-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
  padding-left: 12px;
  border-left: 3px solid #d4a574;
}

/* 技师信息 */
.tech-info {
  display: flex;
  gap: 12px;
  align-items: center;
}

.tech-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
}

.tech-detail h4 {
  font-size: 16px;
  color: #333;
  margin: 0 0 8px 0;
}

.tech-skills {
  font-size: 13px;
  color: #999;
  margin: 0;
}

/* 服务信息 */
.service-info {
  padding: 12px;
  background: #fef8f3;
  border-radius: 8px;
}

.service-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.service-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.duration {
  font-size: 14px;
  color: #666;
}

.price {
  font-size: 18px;
  font-weight: 600;
  color: #d4a574;
}

.service-desc {
  font-size: 13px;
  color: #999;
  margin: 0;
  line-height: 1.5;
}

/* 时间信息 */
.time-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #333;
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 12px 16px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 16px;
}

.total-price {
  flex: 1;
}

.total-price .label {
  font-size: 14px;
  color: #666;
}

.total-price .price {
  font-size: 24px;
  font-weight: 600;
  color: #d4a574;
}

.bottom-bar .el-button {
  width: 140px;
}

/* 响应式适配 */
@media (max-width: 750px) {
  .bottom-bar {
    flex-direction: column;
    gap: 12px;
  }
  
  .bottom-bar .el-button {
    width: 100%;
  }
}

@media (min-width: 751px) {
  .booking-container {
    max-width: 750px;
    margin: 0 auto;
  }
}
</style>
