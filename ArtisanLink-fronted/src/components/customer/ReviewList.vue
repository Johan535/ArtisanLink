<template>
  <div class="review-list-container">
    <!-- 评价统计 -->
    <el-card shadow="hover" class="review-stats-card">
      <div class="stats-header">
        <span class="title">用户评价</span>
        <span class="total-count">共 {{ totalCount }} 条评价</span>
      </div>
      
      <div class="stats-content">
        <div class="average-rating">
          <div class="rating-value">{{ averageRating.toFixed(1) }}</div>
          <el-rate
            v-model="averageRating"
            disabled
            show-score
            score-template=""
            size="large"
          />
          <div class="rating-text">综合评分</div>
        </div>
        
        <div class="rating-distribution">
          <div v-for="(count, index) in ratingDistribution" :key="index" class="distribution-item">
            <span class="star-label">{{ 5 - index }}星</span>
            <el-progress
              :percentage="getPercentage(count)"
              :stroke-width="8"
              :show-text="false"
              status="success"
            />
            <span class="count-label">{{ count }}</span>
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 评价列表 -->
    <div class="review-items">
      <el-empty v-if="reviews.length === 0" description="暂无评价" />
      
      <el-card
        v-for="review in reviews"
        :key="review.id"
        shadow="hover"
        class="review-item-card"
      >
        <div class="review-header">
          <div class="user-info">
            <el-avatar :size="40" :src="review.userAvatar || defaultAvatar">
              {{ review.userName?.charAt(0) || '用' }}
            </el-avatar>
            <div class="user-detail">
              <div class="user-name">{{ review.isAnonymous ? '匿名用户' : review.userName }}</div>
              <div class="review-time">{{ formatTime(review.createTime) }}</div>
            </div>
          </div>
          
          <el-rate
            v-model="review.rating"
            disabled
            show-score
            score-template="{value}分"
            size="small"
          />
        </div>
        
        <div class="review-content">
          <p>{{ review.content }}</p>
        </div>
        
        <!-- 评价图片 -->
        <div v-if="review.images && review.images.length > 0" class="review-images">
          <el-image
            v-for="(image, index) in review.images"
            :key="index"
            :src="image"
            :preview-src-list="review.images"
            :initial-index="index"
            fit="cover"
            class="review-image"
          />
        </div>
        
        <!-- 各维度评分 -->
        <div class="dimension-ratings">
          <span class="dimension-item">服务: {{ review.serviceRating }}分</span>
          <span class="dimension-item">技术: {{ review.skillRating }}分</span>
          <span class="dimension-item">环境: {{ review.environmentRating }}分</span>
        </div>
        
        <!-- 商家回复 -->
        <div v-if="review.replyContent" class="merchant-reply">
          <div class="reply-header">
            <el-icon><ChatDotRound /></el-icon>
            <span>商家回复</span>
            <span class="reply-time">{{ formatTime(review.replyTime) }}</span>
          </div>
          <div class="reply-content">{{ review.replyContent }}</div>
        </div>
      </el-card>
      
      <!-- 加载更多 -->
      <div v-if="hasMore" class="load-more">
        <el-button @click="loadMore" :loading="loading">加载更多</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ChatDotRound } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { customerApi } from '@/api/customer'

const props = defineProps({
  technicianId: {
    type: [String, Number],
    required: true
  },
  orderId: {
    type: [String, Number],
    default: null
  }
})

// 状态
const loading = ref(false)
const reviews = ref([])
const totalCount = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const hasMore = ref(true)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 计算平均评分
const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0
  
  const sum = reviews.value.reduce((acc, review) => acc + review.rating, 0)
  return sum / reviews.value.length
})

// 计算各星级分布
const ratingDistribution = computed(() => {
  const distribution = [0, 0, 0, 0, 0] // 5星到1星
  
  reviews.value.forEach(review => {
    const index = 5 - review.rating
    if (index >= 0 && index < 5) {
      distribution[index]++
    }
  })
  
  return distribution
})

/**
 * 获取百分比
 */
function getPercentage(count) {
  if (totalCount.value === 0) return 0
  return Math.round((count / totalCount.value) * 100)
}

/**
 * 格式化时间
 */
function formatTime(time) {
  if (!time) return ''
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

/**
 * 加载评价列表
 */
async function loadReviews(isLoadMore = false) {
  if (loading.value) return
  
  loading.value = true
  
  try {
    const params = {
      page: isLoadMore ? currentPage.value + 1 : 1,
      pageSize: pageSize.value,
      technicianId: props.technicianId
    }
    
    if (props.orderId) {
      params.orderId = props.orderId
    }
    
    const res = await customerApi.getReviewList(params)
    
    if (res.code === 200) {
      const data = res.data
      
      if (isLoadMore) {
        reviews.value = [...reviews.value, ...(data.list || [])]
        currentPage.value++
      } else {
        reviews.value = data.list || []
        currentPage.value = 1
      }
      
      totalCount.value = data.total || 0
      hasMore.value = reviews.value.length < totalCount.value
    } else {
      console.error('加载评价失败:', res.msg)
    }
  } catch (error) {
    console.error('加载评价失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 加载更多
 */
function loadMore() {
  loadReviews(true)
}

/**
 * 刷新评价列表
 */
function refresh() {
  loadReviews(false)
}

// 暴露方法给父组件
defineExpose({
  refresh
})

onMounted(() => {
  loadReviews()
})
</script>

<style scoped>
.review-list-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.review-stats-card {
  border-radius: 8px;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.stats-header .title {
  font-size: 16px;
  font-weight: 600;
}

.stats-header .total-count {
  font-size: 14px;
  color: #909399;
}

.stats-content {
  display: flex;
  gap: 32px;
}

.average-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 120px;
}

.rating-value {
  font-size: 36px;
  font-weight: 600;
  color: #e6a23c;
  margin-bottom: 8px;
}

.rating-text {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.rating-distribution {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.distribution-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.star-label {
  width: 40px;
  font-size: 14px;
  color: #606266;
}

.count-label {
  width: 40px;
  text-align: right;
  font-size: 14px;
  color: #909399;
}

.review-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item-card {
  border-radius: 8px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.review-time {
  font-size: 12px;
  color: #909399;
}

.review-content {
  margin-bottom: 16px;
}

.review-content p {
  margin: 0;
  line-height: 1.6;
  color: #606266;
}

.review-images {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.review-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  cursor: pointer;
}

.dimension-ratings {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.dimension-item {
  font-size: 13px;
  color: #606266;
}

.merchant-reply {
  margin-top: 16px;
  padding: 16px;
  background: #fef0f0;
  border-radius: 4px;
  border-left: 3px solid #f56c6c;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.reply-time {
  font-size: 12px;
  color: #909399;
  font-weight: normal;
  margin-left: auto;
}

.reply-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.load-more {
  text-align: center;
  padding: 16px 0;
}
</style>
