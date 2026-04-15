<script setup>
import { onMounted, reactive } from 'vue'
import AdminLayout from '../components/AdminLayout.vue'
import { adminApi } from '../api'

const state = reactive({
  loading: false,
  list: [],
  total: 0,
  query: { name: '', status: '', pageNum: 1, pageSize: 10 }
})

async function fetchList() {
  state.loading = true
  try {
    const res = await adminApi.getMerchants({
      ...state.query,
      status: state.query.status === '' ? undefined : Number(state.query.status)
    })
    const data = res.data || {}
    state.list = data.records || data.list || []
    state.total = data.total || state.list.length
  } finally {
    state.loading = false
  }
}

onMounted(fetchList)
</script>

<template>
  <AdminLayout>
    <div class="page-header">
      <h2>商户管理</h2>
    </div>
    <div class="toolbar">
      <input v-model="state.query.name" placeholder="商户名称" />
      <select v-model="state.query.status">
        <option value="">全部状态</option>
        <option value="1">营业中</option>
        <option value="0">停业</option>
      </select>
      <button class="btn btn-primary" @click="fetchList">查询</button>
    </div>

    <table class="table">
      <thead>
        <tr>
          <th>ID</th>
          <th>名称</th>
          <th>地址</th>
          <th>电话</th>
          <th>营业时间</th>
          <th>状态</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="state.loading"><td colspan="6">加载中...</td></tr>
        <tr v-else-if="state.list.length === 0"><td colspan="6">暂无数据</td></tr>
        <tr v-for="item in state.list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.name || item.shopName }}</td>
          <td>{{ item.address }}</td>
          <td>{{ item.phone }}</td>
          <td>{{ item.businessHours || '-' }}</td>
          <td>{{ Number(item.status) === 1 ? '营业中' : '停业' }}</td>
        </tr>
      </tbody>
    </table>
    <p class="total">共 {{ state.total }} 条</p>
  </AdminLayout>
</template>
