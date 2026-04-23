<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { customerApi } from '@/api/customer'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

// 状态
const loading = ref(false)
const technicianInfo = ref({})
const serviceList = ref([])
const timeSlots = ref([])
const selectedDate = ref('')
const selectedTimeSlot = ref(null)
const reviewListRef = ref(null)
const technicianId = computed(() => route.params.id)

// 技师详情数据
const technician = ref({})

// 当前选择的日期
// const selectedDate = ref(dayjs().format('YYYY-MM-DD'))

// 当前选择的时间片
// const selectedTimeSlot = ref(null)

// 服务列表
const services = ref([])

// 当前选择的服务
const selectedService = ref(null)

// 加载状态
const loadingState = reactive({
  detail: false,
  timeSlots: false,
  submitting: false
})

// 加载技师详情
async function loadTechnicianDetail() {
  loadingState.detail = true
  try {
    const res = await customerApi.getTechnicianDetail(technicianId.value)
    if (res.code === 200 && res.data) {
      technician.value = res.data
      
      // 加载服务列表
      services.value = res.data.services || []
      if (services.value.length > 0) {
        selectedService.value = services.value[0]
      }
    }
  } catch (error) {
    console.error('加载技师详情失败:', error)
    ElMessage.error('加载技师详情失败')
  } finally {
    loadingState.detail = false
  }
}

// 加载可预约时间片
async function loadTimeSlots(date) {
  loadingState.timeSlots = true
  try {
    const res = await customerApi.getAvailableTimeSlots({
      technicianId: technicianId.value,
      date: date
    })
    
    if (res.code === 200 && res.data) {
      timeSlots.value = res.data.list || []
    }
  } catch (error) {
    console.error('加载时间片失败:', error)
    ElMessage.error('加载时间片失败')
  } finally {
    loadingState.timeSlots = false
  }
}

// 切换日期
function handleDateChange(date) {
  selectedDate.value = dayjs(date).format('YYYY-MM-DD')
  selectedTimeSlot.value = null
  loadTimeSlots(selectedDate.value)
}

// 选择时间片
function selectTimeSlot(slot) {
  if (slot.status === 1) {
    ElMessage.warning('该时间片已被占用，请选择其他时间')
    return
  }
  
  if (selectedTimeSlot.value?.id === slot.id) {
    selectedTimeSlot.value = null
  } else {
    selectedTimeSlot.value = slot
  }
}

// 选择服务
function selectService(service) {
  selectedService.value = service
}

// 跳转到预约页面
function goToBooking() {
  if (!selectedService.value) {
    ElMessage.warning('请选择服务项目')
    return
  }
  
  if (!selectedTimeSlot.value) {
    ElMessage.warning('请选择预约时间')
    return
  }
  
  router.push({
    path: '/customer/booking',
    query: {
      technicianId: technicianId.value,
      serviceId: selectedService.value.id,
      timeSlotId: selectedTimeSlot.value.id,
      appointmentTime: selectedTimeSlot.value.startTime
    }
  })
}

// 禁用过去的日期
function disabledDate(time) {
  return time.getTime() < Date.now() - 8.64e7
}

/**
 * 显示所有评价
 */
function showAllReviews() {
  // 可以跳转到专门的评价页面或展开更多评价
  ElMessage.info('功能开发中')
}

/**
 * 刷新评价列表
 */
function refreshReviews() {
  if (reviewListRef.value) {
    reviewListRef.value.refresh()
  }
}

onMounted(async () => {
  await loadTechnicianDetail()
  await loadTimeSlots(selectedDate.value)
})
</script>

<template>
  <div class="technician-detail-container">
    <!-- 顶部导航 -->
    <header class="detail-header">
      <el-button 
        :icon="'ArrowLeft'" 
        circle 
        @click="router.back()"
        class="back-btn"
      />
      <h2 class="header-title">技师详情</h2>
    </header>

    <!-- 加载状态 -->
    <el-skeleton v-if="loadingState.detail" :rows="10" animated />

    <!-- 技师基本信息 -->
    <section v-else class="tech-info-section">
      <div class="tech-profile">
        <img 
          :src="technician.avatar || '/images/default-avatar.png'" 
          :alt="technician.name"
          class="tech-avatar"
        />
        <div class="tech-basic">
          <h3 class="tech-name">{{ technician.name }}</h3>
          <div class="tech-rating">
            <el-rate v-model="technician.rating" disabled show-score text-color="#ff9900" />
            <span class="rating-text">{{ technician.rating }}分</span>
          </div>
          <p class="tech-exp">从业 {{ technician.workYears || 0 }} 年</p>
        </div>
      </div>
      
      <!-- 技能标签 -->
      <div class="skill-tags">
        <el-tag 
          v-for="skill in technician.skills" 
          :key="skill"
          effect="plain"
          size="large"
        >
          {{ skill }}
        </el-tag>
      </div>
      
      <!-- 统计数据 -->
      <div class="tech-stats">
        <div class="stat-item">
          <span class="stat-value">{{ technician.completedOrders || 0 }}</span>
          <span class="stat-label">完成订单</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ technician.goodRate || 0 }}%</span>
          <span class="stat-label">好评率</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ technician.responseTime || 0 }}min</span>
          <span class="stat-label">平均响应</span>
        </div>
      </div>
    </section>

    <!-- 服务列表 -->
    <section class="service-section">
      <h3 class="section-title">服务项目</h3>
      <div class="service-list">
        <div 
          v-for="service in services" 
          :key="service.id"
          :class="['service-item', { active: selectedService?.id === service.id }]"
          @click="selectService(service)"
        >
          <div class="service-info">
            <h4>{{ service.name }}</h4>
            <p class="service-desc">{{ service.description }}</p>
            <p class="service-duration">⏱️ {{ service.duration }}分钟</p>
          </div>
          <div class="service-price">
            <span class="price">¥{{ service.price }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 可预约时间 -->
    <section class="time-slot-section">
      <h3 class="section-title">可预约时间</h3>
      
      <!-- 日期选择器 -->
      <el-date-picker
        v-model="selectedDate"
        type="date"
        placeholder="选择日期"
        :disabled-date="disabledDate"
        @change="handleDateChange"
        class="date-picker"
      />
      
      <!-- 时间片列表 -->
      <div v-if="loadingState.timeSlots" class="time-loading">
        <el-loading />
      </div>
      
      <div v-else class="time-slots-grid">
        <div 
          v-for="slot in timeSlots" 
          :key="slot.id"
          :class="[
            'time-slot',
            { 
              selected: selectedTimeSlot?.id === slot.id,
              occupied: slot.status === 1,
              available: slot.status === 0
            }
          ]"
          @click="selectTimeSlot(slot)"
        >
          <span class="time-text">{{ slot.startTime }} - {{ slot.endTime }}</span>
          <span v-if="slot.status === 1" class="status-text">已占用</span>
          <span v-else-if="selectedTimeSlot?.id === slot.id" class="status-text">已选择</span>
          <span v-else class="status-text">可选</span>
        </div>
      </div>
      
      <el-empty 
        v-if="!loadingState.timeSlots && timeSlots.length === 0" 
        description="该日期暂无可预约时间"
      />
    </section>

    <!-- 用户评价 -->
    <el-card shadow="hover" class="review-section">
      <template #header>
        <div class="card-header">
          <span>用户评价</span>
          <el-button size="small" @click="showAllReviews">查看全部</el-button>
        </div>
      </template>
      
      <ReviewList ref="reviewListRef" :technician-id="technicianId" />
    </el-card>

    <!-- 底部操作栏 -->
    <footer class="bottom-bar">
      <el-button 
        type="primary" 
        size="large"
        :disabled="!selectedService || !selectedTimeSlot"
        @click="goToBooking"
        :loading="loadingState.submitting"
      >
        立即预约
      </el-button>
    </footer>
  </div>
</template>

<style scoped>
.technician-detail-container {
  min-height: 100vh;
  background: #f8f9fa;
  padding-bottom: 80px;
}

/* 顶部导航 */
.detail-header {
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

/* 技师信息区 */
.tech-info-section {
  background: #fff;
  margin: 16px;
  padding: 20px;
  border-radius: 12px;
}

.tech-profile {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.tech-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.tech-basic {
  flex: 1;
}

.tech-name {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.tech-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.rating-text {
  font-size: 14px;
  color: #ff9900;
}

.tech-exp {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.skill-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.tech-stats {
  display: flex;
  justify-content: space-around;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: 600;
  color: #d4a574;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

/* 通用标题 */
.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
  padding-left: 12px;
  border-left: 4px solid #d4a574;
}

/* 服务列表 */
.service-section {
  background: #fff;
  margin: 0 16px 16px;
  padding: 20px;
  border-radius: 12px;
}

.service-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.service-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 2px solid #eee;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.service-item:hover {
  border-color: #d4a574;
}

.service-item.active {
  border-color: #d4a574;
  background: #fef8f3;
}

.service-info {
  flex: 1;
}

.service-info h4 {
  font-size: 16px;
  color: #333;
  margin: 0 0 8px 0;
}

.service-desc {
  font-size: 13px;
  color: #999;
  margin: 0 0 8px 0;
}

.service-duration {
  font-size: 12px;
  color: #666;
  margin: 0;
}

.service-price .price {
  font-size: 20px;
  font-weight: 600;
  color: #d4a574;
}

/* 时间片选择 */
.time-slot-section {
  background: #fff;
  margin: 0 16px 16px;
  padding: 20px;
  border-radius: 12px;
}

.date-picker {
  width: 100%;
  margin-bottom: 16px;
}

.time-loading {
  text-align: center;
  padding: 20px;
}

.time-slots-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.time-slot {
  padding: 12px 8px;
  border: 2px solid #eee;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.time-slot.available:hover {
  border-color: #d4a574;
}

.time-slot.selected {
  border-color: #d4a574;
  background: #d4a574;
  color: #fff;
}

.time-slot.occupied {
  border-color: #ddd;
  background: #f5f5f5;
  cursor: not-allowed;
  opacity: 0.6;
}

.time-text {
  display: block;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}

.status-text {
  display: block;
  font-size: 12px;
  color: inherit;
  opacity: 0.8;
}

/* 评价列表 */
.review-section {
  background: #fff;
  margin: 0 16px 16px;
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
}

.bottom-bar .el-button {
  width: 100%;
}

/* 响应式适配 */
@media (max-width: 750px) {
  .time-slots-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 751px) {
  .technician-detail-container {
    max-width: 750px;
    margin: 0 auto;
  }
}
</style>