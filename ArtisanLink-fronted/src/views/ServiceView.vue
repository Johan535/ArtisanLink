<script setup>
import { onMounted, reactive, ref } from 'vue'
import AdminLayout from '../components/AdminLayout.vue'
import { adminApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const state = reactive({
  loading: false,
  list: [],
  total: 0,
  categories: [],
  query: { merchantId: 1, categoryId: '', name: '', status: '', pageNum: 1, pageSize: 10 }
})
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  merchantId: 1,
  categoryId: null,
  name: '',
  description: '',
  price: 0,
  duration: 60,
  status: 1
})

async function loadCategories() {
  const res = await adminApi.getServiceCategories({ merchantId: state.query.merchantId })
  state.categories = res.data || []
}

async function fetchList() {
  state.loading = true
  try {
    const res = await adminApi.getServices({
      ...state.query,
      categoryId: state.query.categoryId || undefined,
      status: state.query.status === '' ? undefined : Number(state.query.status)
    })
    const data = res.data || {}
    state.list = data.records || data.list || []
    state.total = data.total || state.list.length
  } finally {
    state.loading = false
  }
}

function getCategoryName(row) {
  if (row.categoryName) return row.categoryName
  const target = state.categories.find((c) => c.id === row.categoryId)
  return target?.name || '-'
}

function openAddDialog() {
  isEdit.value = false
  Object.assign(form, { id: null, merchantId: 1, categoryId: null, name: '', description: '', price: 0, duration: 60, status: 1 })
  dialogVisible.value = true
}

function openEditDialog(row) {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.name?.trim()) return ElMessage.warning('请输入服务名称')
  if (!form.categoryId) return ElMessage.warning('请选择分类')
  try {
    const payload = { ...form }
    const res = isEdit.value ? await adminApi.updateService(form.id, payload) : await adminApi.saveService(payload)
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

async function deleteService(id) {
  try {
    await ElMessageBox.confirm('确定删除该服务吗？', '提示', { type: 'warning' })
    const res = await adminApi.deleteService(id)
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

onMounted(async () => {
  await loadCategories()
  await fetchList()
})
</script>

<template>
  <AdminLayout>
    <div class="page-header">
      <h2>服务管理</h2>
    </div>
    <div class="toolbar">
      <el-input v-model="state.query.name" placeholder="服务名称" clearable style="width: 200px" />
      <el-select v-model="state.query.categoryId" placeholder="分类" clearable style="width: 160px">
        <el-option label="全部分类" value="" />
        <el-option v-for="c in state.categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-select v-model="state.query.status" placeholder="状态" clearable style="width: 120px">
        <el-option label="全部状态" value="" />
        <el-option label="上架" value="1" />
        <el-option label="下架" value="0" />
      </el-select>
      <el-button type="primary" @click="fetchList">查询</el-button>
      <el-button type="success" @click="openAddDialog">新增服务</el-button>
    </div>
    <el-table :data="state.list" v-loading="state.loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="服务名称" min-width="160" />
      <el-table-column label="分类" width="140">
        <template #default="scope">{{ getCategoryName(scope.row) }}</template>
      </el-table-column>
      <el-table-column prop="price" label="价格(元)" width="100" />
      <el-table-column prop="duration" label="时长(分钟)" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="Number(scope.row.status) === 1 ? 'success' : 'info'">{{ Number(scope.row.status) === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteService(scope.row.id)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑服务' : '新增服务'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="服务名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" style="width: 100%">
            <el-option v-for="c in state.categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="时长"><el-input-number v-model="form.duration" :min="5" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </AdminLayout>
</template>
