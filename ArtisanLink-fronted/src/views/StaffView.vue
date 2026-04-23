<script setup>
import { ref, reactive, onMounted } from 'vue'
import AdminLayout from '../components/AdminLayout.vue'
import { adminApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const state = reactive({
  loading: false,
  list: [],
  total: 0,
  query: { merchantId: 1, name: '', position: '', status: '', pageNum: 1, pageSize: 10 }
})

// 新增/编辑对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const staffForm = reactive({
  id: null,
  merchantId: 1,
  name: '',
  phone: '',
  position: '',
  skillTags: [],
  workStart: '09:00',
  workEnd: '18:00',
  status: 1,
  avatar: ''
})

// 排班对话框
const scheduleDialogVisible = ref(false)
const currentStaff = ref({})
const scheduleDate = ref(dayjs().format('YYYY-MM-DD'))
const timeSlots = ref([])

// 加载员工列表
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
  } catch (error) {
    console.error('加载员工列表失败:', error)
    ElMessage.error('加载员工列表失败')
  } finally {
    state.loading = false
  }
}

// 打开新增对话框
function openAddDialog() {
  isEdit.value = false
  dialogTitle.value = '新增员工'
  resetForm()
  dialogVisible.value = true
}

// 打开编辑对话框
function openEditDialog(row) {
  isEdit.value = true
  dialogTitle.value = '编辑员工'
  Object.assign(staffForm, row)
  // 处理技能标签
  if (typeof row.skillTags === 'string') {
    staffForm.skillTags = row.skillTags.split(',').filter(t => t)
  } else if (Array.isArray(row.skillTags)) {
    staffForm.skillTags = [...row.skillTags]
  } else {
    staffForm.skillTags = []
  }
  dialogVisible.value = true
}

// 重置表单
function resetForm() {
  Object.assign(staffForm, {
    id: null,
    merchantId: 1,
    name: '',
    phone: '',
    position: '',
    skillTags: [],
    workStart: '09:00',
    workEnd: '18:00',
    status: 1,
    avatar: ''
  })
}

// 提交表单
async function submitForm() {
  if (!staffForm.name?.trim()) {
    ElMessage.warning('请输入员工姓名')
    return
  }
  
  if (!staffForm.phone?.trim()) {
    ElMessage.warning('请输入手机号')
    return
  }
  
  if (!/^1[3-9]\d{9}$/.test(staffForm.phone)) {
    ElMessage.warning('请输入正确的手机号码')
    return
  }
  
  try {
    const submitData = {
      ...staffForm,
      skillTags: Array.isArray(staffForm.skillTags) ? staffForm.skillTags.join(',') : staffForm.skillTags
    }
    
    let res
    if (isEdit.value) {
      res = await adminApi.updateStaff(submitData)
    } else {
      res = await adminApi.createStaff(submitData)
    }
    
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      await fetchList()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除员工
async function deleteStaff(id) {
  try {
    await ElMessageBox.confirm('确定要删除该员工吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await adminApi.deleteStaff(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await fetchList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 打开排班对话框
async function openScheduleDialog(row) {
  currentStaff.value = row
  scheduleDate.value = dayjs().format('YYYY-MM-DD')
  scheduleDialogVisible.value = true
  await loadTimeSlots()
}

// 加载时间片
async function loadTimeSlots() {
  try {
    const res = await adminApi.getStaffTimeSlots({
      staffId: currentStaff.value.id,
      date: scheduleDate.value
    })
    if (res.code === 200 && res.data) {
      timeSlots.value = res.data.list || []
    }
  } catch (error) {
    console.error('加载时间片失败:', error)
    ElMessage.error('加载时间片失败')
  }
}

// 切换日期
function handleDateChange() {
  loadTimeSlots()
}

// 批量设置时间片状态
async function batchUpdateTimeSlots(status) {
  try {
    const slotIds = timeSlots.value.map(s => s.id)
    if (slotIds.length === 0) {
      ElMessage.warning('暂无时间片数据')
      return
    }
    
    const res = await adminApi.batchUpdateTimeSlots({
      slotIds,
      status
    })
    
    if (res.code === 200) {
      ElMessage.success('设置成功')
      await loadTimeSlots()
    } else {
      ElMessage.error(res.msg || '设置失败')
    }
  } catch (error) {
    console.error('批量更新失败:', error)
    ElMessage.error('设置失败')
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
      <el-input v-model="state.query.name" placeholder="员工姓名" clearable style="width: 200px" />
      <el-input v-model="state.query.position" placeholder="岗位" clearable style="width: 200px" />
      <el-select v-model="state.query.status" placeholder="状态" clearable style="width: 150px">
        <el-option label="全部状态" value="" />
        <el-option label="在职" :value="1" />
        <el-option label="离职/停用" :value="0" />
      </el-select>
      <el-button type="primary" @click="fetchList">查询</el-button>
      <el-button type="success" @click="openAddDialog">新增员工</el-button>
    </div>
    
    <el-table :data="state.list" v-loading="state.loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="position" label="岗位" width="120" />
      <el-table-column label="技能标签" min-width="200">
        <template #default="{ row }">
          <el-tag v-for="tag in (Array.isArray(row.skillTags) ? row.skillTags : (row.skillTags || '').split(','))" 
                  :key="tag" 
                  size="small" 
                  style="margin-right: 4px">
            {{ tag }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="工作时间" width="150">
        <template #default="{ row }">
          {{ row.workStart || '09:00' }} - {{ row.workEnd || '18:00' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="Number(row.status) === 1 ? 'success' : 'info'">
            {{ Number(row.status) === 1 ? '在职' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="primary" @click="openScheduleDialog(row)">排班</el-button>
          <el-button size="small" type="danger" @click="deleteStaff(row.id)">删除</el-button>
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
    
    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="staffForm" label-width="100px">
        <el-form-item label="姓名" required>
          <el-input v-model="staffForm.name" placeholder="请输入姓名" maxlength="20" />
        </el-form-item>
        
        <el-form-item label="手机号" required>
          <el-input v-model="staffForm.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        
        <el-form-item label="岗位">
          <el-input v-model="staffForm.position" placeholder="请输入岗位" />
        </el-form-item>
        
        <el-form-item label="技能标签">
          <el-select v-model="staffForm.skillTags" multiple placeholder="请选择技能标签" style="width: 100%">
            <el-option label="美发" value="美发" />
            <el-option label="美容" value="美容" />
            <el-option label="美甲" value="美甲" />
            <el-option label="美睫" value="美睫" />
            <el-option label="纹绣" value="纹绣" />
            <el-option label="按摩" value="按摩" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="工作时间">
          <el-time-picker v-model="staffForm.workStart" placeholder="开始时间" format="HH:mm" value-format="HH:mm" style="width: 120px" />
          <span style="margin: 0 8px">至</span>
          <el-time-picker v-model="staffForm.workEnd" placeholder="结束时间" format="HH:mm" value-format="HH:mm" style="width: 120px" />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-radio-group v-model="staffForm.status">
            <el-radio :label="1">在职</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 排班对话框 -->
    <el-dialog v-model="scheduleDialogVisible" title="排班管理" width="700px">
      <div class="schedule-header">
        <span>技师：{{ currentStaff.name }}</span>
        <el-date-picker
          v-model="scheduleDate"
          type="date"
          placeholder="选择日期"
          @change="handleDateChange"
        />
      </div>
      
      <div class="schedule-actions">
        <el-button size="small" @click="batchUpdateTimeSlots(0)">全部设为可用</el-button>
        <el-button size="small" @click="batchUpdateTimeSlots(1)">全部设为不可用</el-button>
      </div>
      
      <div class="time-slots-grid">
        <div v-for="slot in timeSlots" :key="slot.id" 
             :class="['time-slot', { available: slot.status === 0, occupied: slot.status === 1 }]">
          <div class="slot-time">{{ slot.startTime }} - {{ slot.endTime }}</div>
          <el-tag :type="slot.status === 0 ? 'success' : 'info'" size="small">
            {{ slot.status === 0 ? '可用' : '已占用' }}
          </el-tag>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="scheduleDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </AdminLayout>
</template>
