<script setup>
import { onMounted, reactive } from 'vue'
import AdminLayout from '../components/AdminLayout.vue'
import { adminApi } from '../api'

const state = reactive({
  loading: false,
  list: [],
  total: 0,
  query: { merchantId: 1, name: '', position: '', status: '', pageNum: 1, pageSize: 10 }
})

async function fetchList() {
  state.loading = true
  try {
    const res = await adminApi.getStaff({
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
      <h2>员工管理</h2>
    </div>
    <div class="toolbar">
      <input v-model="state.query.name" placeholder="员工姓名" />
      <input v-model="state.query.position" placeholder="岗位" />
      <select v-model="state.query.status">
        <option value="">全部状态</option>
        <option value="1">在职</option>
        <option value="0">离职/停用</option>
      </select>
      <button class="btn btn-primary" @click="fetchList">查询</button>
    </div>
    <table class="table">
      <thead>
        <tr>
          <th>ID</th>
          <th>姓名</th>
          <th>手机号</th>
          <th>岗位</th>
          <th>技能标签</th>
          <th>状态</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="state.loading"><td colspan="6">加载中...</td></tr>
        <tr v-else-if="state.list.length === 0"><td colspan="6">暂无数据</td></tr>
        <tr v-for="item in state.list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.name }}</td>
          <td>{{ item.phone }}</td>
          <td>{{ item.position }}</td>
          <td>{{ Array.isArray(item.skillTags) ? item.skillTags.join(' / ') : item.skill || '-' }}</td>
          <td>{{ Number(item.status) === 1 ? '在职' : '停用' }}</td>
        </tr>
      </tbody>
    </table>
    <p class="total">共 {{ state.total }} 条</p>
  </AdminLayout>
</template>
