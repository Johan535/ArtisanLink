<script setup>
import { onMounted, reactive } from 'vue'
import AdminLayout from '../components/AdminLayout.vue'
import { adminApi } from '../api'

const state = reactive({
  loading: false,
  list: [],
  total: 0,
  categories: [],
  query: { merchantId: 1, categoryId: '', name: '', status: '', pageNum: 1, pageSize: 10 }
})

async function loadCategories() {
  const res = await adminApi.getServiceCategories({ merchantId: state.query.merchantId })
  state.categories = res.data || []
}

async function fetchList() {
  state.loading = true
  try {
    const res = await adminApi.getServices({
      ...state.query,
      categoryId: state.query.categoryId || undefined,
      status: state.query.status === '' ? undefined : Number(state.query.status)
    })
    const data = res.data || {}
    state.list = data.records || data.list || []
    state.total = data.total || state.list.length
  } finally {
    state.loading = false
  }
}

onMounted(async () => {
  await loadCategories()
  await fetchList()
})
</script>

<template>
  <AdminLayout>
    <div class="page-header">
      <h2>服务管理</h2>
    </div>
    <div class="toolbar">
      <input v-model="state.query.name" placeholder="服务名称" />
      <select v-model="state.query.categoryId">
        <option value="">全部分类</option>
        <option v-for="c in state.categories" :key="c.id" :value="c.id">{{ c.name }}</option>
      </select>
      <select v-model="state.query.status">
        <option value="">全部状态</option>
        <option value="1">上架</option>
        <option value="0">下架</option>
      </select>
      <button class="btn btn-primary" @click="fetchList">查询</button>
    </div>
    <table class="table">
      <thead>
        <tr>
          <th>ID</th>
          <th>服务名称</th>
          <th>分类</th>
          <th>价格(元)</th>
          <th>时长(分钟)</th>
          <th>状态</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="state.loading"><td colspan="6">加载中...</td></tr>
        <tr v-else-if="state.list.length === 0"><td colspan="6">暂无数据</td></tr>
        <tr v-for="item in state.list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.serviceName || item.name }}</td>
          <td>{{ item.categoryName || '-' }}</td>
          <td>{{ item.price }}</td>
          <td>{{ item.duration }}</td>
          <td>{{ Number(item.status) === 1 ? '上架' : '下架' }}</td>
        </tr>
      </tbody>
    </table>
    <p class="total">共 {{ state.total }} 条</p>
  </AdminLayout>
</template>
