<script setup>
import { onMounted, reactive } from 'vue'
import AdminLayout from '../components/AdminLayout.vue'
import { adminApi } from '../api'

const today = new Date().toISOString().slice(0, 10)
const state = reactive({
  stats: {
    orderCount: 0,
    revenue: 0,
    appointmentCount: 0,
    customerCount: 0
  },
  loading: false
})

async function loadStats() {
  state.loading = true
  try {
    const res = await adminApi.getStatistics({
      merchantId: 1,
      startTime: `${today} 00:00:00`,
      endTime: `${today} 23:59:59`,
      type: 1
    })
    const data = res.data || {}
    state.stats.orderCount = data.orderCount || data.orders || 0
    state.stats.revenue = data.revenue || 0
    state.stats.appointmentCount = data.appointmentCount || 0
    state.stats.customerCount = data.customerCount || 0
  } catch (error) {
    // 统计接口字段可能按后端实现不同，这里采用容错展示。
  } finally {
    state.loading = false
  }
}

onMounted(loadStats)
</script>

<template>
  <AdminLayout>
    <div class="page-header">
      <h2>数据看板</h2>
      <p>实时掌握门店订单、预约、营收核心指标</p>
    </div>

    <div class="card-grid">
      <article class="metric-card">
        <span>今日订单</span>
        <strong>{{ state.loading ? '--' : state.stats.orderCount }}</strong>
      </article>
      <article class="metric-card">
        <span>今日营收(元)</span>
        <strong>{{ state.loading ? '--' : state.stats.revenue }}</strong>
      </article>
      <article class="metric-card">
        <span>今日预约</span>
        <strong>{{ state.loading ? '--' : state.stats.appointmentCount }}</strong>
      </article>
      <article class="metric-card">
        <span>今日到店客户</span>
        <strong>{{ state.loading ? '--' : state.stats.customerCount }}</strong>
      </article>
    </div>
  </AdminLayout>
</template>
