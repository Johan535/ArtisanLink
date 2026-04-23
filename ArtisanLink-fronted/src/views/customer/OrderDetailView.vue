<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { customerApi } from '@/api/customer'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 订单ID
const orderId = ref(Number(route.params.id))

// 订单详情
const orderDetail = ref({})

// 加载状态
const loading = reactive({
  detail: false,
  cancelling: false
})

// 评价对话框
const reviewDialogVisible = ref(false)
const reviewForm = reactive({
  orderId: orderId.value,
  rating: 5,
  content: '',
  images: []
})

// 加载订单详情
async function loadOrderDetail() {
  loading.detail = true
  try {
    const res = await customerApi.getOrderDetail(orderId.value)
    if (res.code === 200 && res.data) {
      orderDetail.value = res.data
    } else {
      ElMessage.error(res.msg || '加载订单详情失败')
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.detail = false
  }
}

// 取消订单
async function cancelOrder() {
  try {
    await ElMessageBox.confirm(
      '确定要取消该订单吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    loading.cancelling = true
    const res = await customerApi.cancelOrder(orderId.value)
    
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      // 重新加载订单详情
      await loadOrderDetail()
    } else {
      ElMessage.error(res.msg || '取消失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消失败')
    }
  } finally {
    loading.cancelling = false
  }
}

// 打开评价对话框
function openReviewDialog() {
  reviewForm.orderId = orderId.value
  reviewForm.rating = 5
  reviewForm.content = ''
  reviewForm.images = []
  reviewDialogVisible.value = true
}

// 提交评价
async function submitReview() {
  if (!reviewForm.content?.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  
  try {
    const res = await customerApi.submitReview(reviewForm)
    if (res.code === 200) {
      ElMessage.success('评价提交成功')
      reviewDialogVisible.value = false
      // 重新加载订单详情
      await loadOrderDetail()
    } else {
      ElMessage.error(res.msg || '提交失败')
    }
  } catch (error) {
    console.error('提交评价失败:', error)
    ElMessage.error('提交失败')
  }
}

// 获取订单状态文本
function getStatusText(status) {
  const statusTexts = {
    0: '待接单',
    1: '已接单',
    2: '已完成',
    3: '已取消'
  }
  return statusTexts[status] || '未知'
}

// 获取订单状态样式类
function getStatusClass(status) {
  const statusClasses = {
    0: 'status-pending',
    1: 'status-accepted',
    2: 'status-completed',
    3: 'status-cancelled'
  }
  return statusClasses[status] || ''
}

onMounted(async () => {
  await loadOrderDetail()
})
</script>

<template>
  <div class="order-detail-container">
    <!-- 顶部导航 -->
    <header class="detail-header">
      <el-button 
        :icon="'ArrowLeft'" 
        circle 
        @click="router.back()"
        class="back-btn"
      />
      <h2 class="header-title">订单详情</h2>
    </header>

    <!-- 加载状态 -->
    <el-skeleton v-if="loading.detail" :rows="10" animated />

    <!-- 订单详情内容 -->
    <section v-else class="detail-content">
      <!-- 订单状态卡片 -->
      <div class="status-card">
        <div :class="['status-icon', getStatusClass(orderDetail.status)]">
          <el-icon :size="32">
            <component :is="orderDetail.status === 2 ? 'CircleCheck' : orderDetail.status === 3 ? 'CircleClose' : 'Clock'" />
          </el-icon>
        </div>
        <div class="status-info">
          <h3 :class="['status-text', getStatusClass(orderDetail.status)]">
            {{ getStatusText(orderDetail.status) }}
          </h3>
          <p class="status-desc">{{ orderDetail.statusDesc || '' }}</p>
        </div>
      </div>

      <!-- 订单信息 -->
      <div class="info-section">
        <h3 class="section-title">订单信息</h3>
        <div class="info-list">
          <div class="info-item">
            <span class="label">订单号：</span>
            <span class="value">{{ orderDetail.orderNo }}</span>
          </div>
          <div class="info-item">
            <span class="label">下单时间：</span>
            <span class="value">{{ orderDetail.createTime }}</span>
          </div>
          <div class="info-item">
            <span class="label">预约时间：</span>
            <span class="value">{{ orderDetail.appointmentTime }}</span>
          </div>
        </div>
      </div>

      <!-- 技师信息 -->
      <div class="info-section">
        <h3 class="section-title">技师信息</h3>
        <div class="tech-info">
          <img 
            :src="orderDetail.technicianAvatar || '/images/default-avatar.png'" 
            :alt="orderDetail.technicianName"
            class="tech-avatar"
          />
          <div class="tech-detail">
            <h4>{{ orderDetail.technicianName }}</h4>
            <p>{{ orderDetail.technicianPhone }}</p>
          </div>
        </div>
      </div>

      <!-- 服务信息 -->
      <div class="info-section">
        <h3 class="section-title">服务项目</h3>
        <div class="service-info">
          <div class="service-name">{{ orderDetail.serviceName }}</div>
          <div class="service-meta">
            <span>时长：{{ orderDetail.serviceDuration }}分钟</span>
            <span class="price">¥{{ orderDetail.amount }}</span>
          </div>
        </div>
      </div>

      <!-- 联系信息 -->
      <div class="info-section">
        <h3 class="section-title">联系信息</h3>
        <div class="contact-info">
          <div class="contact-item">
            <el-icon><Phone /></el-icon>
            <span>{{ orderDetail.contactPhone }}</span>
          </div>
          <div v-if="orderDetail.remark" class="remark">
            <span class="label">备注：</span>
            <span>{{ orderDetail.remark }}</span>
          </div>
        </div>
      </div>

      <!-- 订单进度 -->
      <div v-if="orderDetail.status > 0" class="info-section">
        <h3 class="section-title">订单进度</h3>
        <el-timeline>
          <el-timeline-item
            v-for="(item, index) in orderDetail.timeline || []"
            :key="index"
            :timestamp="item.time"
            :type="index === 0 ? 'primary' : 'info'"
          >
            {{ item.content }}
          </el-timeline-item>
        </el-timeline>
      </div>
    </section>

    <!-- 底部操作栏 -->
    <footer v-if="!loading.detail" class="bottom-bar">
      <el-button 
        v-if="orderDetail.status === 0"
        @click="cancelOrder"
        :loading="loading.cancelling"
      >
        取消订单
      </el-button>
      
      <el-button 
        v-if="orderDetail.status === 2 && !orderDetail.hasReviewed"
        type="primary"
        @click="openReviewDialog"
      >
        去评价
      </el-button>
      
      <el-button 
        type="default"
        @click="router.push('/customer/orders')"
      >
        返回订单列表
      </el-button>
    </footer>

    <!-- 评价对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      title="发表评价"
      width="90%"
      max-width="500px"
    >
      <el-form :model="reviewForm" label-width="0">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" show-text />
        </el-form-item>
        
        <el-form-item label="评价内容">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请分享您的体验感受..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="上传图片">
          <el-upload
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :limit="3"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.order-detail-container {
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

/* 详情内容区 */
.detail-content {
  padding: 16px;
}

/* 状态卡片 */
.status-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.status-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-pending {
  background: #fef0e6;
  color: #e6a23c;
}

.status-accepted {
  background: #e6f7ff;
  color: #409eff;
}

.status-completed {
  background: #e6ffed;
  color: #67c23a;
}

.status-cancelled {
  background: #f5f5f5;
  color: #909399;
}

.status-info {
  flex: 1;
}

.status-text {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.status-desc {
  font-size: 14px;
  color: #999;
  margin: 0;
}

/* 通用标题 */
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
  padding-left: 12px;
  border-left: 3px solid #d4a574;
}

/* 信息区块 */
.info-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  font-size: 14px;
}

.info-item .label {
  color: #999;
  width: 100px;
  flex-shrink: 0;
}

.info-item .value {
  color: #333;
  flex: 1;
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
  margin: 0 0 4px 0;
}

.tech-detail p {
  font-size: 14px;
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
  font-size: 14px;
  color: #666;
}

.service-meta .price {
  font-size: 18px;
  font-weight: 600;
  color: #d4a574;
}

/* 联系信息 */
.contact-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #333;
}

.remark {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.remark .label {
  color: #999;
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
  gap: 12px;
}

.bottom-bar .el-button {
  flex: 1;
}

/* 响应式适配 */
@media (max-width: 750px) {
  .bottom-bar {
    flex-direction: column;
  }
}

@media (min-width: 751px) {
  .order-detail-container {
    max-width: 750px;
    margin: 0 auto;
  }
}
</style>
