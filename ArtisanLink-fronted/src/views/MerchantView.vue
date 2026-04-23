<script setup>
import { onMounted, reactive, ref } from 'vue'
import AdminLayout from '../components/AdminLayout.vue'
import { adminApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const state = reactive({
  loading: false,
  list: [],
  total: 0,
  query: { name: '', status: '', pageNum: 1, pageSize: 10 }
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  name: '',
  address: '',
  phone: '',
  businessHours: '',
  status: 1,
  remark: ''
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

function openAddDialog() {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', address: '', phone: '', businessHours: '', status: 1, remark: '' })
  dialogVisible.value = true
}

function openEditDialog(row) {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.name?.trim()) {
    ElMessage.warning('请输入商户名称')
    return
  }
  try {
    const payload = { ...form }
    const res = isEdit.value ? await adminApi.updateMerchant(payload) : await adminApi.saveMerchant(payload)
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

async function deleteMerchant(id) {
  try {
    await ElMessageBox.confirm('确定删除该商户吗？', '提示', { type: 'warning' })
    const res = await adminApi.deleteMerchant(id)
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
      <h2>商户管理</h2>
    </div>
    <div class="toolbar">
      <el-input v-model="state.query.name" placeholder="商户名称" clearable style="width: 200px" />
      <el-select v-model="state.query.status" placeholder="状态" clearable style="width: 140px">
        <el-option label="全部状态" value="" />
        <el-option label="营业中" value="1" />
        <el-option label="停业" value="0" />
      </el-select>
      <el-button type="primary" @click="fetchList">查询</el-button>
      <el-button type="success" @click="openAddDialog">新增商户</el-button>
    </div>

    <el-table :data="state.list" v-loading="state.loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" min-width="160" />
      <el-table-column prop="address" label="地址" min-width="220" />
      <el-table-column prop="phone" label="电话" width="140" />
      <el-table-column prop="businessHours" label="营业时间" width="140" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="Number(row.status) === 1 ? 'success' : 'info'">
            {{ Number(row.status) === 1 ? '营业中' : '停业' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteMerchant(row.id)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商户' : '新增商户'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="营业时间"><el-input v-model="form.businessHours" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">营业中</el-radio>
            <el-radio :label="0">停业</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </AdminLayout>
</template>
