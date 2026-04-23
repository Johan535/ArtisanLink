<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { customerApi } from '@/api/customer'
import { ElMessage, ElMessageBox } from 'element-plus'
import wsClient from '@/utils/websocket'

const router = useRouter()

// 订单状态标签页
const activeTab = ref('all')

// 订单列表
const orders = ref([])

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  hasMore: true
})

// 加载状态
const loading = reactive({
  list: false,
  more: false
})

// 订单状态映射
const statusMap = {
  all: { label: '全部', value: null },
  pending: { label: '待接单', value: 0 },
  accepted: { label: '已接单', value: 1 },
  completed: { label: '已完成', value: 2 },
  cancelled: { label: '已取消', value: 3 }
}

// 加载订单列表
async function loadOrders(isRefresh = false) {
  if (isRefresh) {
    pagination.pageNum = 1
    pagination.hasMore = true
    orders.value = []
  }
  
  if (!pagination.hasMore) return
  
  const isLoading = isRefresh ? loading.list : loading.more
  if (isLoading.value) return
  
  isLoading.value = true
  
  try {
    const status = statusMap[activeTab.value]?.value
    const res = await customerApi.getMyOrders({
      status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    
    if (res.code === 200 && res.data) {
      const newList = res.data.list || []
      
      if (isRefresh) {
        orders.value = newList
      } else {
        orders.value.push(...newList)
      }
      
      pagination.total = res.data.total || 0
      pagination.hasMore = newList.length >= pagination.pageSize
      pagination.pageNum++
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    isLoading.value = false
  }
}

// 切换标签页
function handleTabChange(tabName) {
  activeTab.value = tabName
  loadOrders(true)
}

// 下拉刷新
async function handleRefresh() {
  await loadOrders(true)
}

// 加载更多
function loadMore() {
  if (!loading.more && pagination.hasMore) {
    loadOrders(false)
  }
}

// 查看订单详情
function viewOrderDetail(orderId) {
  router.push(`/customer/order/${orderId}`)
}

// 取消订单
async function cancelOrder(orderId) {
  try {
    await ElMessageBox.confirm(
      '确定要取消该订单吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await customerApi.cancelOrder(orderId)
    
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      // 重新加载订单列表
      await loadOrders(true)
    } else {
      ElMessage.error(res.msg || '取消失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消失败')
    }
  }
}

// 获取订单状态文本
function getStatusText(status) {
  const statusTexts = {
    0: '待接单',
    1: '已接单',
    2: '已完成',
    3: '已取消'
  }
  return statusTexts[status] || '未知'
}

// 获取订单状态样式类
function getStatusClass(status) {
  const statusClasses = {
    0: 'status-pending',
    1: 'status-accepted',
    2: 'status-completed',
    3: 'status-cancelled'
  }
  return statusClasses[status] || ''
}

// WebSocket订单状态更新处理
function handleOrderStatusUpdate(data) {
  // 查找并更新对应订单的状态
  const orderIndex = orders.value.findIndex(o => o.id === data.orderId)
  if (orderIndex !== -1) {
    orders.value[orderIndex].status = data.status
    orders.value[orderIndex].statusText = getStatusText(data.status)
    
    ElMessage.success(`订单#${data.orderId}状态已更新为：${getStatusText(data.status)}`)
  }
}

onMounted(async () => {
  await loadOrders(true)
  
  // 连接WebSocket接收订单通知
  wsClient.connect()
  wsClient.on('order_status_change', handleOrderStatusUpdate)
})

onUnmounted(() => {
  wsClient.off('order_status_change', handleOrderStatusUpdate)
})
</script>

<template>
  <div class="order-list-container">
    <!-- 顶部导航 -->
    <header class="order-header">
      <el-button 
        :icon="'ArrowLeft'" 
        circle 
        @click="router.back()"
        class="back-btn"
      />
      <h2 class="header-title">我的订单</h2>
    </header>

    <!-- 状态标签页 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="status-tabs">
      <el-tab-pane 
        v-for="(status, key) in statusMap" 
        :key="key"
        :label="status.label"
        :name="key"
      />
    </el-tabs>

    <!-- 订单列表 -->
    <div class="order-content">
      <!-- 加载状态 -->
      <el-skeleton v-if="loading.list && orders.length === 0" :rows="5" animated />
      
      <!-- 订单卡片列表 -->
      <div v-else class="order-list">
        <div 
          v-for="order in orders" 
          :key="order.id"
          class="order-card"
          @click="viewOrderDetail(order.id)"
        >
          <!-- 订单头部 -->
          <div class="order-header-info">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span :class="['order-status', getStatusClass(order.status)]">
              {{ getStatusText(order.status) }}
            </span>
          </div>
          
          <!-- 技师信息 -->
          <div class="order-tech">
            <img 
              :src="order.technicianAvatar || '/images/default-avatar.png'" 
              :alt="order.technicianName"
              class="tech-avatar"
            />
            <div class="tech-info">
              <h4>{{ order.technicianName }}</h4>
              <p>{{ order.serviceName }}</p>
            </div>
          </div>
          
          <!-- 预约时间 -->
          <div class="order-time">
            <el-icon><Calendar /></el-icon>
            <span>{{ order.appointmentTime }}</span>
          </div>
          
          <!-- 订单金额 -->
          <div class="order-price">
            <span class="label">实付：</span>
            <span class="price">¥{{ order.amount }}</span>
          </div>
          
          <!-- 操作按钮 -->
          <div class="order-actions" @click.stop>
            <el-button 
              v-if="order.status === 0"
              size="small"
              @click="cancelOrder(order.id)"
            >
              取消订单
            </el-button>
            <el-button 
              size="small"
              type="primary"
              @click="viewOrderDetail(order.id)"
            >
              查看详情
            </el-button>
          </div>
        </div>
        
        <!-- 空状态 -->
        <el-empty 
          v-if="!loading.list && orders.length === 0" 
          description="暂无订单"
        />
        
        <!-- 加载更多 -->
        <div v-if="pagination.hasMore" class="load-more" @click="loadMore">
          <span v-if="!loading.more">加载更多</span>
          <el-loading v-else />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-list-container {
  min-height: 100vh;
  background: #f8f9fa;
}

/* 顶部导航 */
.order-header {
  background: linear-gradient(135deg, #d4a574 0%, #c9956b 100%);
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: #fff;
}

.header-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

/* 状态标签页 */
.status-tabs {
  background: #fff;
  margin-bottom: 12px;
}

.status-tabs :deep(.el-tabs__header) {
  margin: 0;
}

.status-tabs :deep(.el-tabs__item) {
  font-size: 14px;
}

/* 订单内容区 */
.order-content {
  padding: 0 16px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 订单卡片 */
.order-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.order-header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.order-no {
  font-size: 14px;
  color: #666;
}

.order-status {
  font-size: 14px;
  font-weight: 600;
}

.status-pending {
  color: #e6a23c;
}

.status-accepted {
  color: #409eff;
}

.status-completed {
  color: #67c23a;
}

.status-cancelled {
  color: #909399;
}

/* 技师信息 */
.order-tech {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
}

.tech-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.tech-info h4 {
  font-size: 16px;
  color: #333;
  margin: 0 0 4px 0;
}

.tech-info p {
  font-size: 13px;
  color: #999;
  margin: 0;
}

/* 预约时间 */
.order-time {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

/* 订单金额 */
.order-price {
  text-align: right;
  margin-bottom: 12px;
}

.order-price .label {
  font-size: 14px;
  color: #666;
}

.order-price .price {
  font-size: 18px;
  font-weight: 600;
  color: #d4a574;
}

/* 操作按钮 */
.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 16px;
  color: #d4a574;
  cursor: pointer;
  font-size: 14px;
}

.load-more:hover {
  color: #c9956b;
}

/* 响应式适配 */
@media (max-width: 750px) {
  .order-content {
    padding: 0 12px;
  }
}

@media (min-width: 751px) {
  .order-list-container {
    max-width: 750px;
    margin: 0 auto;
  }
}
</style>
