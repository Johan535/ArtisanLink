<script setup>
import { onMounted, reactive } from 'vue'
import AdminLayout from '../components/AdminLayout.vue'
import { adminApi } from '../api'

const levelMap = {
  1: '普通会员',
  2: '白银会员',
  3: '黄金会员',
  4: '黑金会员'
}

const state = reactive({
  loading: false,
  list: [],
  total: 0,
  query: { merchantId: 1, name: '', phone: '', memberLevel: '', pageNum: 1, pageSize: 10 }
})

async function fetchList() {
  state.loading = true
  try {
    const res = await adminApi.getCustomers({
      ...state.query,
      memberLevel: state.query.memberLevel === '' ? undefined : Number(state.query.memberLevel)
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
      <h2>客户管理</h2>
    </div>
    <div class="toolbar">
      <input v-model="state.query.name" placeholder="客户姓名" />
      <input v-model="state.query.phone" placeholder="客户手机号" />
      <select v-model="state.query.memberLevel">
        <option value="">全部等级</option>
        <option value="1">普通会员</option>
        <option value="2">白银会员</option>
        <option value="3">黄金会员</option>
        <option value="4">黑金会员</option>
      </select>
      <button class="btn btn-primary" @click="fetchList">查询</button>
    </div>
    <table class="table">
      <thead>
        <tr>
          <th>ID</th>
          <th>姓名</th>
          <th>手机号</th>
          <th>会员等级</th>
          <th>累计消费(元)</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="state.loading"><td colspan="5">加载中...</td></tr>
        <tr v-else-if="state.list.length === 0"><td colspan="5">暂无数据</td></tr>
        <tr v-for="item in state.list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.name }}</td>
          <td>{{ item.phone }}</td>
          <td>{{ levelMap[item.memberLevel] || '普通会员' }}</td>
          <td>{{ item.totalAmount || 0 }}</td>
        </tr>
      </tbody>
    </table>
    <p class="total">共 {{ state.total }} 条</p>
  </AdminLayout>
</template>
