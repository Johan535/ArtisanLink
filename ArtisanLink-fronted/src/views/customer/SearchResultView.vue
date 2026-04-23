<template>
  <customer-layout>
    <!-- 搜索栏 -->
    <search-bar 
      :placeholder="searchPlaceholder"
      :show-hot-searches="false"
      @search="handleSearch"
    />
    
    <!-- 筛选条件 -->
    <div class="filter-bar">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="综合" name="all" />
        <el-tab-pane label="距离最近" name="distance" />
        <el-tab-pane label="评分最高" name="rating" />
        <el-tab-pane label="价格最低" name="price" />
      </el-tabs>
      
      <el-dropdown trigger="click" @command="handleFilterCommand">
        <el-button size="small">
          筛选<el-icon class="el-icon--right"><ArrowDown /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="level">技师等级</el-dropdown-item>
            <el-dropdown-item command="service">服务类型</el-dropdown-item>
            <el-dropdown-item command="gender">性别</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    
    <!-- 搜索结果 -->
    <div class="result-container" v-loading="loading">
      <div class="result-header" v-if="keyword">
        <span>搜索 "{{ keyword }}" 找到 {{ total }} 个结果</span>
      </div>
      
      <div class="technician-list">
        <technician-card
          v-for="tech in technicians"
          :key="tech.id"
          :technician="tech"
          @book="handleBookTechnician"
        />
        
        <el-empty v-if="!loading && technicians.length === 0" description="未找到相关技师" />
      </div>
      
      <!-- 加载更多 -->
      <div class="load-more" v-if="hasMore && !loading">
        <el-button @click="loadMore" :loading="loadingMore">加载更多</el-button>
      </div>
    </div>
  </customer-layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import CustomerLayout from '@/components/CustomerLayout.vue'
import SearchBar from '@/components/customer/SearchBar.vue'
import TechnicianCard from '@/components/customer/TechnicianCard.vue'
import { customerApi } from '@/api/customer'

const route = useRoute()
const router = useRouter()

// 状态
const keyword = ref(route.query.keyword || '')
const activeTab = ref('all')
const technicians = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchPlaceholder = '搜索技师、服务名称'

/**
 * 处理搜索
 */
function handleSearch(newKeyword) {
  keyword.value = newKeyword
  currentPage.value = 1
  technicians.value = []
  loadResults(true)
}

/**
 * 处理标签切换
 */
function handleTabChange(tab) {
  currentPage.value = 1
  technicians.value = []
  loadResults(true)
}

/**
 * 处理筛选命令
 */
function handleFilterCommand(command) {
  console.log('筛选条件:', command)
  // TODO: 实现筛选逻辑
  ElMessage.info('筛选功能开发中')
}

/**
 * 加载搜索结果
 */
async function loadResults(isRefresh = false) {
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
      keyword: keyword.value,
      page: currentPage.value,
      pageSize: pageSize.value,
      sortType: activeTab.value
    }
    
    const res = await customerApi.searchTechnicians(params)
    
    if (res.code === 200) {
      const newTechnicians = res.data?.list || []
      total.value = res.data?.total || 0
      
      if (isRefresh) {
        technicians.value = newTechnicians
      } else {
        technicians.value.push(...newTechnicians)
      }
      
      hasMore.value = technicians.value.length < total.value
      currentPage.value++
    } else {
      ElMessage.error(res.msg || '搜索失败')
    }
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

/**
 * 加载更多
 */
function loadMore() {
  loadResults(false)
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
  if (keyword.value) {
    loadResults(true)
  }
})
</script>

<style scoped>
.filter-bar {
  background: #fff;
  padding: 0 16px;
  border-bottom: 1px solid #f0f0f0;
}

.result-container {
  padding: 16px;
  min-height: calc(100vh - 200px);
}

.result-header {
  margin-bottom: 16px;
  font-size: 14px;
  color: #666;
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
</style>
