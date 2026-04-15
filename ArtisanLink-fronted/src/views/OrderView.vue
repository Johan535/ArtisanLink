<script setup>
import { onMounted, reactive } from 'vue'
import AdminLayout from '../components/AdminLayout.vue'
import { adminApi } from '../api'

const statusMap = {
  0: '待接单',
  1: '已接单',
  2: '已完成',
  3: '已取消'
}

const state = reactive({
  loading: false,
  list: [],
  total: 0,
  query: {
    merchantId: 1,
    orderNo: '',
    customerPhone: '',
    orderStatus: '',
    pageNum: 1,
    pageSize: 10
  }
})

async function fetchList() {
  state.loading = true
  try {
    const res = await adminApi.getOrders({
      ...state.query,
      orderStatus: state.query.orderStatus === '' ? undefined : Number(state.query.orderStatus)
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
      <h2>订单管理</h2>
    </div>
    <div class="toolbar">
      <input v-model="state.query.orderNo" placeholder="订单号" />
      <input v-model="state.query.customerPhone" placeholder="客户手机号" />
      <select v-model="state.query.orderStatus">
        <option value="">全部状态</option>
        <option value="0">待接单</option>
        <option value="1">已接单</option>
        <option value="2">已完成</option>
        <option value="3">已取消</option>
      </select>
      <button class="btn btn-primary" @click="fetchList">查询</button>
    </div>
    <table class="table">
      <thead>
        <tr>
          <th>ID</th>
          <th>订单号</th>
          <th>客户</th>
          <th>服务</th>
          <th>技师</th>
          <th>状态</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="state.loading"><td colspan="6">加载中...</td></tr>
        <tr v-else-if="state.list.length === 0"><td colspan="6">暂无数据</td></tr>
        <tr v-for="item in state.list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.orderNo }}</td>
          <td>{{ item.customerName || item.customerPhone || '-' }}</td>
          <td>{{ item.serviceName || '-' }}</td>
          <td>{{ item.staffName || item.techName || '-' }}</td>
          <td>{{ statusMap[item.orderStatus] || '未知状态' }}</td>
        </tr>
      </tbody>
    </table>
    <p class="total">共 {{ state.total }} 条</p>
  </AdminLayout>
</template>
