<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import AdminLayout from '../components/AdminLayout.vue'
import { adminApi } from '../api'
import * as echarts from 'echarts'

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

// 图表实例
const revenueChartRef = ref(null)
const orderChartRef = ref(null)
let revenueChart = null
let orderChart = null

// 加载统计数据
async function fetchStats() {
  state.loading = true
  try {
    const res = await adminApi.getStatistics(state.query)
    state.data = res.data || {}
    
    // 更新图表
    await nextTick()
    initCharts()
  } catch (error) {
    console.error('加载统计数据失败:', error)
  } finally {
    state.loading = false
  }
}

// 初始化图表
function initCharts() {
  if (!revenueChartRef.value || !orderChartRef.value) return
  
  // 营收趋势图
  if (revenueChart) revenueChart.dispose()
  revenueChart = echarts.init(revenueChartRef.value)
  
  const revenueOption = {
    title: { text: '营收趋势', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: state.data.revenueTrend?.map(item => item.date) || []
    },
    yAxis: { type: 'value', name: '金额(元)' },
    series: [{
      name: '营收',
      type: 'line',
      smooth: true,
      data: state.data.revenueTrend?.map(item => item.amount) || [],
      itemStyle: { color: '#d4a574' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(212, 165, 116, 0.3)' },
          { offset: 1, color: 'rgba(212, 165, 116, 0.05)' }
        ])
      }
    }]
  }
  revenueChart.setOption(revenueOption)
  
  // 订单来源分布图
  if (orderChart) orderChart.dispose()
  orderChart = echarts.init(orderChartRef.value)
  
  const orderOption = {
    title: { text: '订单来源分布', left: 'center' },
    tooltip: { trigger: 'item' },
    legend: { bottom: '5%', left: 'center' },
    series: [{
      name: '订单来源',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: { show: false, position: 'center' },
      emphasis: {
        label: {
          show: true,
          fontSize: 20,
          fontWeight: 'bold'
        }
      },
      data: state.data.orderSource || [
        { value: 335, name: '首页推荐' },
        { value: 310, name: '附近技师' },
        { value: 234, name: '搜索' },
        { value: 135, name: '案例种草' },
        { value: 1548, name: '其他' }
      ]
    }]
  }
  orderChart.setOption(orderOption)
}

onMounted(() => {
  fetchStats()
  
  // 窗口大小变化时重新渲染图表
  window.addEventListener('resize', () => {
    revenueChart?.resize()
    orderChart?.resize()
  })
})
</script>

<template>
  <AdminLayout>
    <div class="page-header">
      <h2>经营统计</h2>
    </div>
    <div class="toolbar">
      <el-date-picker
        v-model="state.query.startTime"
        type="datetime"
        placeholder="开始时间"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="YYYY-MM-DD HH:mm:ss"
        style="width: 220px"
      />
      <el-date-picker
        v-model="state.query.endTime"
        type="datetime"
        placeholder="结束时间"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="YYYY-MM-DD HH:mm:ss"
        style="width: 220px"
      />
      <el-select v-model.number="state.query.type" placeholder="统计维度" style="width: 150px">
        <el-option :value="1" label="按天" />
        <el-option :value="2" label="按周" />
        <el-option :value="3" label="按月" />
      </el-select>
      <el-button type="primary" @click="fetchStats">查询</el-button>
    </div>

    <!-- 核心指标卡片 -->
    <el-row :gutter="20" class="metric-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>订单总数</span>
          </template>
          <div class="metric-value">{{ state.loading ? '--' : state.data.orderCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>营业额(元)</span>
          </template>
          <div class="metric-value highlight">{{ state.loading ? '--' : state.data.revenue || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>客单价(元)</span>
          </template>
          <div class="metric-value">{{ state.loading ? '--' : state.data.avgOrderAmount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>复购率(%)</span>
          </template>
          <div class="metric-value">{{ state.loading ? '--' : state.data.repurchaseRate || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <div ref="revenueChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div ref="orderChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </AdminLayout>
</template>
