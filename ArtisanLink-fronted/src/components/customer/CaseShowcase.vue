<template>
  <div class="case-showcase-container">
    <el-card shadow="hover" class="showcase-card">
      <template #header>
        <div class="card-header">
          <span>精选案例</span>
          <el-button size="small" @click="viewAllCases">查看更多</el-button>
        </div>
      </template>
      
      <!-- 案例轮播 -->
      <el-carousel
        v-if="cases.length > 0"
        :interval="4000"
        type="card"
        height="300px"
        indicator-position="outside"
      >
        <el-carousel-item v-for="caseItem in cases" :key="caseItem.id">
          <div class="case-item">
            <el-image
              :src="caseItem.coverImage"
              fit="cover"
              class="case-image"
              lazy
            >
              <template #error>
                <div class="image-placeholder">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            
            <div class="case-info">
              <h3 class="case-title">{{ caseItem.title }}</h3>
              <p class="case-description">{{ caseItem.description }}</p>
              
              <div class="case-meta">
                <div class="technician-info">
                  <el-avatar :size="24" :src="caseItem.technicianAvatar">
                    {{ caseItem.technicianName?.charAt(0) || '技' }}
                  </el-avatar>
                  <span class="technician-name">{{ caseItem.technicianName }}</span>
                </div>
                
                <div class="stats">
                  <span class="stat-item">
                    <el-icon><View /></el-icon>
                    {{ formatNumber(caseItem.viewCount) }}
                  </span>
                  <span class="stat-item">
                    <el-icon><Star /></el-icon>
                    {{ formatNumber(caseItem.likeCount) }}
                  </span>
                </div>
              </div>
              
              <div class="case-tags">
                <el-tag
                  v-for="tag in caseItem.tags"
                  :key="tag"
                  size="small"
                  effect="plain"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
      
      <el-empty v-else description="暂无案例" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Picture, View, Star } from '@element-plus/icons-vue'
import { customerApi } from '@/api/customer'

const router = useRouter()

// 状态
const cases = ref([])
const loading = ref(false)

/**
 * 格式化数字
 */
function formatNumber(num) {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num
}

/**
 * 加载案例列表
 */
async function loadCases() {
  loading.value = true
  
  try {
    const res = await customerApi.getCaseList({
      page: 1,
      pageSize: 6,
      sortBy: 'hot' // 按热度排序
    })
    
    if (res.code === 200) {
      cases.value = res.data.list || []
    } else {
      console.error('加载案例失败:', res.msg)
    }
  } catch (error) {
    console.error('加载案例失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 查看所有案例
 */
function viewAllCases() {
  router.push('/customer/cases')
}

onMounted(() => {
  loadCases()
})
</script>

<style scoped>
.case-showcase-container {
  margin-bottom: 24px;
}

.showcase-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.case-item {
  display: flex;
  height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-radius: 8px;
  overflow: hidden;
}

.case-image {
  width: 40%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
  font-size: 48px;
}

.case-info {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.case-title {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
}

.case-description {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.case-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.technician-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.technician-name {
  font-size: 14px;
  color: #606266;
}

.stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.case-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

:deep(.el-carousel__item) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-carousel__indicator) {
  bottom: 10px;
}

@media (max-width: 768px) {
  .case-item {
    flex-direction: column;
  }
  
  .case-image {
    width: 100%;
    height: 200px;
  }
  
  .case-info {
    padding: 16px;
  }
  
  .case-title {
    font-size: 16px;
  }
  
  .case-description {
    font-size: 13px;
    -webkit-line-clamp: 2;
  }
}
</style>
