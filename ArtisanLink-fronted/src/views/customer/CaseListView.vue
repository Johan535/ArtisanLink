<template>
  <customer-layout>
    <div class="case-list-container">
      <!-- 筛选栏 -->
      <el-card shadow="hover" class="filter-card">
        <div class="filter-bar">
          <el-select
            v-model="filterCategory"
            placeholder="服务分类"
            clearable
            style="width: 150px;"
            @change="handleFilterChange"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
          
          <el-select
            v-model="filterSort"
            placeholder="排序方式"
            style="width: 150px; margin-left: 16px;"
            @change="handleFilterChange"
          >
            <el-option label="最新" value="latest" />
            <el-option label="最热" value="hot" />
            <el-option label="最多点赞" value="likes" />
          </el-select>
          
          <el-input
            v-model="searchKeyword"
            placeholder="搜索案例标题或描述"
            clearable
            style="width: 300px; margin-left: 16px;"
            @clear="handleFilterChange"
            @keyup.enter="handleFilterChange"
          >
            <template #append>
              <el-button @click="handleFilterChange">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </el-card>
      
      <!-- 案例列表 -->
      <div class="case-grid" v-loading="loading">
        <el-card
          v-for="caseItem in cases"
          :key="caseItem.id"
          shadow="hover"
          class="case-card"
          @click="viewCaseDetail(caseItem.id)"
        >
          <el-image
            :src="caseItem.coverImage"
            fit="cover"
            class="case-cover"
            lazy
          >
            <template #error>
              <div class="image-placeholder">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          
          <div class="case-content">
            <h3 class="case-title">{{ caseItem.title }}</h3>
            <p class="case-description">{{ caseItem.description }}</p>
            
            <div class="case-footer">
              <div class="technician-info">
                <el-avatar :size="24" :src="caseItem.technicianAvatar">
                  {{ caseItem.technicianName?.charAt(0) || '技' }}
                </el-avatar>
                <span>{{ caseItem.technicianName }}</span>
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
                v-for="tag in caseItem.tags.slice(0, 3)"
                :key="tag"
                size="small"
                effect="plain"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[12, 24, 36, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadCases"
        @size-change="loadCases"
        class="pagination"
      />
      
      <el-empty v-if="!loading && cases.length === 0" description="暂无案例" />
    </div>
  </customer-layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Picture, View, Star } from '@element-plus/icons-vue'
import CustomerLayout from '@/components/CustomerLayout.vue'
import { customerApi } from '@/api/customer'

const router = useRouter()

// 状态
const loading = ref(false)
const cases = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const filterCategory = ref(null)
const filterSort = ref('hot')
const searchKeyword = ref('')
const categories = ref([])

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
 * 加载服务分类
 */
async function loadCategories() {
  try {
    const res = await customerApi.getServiceCategoryList({ page: 1, pageSize: 100 })
    if (res.code === 200) {
      categories.value = res.data.list || []
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

/**
 * 加载案例列表
 */
async function loadCases() {
  loading.value = true
  
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      sortBy: filterSort.value
    }
    
    if (filterCategory.value) {
      params.categoryId = filterCategory.value
    }
    
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    
    const res = await customerApi.getCaseList(params)
    
    if (res.code === 200) {
      cases.value = res.data.list || []
      total.value = res.data.total || 0
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
 * 处理筛选条件变化
 */
function handleFilterChange() {
  currentPage.value = 1
  loadCases()
}

/**
 * 查看案例详情
 */
function viewCaseDetail(caseId) {
  router.push(`/customer/case/${caseId}`)
}

onMounted(() => {
  loadCategories()
  loadCases()
})
</script>

<style scoped>
.case-list-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.filter-card {
  border-radius: 8px;
  margin-bottom: 24px;
}

.filter-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.case-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

.case-card {
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  overflow: hidden;
}

.case-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.case-cover {
  width: 100%;
  height: 200px;
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

.case-content {
  padding: 16px;
}

.case-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.case-description {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.case-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.technician-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
}

.stats {
  display: flex;
  gap: 12px;
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

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .case-list-container {
    padding: 16px;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-bar > * {
    width: 100% !important;
    margin-left: 0 !important;
  }
  
  .case-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}
</style>
