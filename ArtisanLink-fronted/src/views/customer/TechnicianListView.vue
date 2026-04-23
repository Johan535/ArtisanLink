<template>
  <customer-layout>
    <!-- 顶部搜索 -->
    <search-bar @search="handleSearch" />
    
    <!-- 筛选栏 -->
    <div class="filter-section">
      <el-tabs v-model="activeFilter" @tab-change="handleFilterChange">
        <el-tab-pane label="推荐" name="recommend" />
        <el-tab-pane label="距离最近" name="distance" />
        <el-tab-pane label="评分最高" name="rating" />
        <el-tab-pane label="新人优惠" name="newcomer" />
      </el-tabs>
    </div>
    
    <!-- 技师列表 -->
    <div class="technician-container" v-loading="loading">
      <technician-card
        v-for="tech in technicians"
        :key="tech.id"
        :technician="tech"
        @book="handleBookTechnician"
      />
      
      <el-empty v-if="!loading && technicians.length === 0" description="暂无技师" />
      
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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import CustomerLayout from '@/components/CustomerLayout.vue'
import SearchBar from '@/components/customer/SearchBar.vue'
import TechnicianCard from '@/components/customer/TechnicianCard.vue'
import { customerApi } from '@/api/customer'

const router = useRouter()

// 状态
const activeFilter = ref('recommend')
const technicians = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = ref(10)

/**
 * 处理搜索
 */
function handleSearch(keyword) {
  router.push({
    path: '/customer/search',
    query: { keyword }
  })
}

/**
 * 处理筛选条件变化
 */
function handleFilterChange(filter) {
  currentPage.value = 1
  technicians.value = []
  loadTechnicians(true)
}

/**
 * 加载技师列表
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
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      sortType: activeFilter.value
    }
    
    // 获取用户位置
    const position = await getUserPosition()
    params.longitude = position.longitude
    params.latitude = position.latitude
    
    const res = await customerApi.getTechnicianList(params)
    
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
      ElMessage.error(res.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载技师列表失败:', error)
    ElMessage.error('加载失败，请稍后重试')
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
    setTimeout(() => {
      resolve({
        longitude: 116.397428,
        latitude: 39.90923
      })
    }, 500)
  })
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
  loadTechnicians(true)
})
</script>

<style scoped>
.filter-section {
  background: #fff;
  padding: 0 16px;
  border-bottom: 1px solid #f0f0f0;
}

.technician-container {
  padding: 16px;
  min-height: calc(100vh - 200px);
}

.load-more {
  text-align: center;
  padding: 16px 0;
}
</style>