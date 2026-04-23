<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import AdminLayout from '../components/AdminLayout.vue'
import { adminApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import wsClient from '@/utils/websocket'

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

// 未处理订单数
const pendingCount = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const orderForm = reactive({
  id: null,
  merchantId: 1,
  orderNo: '',
  customerName: '',
  customerPhone: '',
  serviceName: '',
  staffName: '',
  amount: 0,
  orderStatus: 0,
  remark: ''
})

// 加载订单列表
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
    
    // 统计待接单数量
    pendingCount.value = state.list.filter(o => o.orderStatus === 0).length
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    state.loading = false
  }
}

function openAddDialog() {
  isEdit.value = false
  Object.assign(orderForm, {
    id: null,
    merchantId: 1,
    orderNo: '',
    customerName: '',
    customerPhone: '',
    serviceName: '',
    staffName: '',
    amount: 0,
    orderStatus: 0,
    remark: ''
  })
  dialogVisible.value = true
}

function openEditDialog(row) {
  isEdit.value = true
  Object.assign(orderForm, row)
  dialogVisible.value = true
}

async function submitOrderForm() {
  if (!orderForm.orderNo?.trim()) return ElMessage.warning('请输入订单号')
  try {
    const payload = { ...orderForm }
    const res = isEdit.value ? await adminApi.updateOrder(payload) : await adminApi.saveOrder(payload)
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
      dialogVisible.value = false
      await fetchList()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

async function deleteOrder(id) {
  try {
    await ElMessageBox.confirm('确定删除该订单吗？', '提示', { type: 'warning' })
    const res = await adminApi.deleteOrder(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await fetchList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

// 接单
async function acceptOrder(orderId) {
  try {
    await ElMessageBox.confirm('确定要接此订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    const res = await adminApi.acceptOrder(orderId)
    if (res.code === 200) {
      ElMessage.success('接单成功')
      await fetchList()
    } else {
      ElMessage.error(res.msg || '接单失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('接单失败:', error)
      ElMessage.error('接单失败')
    }
  }
}

// 拒绝订单
async function rejectOrder(orderId) {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入拒绝原因'
    })
    
    const res = await adminApi.rejectOrder(orderId, reason)
    if (res.code === 200) {
      ElMessage.success('已拒绝订单')
      await fetchList()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('拒绝订单失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 完成订单
async function completeOrder(orderId) {
  try {
    await ElMessageBox.confirm('确定标记此订单为已完成吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    const res = await adminApi.completeOrder(orderId)
    if (res.code === 200) {
      ElMessage.success('订单已完成')
      await fetchList()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('完成订单失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// WebSocket订单通知处理
function handleNewOrder(data) {
  ElMessage.warning(`收到新订单：${data.orderNo}`)
  pendingCount.value++
  // 如果当前在待接单标签页，刷新列表
  if (state.query.orderStatus === '' || state.query.orderStatus === '0') {
    fetchList()
  }
}

onMounted(async () => {
  await fetchList()
  
  // 连接WebSocket接收订单通知
  wsClient.connect()
  wsClient.on('new_order', handleNewOrder)
  wsClient.on('order_status_change', () => {
    fetchList()
  })
})

onUnmounted(() => {
  wsClient.off('new_order', handleNewOrder)
  wsClient.off('order_status_change')
})
</script>

<template>
  <AdminLayout>
    <div class="page-header">
      <h2>订单管理</h2>
    </div>
    <div class="toolbar">
      <el-input v-model="state.query.orderNo" placeholder="订单号" clearable style="width: 200px" />
      <el-input v-model="state.query.customerPhone" placeholder="客户手机号" clearable style="width: 200px" />
      <el-select v-model="state.query.orderStatus" placeholder="状态" clearable style="width: 150px">
        <el-option label="全部状态" value="" />
        <el-option label="待接单" :value="0" />
        <el-option label="已接单" :value="1" />
        <el-option label="已完成" :value="2" />
        <el-option label="已取消" :value="3" />
      </el-select>
      <el-button type="primary" @click="fetchList">查询</el-button>
      <el-button type="success" @click="openAddDialog">新增订单</el-button>
      
      <!-- 待接单提醒 -->
      <el-badge v-if="pendingCount > 0" :value="pendingCount" class="pending-badge">
        <el-tag type="warning">待处理订单</el-tag>
      </el-badge>
    </div>
    
    <el-table :data="state.list" v-loading="state.loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column label="客户信息" width="150">
        <template #default="{ row }">
          <div>{{ row.customerName || '-' }}</div>
          <div style="font-size: 12px; color: #999;">{{ row.customerPhone }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="serviceName" label="服务项目" width="150" />
      <el-table-column prop="staffName" label="技师" width="120" />
      <el-table-column prop="appointmentTime" label="预约时间" width="160" />
      <el-table-column prop="amount" label="金额" width="100">
        <template #default="{ row }">
          <span style="color: #d4a574; font-weight: 600;">¥{{ row.amount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.orderStatus === 0 ? 'warning' : row.orderStatus === 1 ? 'primary' : row.orderStatus === 2 ? 'success' : 'info'">
            {{ statusMap[row.orderStatus] || '未知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <!-- 待接单状态 -->
          <template v-if="row.orderStatus === 0">
            <el-button size="small" type="success" @click="acceptOrder(row.id)">接单</el-button>
            <el-button size="small" type="danger" @click="rejectOrder(row.id)">拒绝</el-button>
          </template>
          
          <!-- 已接单状态 -->
          <template v-else-if="row.orderStatus === 1">
            <el-button size="small" type="primary" @click="completeOrder(row.id)">完成</el-button>
          </template>
          
          <!-- 其他状态 -->
          <el-button v-else size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteOrder(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination">
      <el-pagination
        v-model:current-page="state.query.pageNum"
        v-model:page-size="state.query.pageSize"
        :total="state.total"
        layout="total, prev, pager, next, jumper"
        @current-change="fetchList"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑订单' : '新增订单'" width="640px">
      <el-form :model="orderForm" label-width="100px">
        <el-form-item label="订单号"><el-input v-model="orderForm.orderNo" /></el-form-item>
        <el-form-item label="客户姓名"><el-input v-model="orderForm.customerName" /></el-form-item>
        <el-form-item label="客户手机号"><el-input v-model="orderForm.customerPhone" /></el-form-item>
        <el-form-item label="服务名称"><el-input v-model="orderForm.serviceName" /></el-form-item>
        <el-form-item label="技师姓名"><el-input v-model="orderForm.staffName" /></el-form-item>
        <el-form-item label="金额"><el-input-number v-model="orderForm.amount" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="orderForm.orderStatus" style="width: 100%">
            <el-option label="待接单" :value="0" />
            <el-option label="已接单" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="orderForm.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOrderForm">保存</el-button>
      </template>
    </el-dialog>
  </AdminLayout>
</template>
