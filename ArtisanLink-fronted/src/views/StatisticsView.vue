<script setup>
import { reactive } from 'vue'
import AdminLayout from '../components/AdminLayout.vue'
import { adminApi } from '../api'

const today = new Date().toISOString().slice(0, 10)
const state = reactive({
  loading: false,
  data: {},
  query: {
    merchantId: 1,
    startTime: `${today} 00:00:00`,
    endTime: `${today} 23:59:59`,
    type: 1
  }
})

async function fetchStats() {
  state.loading = true
  try {
    const res = await adminApi.getStatistics(state.query)
    state.data = res.data || {}
  } finally {
    state.loading = false
  }
}

fetchStats()
</script>

<template>
  <AdminLayout>
    <div class="page-header">
      <h2>经营统计</h2>
    </div>
    <div class="toolbar">
      <input v-model="state.query.startTime" placeholder="开始时间 2026-04-15 00:00:00" />
      <input v-model="state.query.endTime" placeholder="结束时间 2026-04-15 23:59:59" />
      <select v-model.number="state.query.type">
        <option :value="1">按天</option>
        <option :value="2">按周</option>
        <option :value="3">按月</option>
      </select>
      <button class="btn btn-primary" @click="fetchStats">查询</button>
    </div>

    <div class="card-grid">
      <article class="metric-card">
        <span>订单总数</span>
        <strong>{{ state.loading ? '--' : state.data.orderCount || 0 }}</strong>
      </article>
      <article class="metric-card">
        <span>营业额(元)</span>
        <strong>{{ state.loading ? '--' : state.data.revenue || 0 }}</strong>
      </article>
      <article class="metric-card">
        <span>客单价(元)</span>
        <strong>{{ state.loading ? '--' : state.data.avgOrderAmount || 0 }}</strong>
      </article>
      <article class="metric-card">
        <span>复购率(%)</span>
        <strong>{{ state.loading ? '--' : state.data.repurchaseRate || 0 }}</strong>
      </article>
    </div>
  </AdminLayout>
</template>
