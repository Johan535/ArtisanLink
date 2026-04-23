<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh, Star } from '@element-plus/icons-vue'
import CustomerLayout from '@/components/CustomerLayout.vue'
import SearchBar from '@/components/customer/SearchBar.vue'
import TechnicianCard from '@/components/customer/TechnicianCard.vue'
import CaseShowcase from '@/components/customer/CaseShowcase.vue'
import { customerApi } from '@/api/customer'

const router = useRouter()

// 状态
const banners = ref([
  { image: '/banner1.jpg', title: '春季美甲特惠' },
  { image: '/banner2.jpg', title: '专业发型设计' },
  { image: '/banner3.jpg', title: '面部护理套餐' }
])

const categories = ref([])
const technicians = ref([])
const cases = ref([
  { image: '/case1.jpg', title: '法式美甲案例', author: '用户A', likes: 128 },
  { image: '/case2.jpg', title: '短发造型分享', author: '用户B', likes: 96 },
  { image: '/case3.jpg', title: '补水护理效果', author: '用户C', likes: 85 }
])

const loading = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = ref(10)

/**
 * 加载服务分类
 */
async function loadCategories() {
  try {
    const res = await customerApi.getServiceCategories()
    if (res.code === 200) {
      categories.value = (res.data || []).map(cat => ({
        id: cat.id,
        name: cat.name,
        icon: getCategoryIcon(cat.name)
      }))
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

/**
 * 获取分类图标
 */
function getCategoryIcon(iconName) {
  const iconMap = {
    'hair': 'Scissors',
    'nail': 'Brush',
    'face': 'MagicStick',
    'body': 'User',
    'massage': 'CoffeeCup',
    'default': 'Grid'
  }
  return iconMap[iconName] || iconMap['default']
}

/**
 * 查看全部服务分类
 */
function viewAllCategories() {
  router.push('/customer/categories')
}

/**
 * 处理分类点击
 */
function handleCategoryClick(category) {
  router.push({
    path: '/customer/services',
    query: { categoryId: category.id, categoryName: category.name }
  })
}

/**
 * 加载附近技师列表
 */
async function loadTechnicians(isRefresh = false) {
  if (isRefresh) {
    currentPage.value = 1
    technicians.value = []
  }
  
  if (loading.value || loadingMore.value) return
  
  if (currentPage.value === 1) {
    loading.value = true
  } else {
    loadingMore.value = true
  }
  
  try {
    // 获取用户位置（实际项目中应使用高德/百度地图API）
    const position = await getUserPosition()
    
    const res = await customerApi.getNearbyTechnicians({
      longitude: position.longitude,
      latitude: position.latitude,
      radius: 3, // 3公里
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    if (res.code === 200) {
      const newTechnicians = res.data?.list || []
      
      if (isRefresh) {
        technicians.value = newTechnicians
      } else {
        technicians.value.push(...newTechnicians)
      }
      
      hasMore.value = newTechnicians.length >= pageSize.value
      currentPage.value++
    } else {
      ElMessage.error(res.msg || '加载技师列表失败')
    }
  } catch (error) {
    console.error('加载技师列表失败:', error)
    ElMessage.error('加载技师列表失败，请稍后重试')
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

/**
 * 获取用户位置（模拟）
 */
function getUserPosition() {
  return new Promise((resolve) => {
    // 实际项目中应使用 navigator.geolocation 或高德/百度地图API
    setTimeout(() => {
      resolve({
        longitude: 116.397428,
        latitude: 39.90923
      })
    }, 500)
  })
}

/**
 * 刷新技师列表
 */
function refreshTechnicians() {
  loadTechnicians(true)
}

/**
 * 加载更多
 */
function loadMore() {
  loadTechnicians(false)
}

/**
 * 处理预约技师
 */
function handleBookTechnician(technician) {
  router.push({
    path: '/customer/booking',
    query: { technicianId: technician.id }
  })
}

onMounted(() => {
  loadCategories()
  loadTechnicians(true)
})
</script>

<template>
  <customer-layout>
    <!-- Banner轮播 -->
    <el-carousel height="200px" class="banner-carousel">
      <el-carousel-item v-for="(item, index) in banners" :key="index">
        <img :src="item.image" :alt="item.title" class="banner-image" />
        <div class="banner-title">{{ item.title }}</div>
      </el-carousel-item>
    </el-carousel>
    
    <!-- 服务分类 -->
    <div class="section-title">
      <h3>服务分类</h3>
      <el-button link type="primary" @click="viewAllCategories">查看全部</el-button>
    </div>
    
    <div class="category-grid">
      <div
        v-for="category in categories.slice(0, 6)"
        :key="category.id"
        class="category-item"
        @click="handleCategoryClick(category)"
      >
        <div class="category-icon">
          <el-icon :size="24">
            <component :is="getCategoryIcon(category.icon)" />
          </el-icon>
        </div>
        <div class="category-name">{{ category.name }}</div>
      </div>
    </div>
    
    <!-- 案例种草板块 -->
    <CaseShowcase />
    
    <!-- 附近技师 -->
    <div class="section">
      <div class="section-header">
        <h2 class="section-title">附近技师</h2>
        <el-button link type="primary" @click="refreshTechnicians">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
      
      <div class="technician-list" v-loading="loading">
        <technician-card
          v-for="tech in technicians"
          :key="tech.id"
          :technician="tech"
          @book="handleBookTechnician"
        />
        
        <el-empty v-if="!loading && technicians.length === 0" description="暂无附近技师" />
        
        <!-- 加载更多 -->
        <div class="load-more" v-if="hasMore && !loading">
          <el-button @click="loadMore" :loading="loadingMore">加载更多</el-button>
        </div>
      </div>
    </div>
    
    <!-- 案例种草板块 -->
    <div class="section">
      <div class="section-header">
        <h2 class="section-title">精选案例</h2>
        <el-button link type="primary">查看更多</el-button>
      </div>
      
      <div class="case-grid">
        <div v-for="(caseItem, index) in cases" :key="index" class="case-item">
          <img :src="caseItem.image" :alt="caseItem.title" class="case-image" />
          <div class="case-info">
            <div class="case-title">{{ caseItem.title }}</div>
            <div class="case-meta">
              <span>{{ caseItem.author }}</span>
              <span><el-icon><Star /></el-icon> {{ caseItem.likes }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </customer-layout>
</template>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.banner-carousel {
  margin-bottom: 16px;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-title {
  position: absolute;
  bottom: 20px;
  left: 20px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #fff;
}

.section-title h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  padding: 0 16px 16px;
  background: #fff;
  margin-bottom: 16px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
}

.category-item:hover {
  transform: scale(1.05);
}

.category-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.category-name {
  font-size: 12px;
  color: #666;
}

.section {
  background: #fff;
  margin-bottom: 16px;
  padding: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header .section-title {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.technician-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.load-more {
  text-align: center;
  padding: 16px 0;
}

.case-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.case-item {
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.case-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.case-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.case-info {
  padding: 8px;
  background: #fff;
}

.case-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.case-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .category-grid {
    grid-template-columns: repeat(3, 1fr);
    padding: 0 12px 12px;
    gap: 8px;
  }
  
  .category-icon {
    font-size: 28px;
  }
  
  .category-name {
    font-size: 11px;
  }
  
  .case-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>