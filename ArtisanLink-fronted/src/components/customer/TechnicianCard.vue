<template>
  <el-card class="technician-card" shadow="hover" @click="handleClick">
    <div class="card-content">
      <!-- 头像 -->
      <div class="avatar-wrapper">
        <el-avatar :size="80" :src="technician.avatar || defaultAvatar" />
        <div class="distance-badge" v-if="technician.distance">
          <el-icon><Location /></el-icon>
          {{ formatDistance(technician.distance) }}
        </div>
      </div>
      
      <!-- 信息 -->
      <div class="info-wrapper">
        <div class="name-row">
          <h3 class="name">{{ technician.name }}</h3>
          <el-tag size="small" :type="getLevelType(technician.level)">
            {{ getLevelText(technician.level) }}
          </el-tag>
        </div>
        
        <div class="rating-row">
          <el-rate
            v-model="technician.rating"
            disabled
            show-score
            text-color="#d4a574"
            score-template="{value}"
            :max="5"
          />
          <span class="review-count">({{ technician.reviewCount || 0 }}条评价)</span>
        </div>
        
        <div class="skills-row" v-if="technician.skills && technician.skills.length > 0">
          <el-tag
            v-for="(skill, index) in technician.skills.slice(0, 3)"
            :key="index"
            size="small"
            effect="plain"
            class="skill-tag"
          >
            {{ skill }}
          </el-tag>
          <span class="more-skills" v-if="technician.skills.length > 3">
            +{{ technician.skills.length - 3 }}
          </span>
        </div>
        
        <div class="price-row" v-if="technician.minPrice">
          <span class="price-label">起步价：</span>
          <span class="price">¥{{ technician.minPrice }}</span>
        </div>
      </div>
    </div>
    
    <!-- 底部操作 -->
    <div class="card-footer">
      <el-button size="small" @click.stop="handleViewDetail">查看详情</el-button>
      <el-button type="primary" size="small" @click.stop="handleBook">立即预约</el-button>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { Location } from '@element-plus/icons-vue'

// 默认头像URL
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const props = defineProps({
  technician: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click', 'book'])

const router = useRouter()

/**
 * 格式化距离
 */
function formatDistance(distance) {
  if (distance < 1) {
    return `${Math.round(distance * 1000)}m`
  }
  return `${distance.toFixed(1)}km`
}

/**
 * 获取等级类型
 */
function getLevelType(level) {
  const typeMap = {
    'JUNIOR': 'info',
    'MIDDLE': '',
    'SENIOR': 'warning',
    'EXPERT': 'danger'
  }
  return typeMap[level] || ''
}

/**
 * 获取等级文本
 */
function getLevelText(level) {
  const textMap = {
    'JUNIOR': '初级',
    'MIDDLE': '中级',
    'SENIOR': '高级',
    'EXPERT': '专家'
  }
  return textMap[level] || level
}

/**
 * 处理卡片点击
 */
function handleClick() {
  emit('click', props.technician)
}

/**
 * 查看详情
 */
function handleViewDetail() {
  router.push(`/customer/technician/${props.technician.id}`)
}

/**
 * 立即预约
 */
function handleBook() {
  emit('book', props.technician)
  router.push({
    path: '/customer/booking',
    query: { technicianId: props.technician.id }
  })
}
</script>

<style scoped>
.technician-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.technician-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(212, 165, 116, 0.2);
}

.card-content {
  display: flex;
  gap: 16px;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.distance-badge {
  position: absolute;
  bottom: -8px;
  right: -8px;
  background: #d4a574;
  color: #fff;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.info-wrapper {
  flex: 1;
  min-width: 0;
}

.name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.name {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.rating-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.review-count {
  font-size: 12px;
  color: #999;
}

.skills-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 8px;
}

.skill-tag {
  background-color: #f9f3eb;
  color: #d4a574;
  border-color: #f0e0c8;
}

.more-skills {
  font-size: 12px;
  color: #999;
}

.price-row {
  font-size: 14px;
}

.price-label {
  color: #999;
}

.price {
  color: #d4a574;
  font-weight: bold;
  font-size: 16px;
}

.card-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
