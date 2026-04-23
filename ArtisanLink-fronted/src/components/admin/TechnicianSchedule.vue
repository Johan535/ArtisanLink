<template>
  <div class="technician-schedule-container">
    <!-- 顶部操作栏 -->
    <el-card shadow="hover" class="toolbar-card">
      <div class="toolbar">
        <div class="left-section">
          <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="选择日期"
            :disabled-date="disabledDate"
            @change="handleDateChange"
          />
          
          <el-select
            v-model="selectedTechnicianId"
            placeholder="选择技师"
            filterable
            clearable
            style="width: 200px; margin-left: 16px;"
            @change="handleTechnicianChange"
          >
            <el-option
              v-for="tech in technicianList"
              :key="tech.id"
              :label="tech.name"
              :value="tech.id"
            />
          </el-select>
        </div>
        
        <div class="right-section">
          <el-button type="primary" @click="showBatchSetDialog">
            <el-icon><Setting /></el-icon>
            批量设置
          </el-button>
          
          <el-button @click="refreshSchedule">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </el-card>
    
    <!-- 排班表格 -->
    <el-card shadow="hover" class="schedule-table-card">
      <el-table
        v-loading="loading"
        :data="scheduleData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="timeSlot" label="时间段" width="150" fixed />
        
        <el-table-column
          v-for="tech in displayedTechnicians"
          :key="tech.id"
          :label="tech.name"
          min-width="120"
        >
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row, tech.id)"
              size="large"
              effect="dark"
              @click="toggleTimeSlot(row, tech.id)"
              style="cursor: pointer;"
            >
              {{ getStatusText(row, tech.id) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 批量设置对话框 -->
    <el-dialog
      v-model="batchSetDialogVisible"
      title="批量设置排班"
      width="600px"
    >
      <el-form :model="batchSetForm" label-width="100px">
        <el-form-item label="选择技师">
          <el-select
            v-model="batchSetForm.technicianIds"
            multiple
            placeholder="请选择技师"
            filterable
            style="width: 100%;"
          >
            <el-option
              v-for="tech in technicianList"
              :key="tech.id"
              :label="tech.name"
              :value="tech.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="设置日期">
          <el-date-picker
            v-model="batchSetForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :disabled-date="disabledDate"
            style="width: 100%;"
          />
        </el-form-item>
        
        <el-form-item label="工作时间">
          <el-time-picker
            v-model="batchSetForm.workStartTime"
            placeholder="开始时间"
            format="HH:mm"
            value-format="HH:mm:ss"
            style="width: 48%; margin-right: 4%;"
          />
          <span style="margin: 0 8px;">至</span>
          <el-time-picker
            v-model="batchSetForm.workEndTime"
            placeholder="结束时间"
            format="HH:mm"
            value-format="HH:mm:ss"
            style="width: 48%;"
          />
        </el-form-item>
        
        <el-form-item label="休息时段">
          <el-checkbox-group v-model="batchSetForm.restPeriods">
            <el-checkbox label="12:00-13:00">午休 (12:00-13:00)</el-checkbox>
            <el-checkbox label="18:00-19:00">晚餐 (18:00-19:00)</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        
        <el-form-item label="时间片间隔">
          <el-select v-model="batchSetForm.interval" placeholder="请选择">
            <el-option label="30分钟" :value="30" />
            <el-option label="60分钟" :value="60" />
            <el-option label="90分钟" :value="90" />
            <el-option label="120分钟" :value="120" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="batchSetDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchSet" :loading="batchSetting">
          确认设置
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, Refresh } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { adminApi } from '@/api/index'

// 状态
const loading = ref(false)
const batchSetting = ref(false)
const selectedDate = ref(dayjs())
const selectedTechnicianId = ref(null)
const batchSetDialogVisible = ref(false)
const technicianList = ref([])
const scheduleData = ref([])

// 批量设置表单
const batchSetForm = reactive({
  technicianIds: [],
  dateRange: [],
  workStartTime: '09:00:00',
  workEndTime: '18:00:00',
  restPeriods: ['12:00-13:00'],
  interval: 60
})

// 显示的技师列表（根据选择过滤）
const displayedTechnicians = computed(() => {
  if (selectedTechnicianId.value) {
    return technicianList.value.filter(tech => tech.id === selectedTechnicianId.value)
  }
  return technicianList.value
})

/**
 * 禁用过去的日期
 */
function disabledDate(time) {
  return time.getTime() < Date.now() - 8.64e7
}

/**
 * 获取时间段状态类型
 */
function getStatusType(row, technicianId) {
  const status = getTimeSlotStatus(row.timeSlot, technicianId)
  
  switch (status) {
    case 'available':
      return 'success'
    case 'occupied':
      return 'danger'
    case 'rest':
      return 'info'
    default:
      return 'warning'
  }
}

/**
 * 获取时间段状态文本
 */
function getStatusText(row, technicianId) {
  const status = getTimeSlotStatus(row.timeSlot, technicianId)
  
  switch (status) {
    case 'available':
      return '可预约'
    case 'occupied':
      return '已占用'
    case 'rest':
      return '休息中'
    default:
      return '未设置'
  }
}

/**
 * 获取时间段状态
 */
function getTimeSlotStatus(timeSlot, technicianId) {
  // 这里应该从后端获取实际的状态
  // 暂时模拟数据
  const hash = `${timeSlot}-${technicianId}`.charCodeAt(0)
  
  if (hash % 3 === 0) return 'occupied'
  if (hash % 5 === 0) return 'rest'
  return 'available'
}

/**
 * 切换时间段状态
 */
async function toggleTimeSlot(row, technicianId) {
  const currentStatus = getTimeSlotStatus(row.timeSlot, technicianId)
  
  if (currentStatus === 'occupied') {
    ElMessage.warning('该时间段已被预约，无法修改')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要将 ${row.timeSlot} 设置为${currentStatus === 'available' ? '休息' : '可预约'}吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用后端接口更新状态
    const res = await adminApi.updateTechnicianSchedule({
      technicianId,
      date: selectedDate.value.format('YYYY-MM-DD'),
      timeSlot: row.timeSlot,
      status: currentStatus === 'available' ? 'rest' : 'available'
    })
    
    if (res.code === 200) {
      ElMessage.success('更新成功')
      refreshSchedule()
    } else {
      ElMessage.error(res.msg || '更新失败')
    }
  } catch (error) {
    // 用户取消操作
  }
}

/**
 * 显示批量设置对话框
 */
function showBatchSetDialog() {
  batchSetDialogVisible.value = true
  
  // 重置表单
  batchSetForm.technicianIds = []
  batchSetForm.dateRange = [
    dayjs(),
    dayjs().add(6, 'day')
  ]
  batchSetForm.workStartTime = '09:00:00'
  batchSetForm.workEndTime = '18:00:00'
  batchSetForm.restPeriods = ['12:00-13:00']
  batchSetForm.interval = 60
}

/**
 * 处理批量设置
 */
async function handleBatchSet() {
  if (!batchSetForm.technicianIds || batchSetForm.technicianIds.length === 0) {
    ElMessage.warning('请选择技师')
    return
  }
  
  if (!batchSetForm.dateRange || batchSetForm.dateRange.length !== 2) {
    ElMessage.warning('请选择日期范围')
    return
  }
  
  batchSetting.value = true
  
  try {
    const res = await adminApi.batchSetSchedule({
      technicianIds: batchSetForm.technicianIds,
      startDate: dayjs(batchSetForm.dateRange[0]).format('YYYY-MM-DD'),
      endDate: dayjs(batchSetForm.dateRange[1]).format('YYYY-MM-DD'),
      workStartTime: batchSetForm.workStartTime,
      workEndTime: batchSetForm.workEndTime,
      restPeriods: batchSetForm.restPeriods,
      interval: batchSetForm.interval
    })
    
    if (res.code === 200) {
      ElMessage.success('批量设置成功')
      batchSetDialogVisible.value = false
      refreshSchedule()
    } else {
      ElMessage.error(res.msg || '批量设置失败')
    }
  } catch (error) {
    console.error('批量设置失败:', error)
    ElMessage.error('批量设置失败，请稍后重试')
  } finally {
    batchSetting.value = false
  }
}

/**
 * 处理日期变化
 */
function handleDateChange() {
  refreshSchedule()
}

/**
 * 处理技师变化
 */
function handleTechnicianChange() {
  refreshSchedule()
}

/**
 * 刷新排班数据
 */
async function refreshSchedule() {
  loading.value = true
  
  try {
    // 加载技师列表
    if (technicianList.value.length === 0) {
      const techRes = await adminApi.getStaffList({ page: 1, pageSize: 100 })
      if (techRes.code === 200) {
        technicianList.value = techRes.data.list || []
      }
    }
    
    // 加载排班数据
    const params = {
      date: selectedDate.value.format('YYYY-MM-DD')
    }
    
    if (selectedTechnicianId.value) {
      params.technicianId = selectedTechnicianId.value
    }
    
    const scheduleRes = await adminApi.getTechnicianSchedule(params)
    
    if (scheduleRes.code === 200) {
      // 生成时间段数据
      const timeSlots = generateTimeSlots()
      scheduleData.value = timeSlots.map(time => ({
        timeSlot: time,
        ...scheduleRes.data[time] || {}
      }))
    } else {
      ElMessage.error(scheduleRes.msg || '加载排班数据失败')
    }
  } catch (error) {
    console.error('加载排班数据失败:', error)
    ElMessage.error('加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

/**
 * 生成时间段
 */
function generateTimeSlots() {
  const slots = []
  const startTime = dayjs().hour(9).minute(0)
  const endTime = dayjs().hour(18).minute(0)
  
  let currentTime = startTime
  while (currentTime.isBefore(endTime)) {
    const nextTime = currentTime.add(batchSetForm.interval, 'minute')
    slots.push(`${currentTime.format('HH:mm')}-${nextTime.format('HH:mm')}`)
    currentTime = nextTime
  }
  
  return slots
}

onMounted(() => {
  refreshSchedule()
})
</script>

<style scoped>
.technician-schedule-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.toolbar-card {
  border-radius: 8px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left-section,
.right-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.schedule-table-card {
  border-radius: 8px;
}

:deep(.el-table .el-table__cell) {
  padding: 12px 0;
}

:deep(.el-tag) {
  cursor: pointer;
  transition: all 0.3s;
}

:deep(.el-tag:hover) {
  transform: scale(1.05);
}
</style>
