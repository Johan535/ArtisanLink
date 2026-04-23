<template>
  <div class="search-bar">
    <el-input
      v-model="searchKeyword"
      :placeholder="placeholder"
      :prefix-icon="Search"
      clearable
      @keyup.enter="handleSearch"
      @clear="handleClear"
    >
      <template #append>
        <el-button :icon="Search" @click="handleSearch">搜索</el-button>
      </template>
    </el-input>
    
    <!-- 热门搜索 -->
    <div class="hot-searches" v-if="showHotSearches && hotSearches.length > 0">
      <span class="label">热门搜索：</span>
      <el-tag
        v-for="(item, index) in hotSearches"
        :key="index"
        size="small"
        effect="plain"
        @click="handleHotSearch(item)"
        class="hot-tag"
      >
        {{ item }}
      </el-tag>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  placeholder: {
    type: String,
    default: '搜索技师、服务名称'
  },
  showHotSearches: {
    type: Boolean,
    default: true
  },
  hotSearches: {
    type: Array,
    default: () => ['美甲', '美发', '美容', '按摩', 'SPA']
  }
})

const emit = defineEmits(['search', 'clear'])

const router = useRouter()
const searchKeyword = ref('')

/**
 * 处理搜索
 */
function handleSearch() {
  const keyword = searchKeyword.value.trim()
  if (!keyword) return
  
  emit('search', keyword)
  
  // 跳转到搜索结果页
  router.push({
    path: '/customer/search',
    query: { keyword }
  })
}

/**
 * 处理清空
 */
function handleClear() {
  emit('clear')
}

/**
 * 处理热门搜索点击
 */
function handleHotSearch(keyword) {
  searchKeyword.value = keyword
  handleSearch()
}
</script>

<style scoped>
.search-bar {
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.hot-searches {
  margin-top: 12px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.label {
  font-size: 12px;
  color: #999;
  margin-right: 4px;
}

.hot-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.hot-tag:hover {
  background-color: #d4a574;
  color: #fff;
  border-color: #d4a574;
}
</style>
