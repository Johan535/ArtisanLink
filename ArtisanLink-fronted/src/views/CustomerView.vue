<script setup>
import { onMounted, reactive, ref } from 'vue'
import AdminLayout from '../components/AdminLayout.vue'
import { adminApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

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
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  merchantId: 1,
  name: '',
  phone: '',
  gender: 0,
  memberLevel: 0,
  points: 0,
  balance: 0,
  remark: ''
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

function openAddDialog() {
  isEdit.value = false
  Object.assign(form, { id: null, merchantId: 1, name: '', phone: '', gender: 0, memberLevel: 0, points: 0, balance: 0, remark: '' })
  dialogVisible.value = true
}

function openEditDialog(row) {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.phone?.trim()) return ElMessage.warning('请输入手机号')
  try {
    const payload = { ...form }
    const res = isEdit.value ? await adminApi.updateCustomer(payload) : await adminApi.saveCustomer(payload)
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

async function deleteCustomer(id) {
  try {
    await ElMessageBox.confirm('确定删除该客户吗？', '提示', { type: 'warning' })
    const res = await adminApi.deleteCustomer(id)
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

onMounted(fetchList)
</script>

<template>
  <AdminLayout>
    <div class="page-header">
      <h2>客户管理</h2>
    </div>
    <div class="toolbar">
      <el-input v-model="state.query.name" placeholder="客户姓名" clearable style="width: 180px" />
      <el-input v-model="state.query.phone" placeholder="客户手机号" clearable style="width: 180px" />
      <el-select v-model="state.query.memberLevel" placeholder="等级" clearable style="width: 150px">
        <el-option label="全部等级" value="" />
        <el-option label="普通会员" :value="1" />
        <el-option label="白银会员" :value="2" />
        <el-option label="黄金会员" :value="3" />
        <el-option label="黑金会员" :value="4" />
      </el-select>
      <el-button type="primary" @click="fetchList">查询</el-button>
      <el-button type="success" @click="openAddDialog">新增客户</el-button>
    </div>
    <el-table :data="state.list" v-loading="state.loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column label="会员等级" width="120">
        <template #default="{ row }">{{ levelMap[row.memberLevel] || '普通会员' }}</template>
      </el-table-column>
      <el-table-column prop="points" label="积分" width="100" />
      <el-table-column prop="balance" label="余额(元)" width="110" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteCustomer(row.id)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑客户' : '新增客户'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :label="0">未知</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="会员等级">
          <el-select v-model="form.memberLevel" style="width: 100%">
            <el-option label="普通会员" :value="0" />
            <el-option label="白银会员" :value="1" />
            <el-option label="黄金会员" :value="2" />
            <el-option label="黑金会员" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="积分"><el-input-number v-model="form.points" :min="0" /></el-form-item>
        <el-form-item label="余额"><el-input-number v-model="form.balance" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </AdminLayout>
</template>
