<template>
  <customer-layout>
    <!-- 顶部搜索 -->
    <search-bar @search="handleSearch" />
    
    <!-- 服务分类列表 -->
    <div class="category-container" v-loading="loading">
      <div class="category-grid">
        <div
          v-for="category in categories"
          :key="category.id"
          class="category-item"
          @click="handleCategoryClick(category)"
        >
          <div class="category-icon">
            <el-icon :size="32">
              <component :is="getCategoryIcon(category.icon)" />
            </el-icon>
          </div>
          <div class="category-name">{{ category.name }}</div>
          <div class="category-count">{{ category.count }}项服务</div>
        </div>
      </div>
      
      <el-empty v-if="!loading && categories.length === 0" description="暂无服务分类" />
    </div>
  </customer-layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import CustomerLayout from '@/components/CustomerLayout.vue'
import SearchBar from '@/components/customer/SearchBar.vue'
import { customerApi } from '@/api/customer'

const router = useRouter()

// 状态
const categories = ref([])
const loading = ref(false)

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
 * 加载服务分类
 */
async function loadCategories() {
  loading.value = true
  try {
    const res = await customerApi.getServiceCategories()
    if (res.code === 200) {
      categories.value = res.data || []
    } else {
      ElMessage.error(res.msg || '加载分类失败')
    }
  } catch (error) {
    console.error('加载服务分类失败:', error)
    ElMessage.error('加载分类失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

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
 * 处理分类点击
 */
function handleCategoryClick(category) {
  router.push({
    path: '/customer/services',
    query: { categoryId: category.id, categoryName: category.name }
  })
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-container {
  padding: 16px;
  min-height: calc(100vh - 200px);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
}

.category-item {
  background: #fff;
  border-radius: 12px;
  padding: 24px 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.category-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.category-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 12px;
  background: linear-gradient(135deg, #f5e6d3 0%, #e8d5c4 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #b8860b;
}

.category-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.category-count {
  font-size: 12px;
  color: #999;
}
</style>
