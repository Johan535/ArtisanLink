<template>
  <customer-layout>
    <!-- 顶部导航 -->
    <div class="header-nav">
      <el-button @click="goBack" icon="ArrowLeft">返回</el-button>
      <h2>{{ categoryName || '服务列表' }}</h2>
    </div>
    
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select v-model="sortBy" placeholder="排序方式" style="width: 150px;">
        <el-option label="综合排序" value="default" />
        <el-option label="价格从低到高" value="price_asc" />
        <el-option label="价格从高到低" value="price_desc" />
        <el-option label="销量最高" value="sales" />
        <el-option label="评分最高" value="rating" />
      </el-select>
      
      <el-select v-model="priceRange" placeholder="价格区间" style="width: 150px; margin-left: 16px;">
        <el-option label="全部价格" value="all" />
        <el-option label="100元以下" value="0-100" />
        <el-option label="100-300元" value="100-300" />
        <el-option label="300-500元" value="300-500" />
        <el-option label="500元以上" value="500+" />
      </el-select>
    </div>
    
    <!-- 服务列表 -->
    <div class="service-container" v-loading="loading">
      <div
        v-for="service in services"
        :key="service.id"
        class="service-card"
        @click="handleServiceClick(service)"
      >
        <div class="service-image">
          <img :src="service.coverImage || '/default-service.png'" :alt="service.name" />
          <div class="service-badge" v-if="service.isHot">热门</div>
        </div>
        
        <div class="service-info">
          <h3 class="service-name">{{ service.name }}</h3>
          <p class="service-desc">{{ service.description }}</p>
          
          <div class="service-meta">
            <span class="duration">
              <el-icon><Clock /></el-icon>
              {{ service.duration }}分钟
            </span>
            <span class="rating">
              <el-icon><Star /></el-icon>
              {{ service.rating.toFixed(1) }}
            </span>
            <span class="sales">已售{{ service.salesCount }}</span>
          </div>
          
          <div class="service-price">
            <span class="price">¥{{ service.price }}</span>
            <span class="original-price" v-if="service.originalPrice && service.originalPrice > service.price">
              ¥{{ service.originalPrice }}
            </span>
          </div>
        </div>
      </div>
      
      <el-empty v-if="!loading && services.length === 0" description="暂无服务" />
      
      <!-- 加载更多 -->
      <div class="load-more" v-if="hasMore && !loading">
        <el-button @click="loadMore" :loading="loadingMore">
          {{ loadingMore ? '加载中...' : '加载更多' }}
        </el-button>
      </div>
    </div>
  </customer-layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Clock, Star, ArrowLeft } from '@element-plus/icons-vue'
import CustomerLayout from '@/components/CustomerLayout.vue'
import { customerApi } from '@/api/customer'

const router = useRouter()
const route = useRoute()

// 状态
const categoryName = ref(route.query.categoryName || '')
const categoryId = ref(route.query.categoryId || null)
const sortBy = ref('default')
const priceRange = ref('all')
const services = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = ref(10)

/**
 * 返回上一页
 */
function goBack() {
  router.back()
}

/**
 * 加载服务列表
 */
async function loadServices(isRefresh = false) {
  if (isRefresh) {
    currentPage.value = 1
    services.value = []
  }
  
  if (loading.value || loadingMore.value) return
  
  if (currentPage.value === 1) {
    loading.value = true
  } else {
    loadingMore.value = true
  }
  
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      categoryId: categoryId.value,
      sortType: sortBy.value,
      priceRange: priceRange.value !== 'all' ? priceRange.value : undefined
    }
    
    const res = await customerApi.getServiceList(params)
    
    if (res.code === 200) {
      const newServices = res.data?.list || []
      
      if (isRefresh) {
        services.value = newServices
      } else {
        services.value.push(...newServices)
      }
      
      hasMore.value = newServices.length >= pageSize.value
      currentPage.value++
    } else {
      ElMessage.error(res.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载服务列表失败:', error)
    ElMessage.error('加载失败，请稍后重试')
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

/**
 * 加载更多
 */
function loadMore() {
  loadServices(false)
}

/**
 * 处理服务点击
 */
function handleServiceClick(service) {
  // 跳转到技师详情页或预约页
  router.push({
    path: '/customer/technicians',
    query: { serviceId: service.id }
  })
}

onMounted(() => {
  loadServices(true)
})
</script>

<style scoped>
.header-nav {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.header-nav h2 {
  margin: 0 0 0 16px;
  font-size: 18px;
  font-weight: 600;
}

.filter-bar {
  padding: 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.service-container {
  padding: 16px;
  min-height: calc(100vh - 280px);
}

.service-card {
  display: flex;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s ease;
}

.service-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.service-image {
  position: relative;
  width: 120px;
  height: 120px;
  flex-shrink: 0;
}

.service-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.service-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.service-info {
  flex: 1;
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.service-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
}

.service-desc {
  font-size: 14px;
  color: #666;
  margin: 0 0 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.service-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.service-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.service-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price {
  font-size: 20px;
  font-weight: 700;
  color: #ff6b6b;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.load-more {
  text-align: center;
  padding: 16px 0;
}
</style>
