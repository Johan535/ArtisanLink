<template>
  <div class="time-slot-picker">
    <!-- 日期选择 -->
    <div class="date-selector">
      <el-date-picker
        v-model="selectedDate"
        type="date"
        placeholder="选择日期"
        :disabled-date="disabledDate"
        :min-date="minDate"
        @change="handleDateChange"
        style="width: 100%"
      />
    </div>
    
    <!-- 时间段选择 -->
    <div class="time-slots-wrapper" v-loading="loading">
      <div class="section-title">可选时间段</div>
      
      <div class="time-slots-grid" v-if="timeSlots.length > 0">
        <div
          v-for="slot in timeSlots"
          :key="slot.id"
          class="time-slot-item"
          :class="{
            'selected': selectedSlot?.id === slot.id,
            'disabled': slot.status === 1 || isSlotSubmitting(slot.id),
            'occupied': slot.status === 1
          }"
          @click="handleSelectSlot(slot)"
        >
          <div class="time-text">{{ formatTime(slot.startTime) }} - {{ formatTime(slot.endTime) }}</div>
          <div class="status-text" v-if="slot.status === 1">已约满</div>
          <div class="status-text" v-else-if="isSlotSubmitting(slot.id)">提交中...</div>
        </div>
      </div>
      
      <el-empty v-else description="该日期暂无可预约时间段" />
    </div>
    
    <!-- 已选信息 -->
    <div class="selected-info" v-if="selectedSlot">
      <el-alert
        :title="`已选择：${formatDate(selectedDate)} ${formatTime(selectedSlot.startTime)}-${formatTime(selectedSlot.endTime)}`"
        type="success"
        show-icon
        :closable="false"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { customerApi } from '@/api/customer'

const props = defineProps({
  technicianId: {
    type: Number,
    required: true
  },
  modelValue: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'select'])

// 状态
const selectedDate = ref(dayjs().add(1, 'day').toDate())
const selectedSlot = ref(props.modelValue)
const timeSlots = ref([])
const loading = ref(false)
const submittingSlotIds = ref(new Set())

// 最小日期（今天）
const minDate = new Date()

/**
 * 禁用过去的日期
 */
function disabledDate(date) {
  return date.getTime() < Date.now() - 8.64e7
}

/**
 * 处理日期变化
 */
async function handleDateChange(date) {
  if (!date) return
  
  await loadTimeSlots()
}

/**
 * 加载时间片列表
 */
async function loadTimeSlots() {
  if (!props.technicianId || !selectedDate.value) return
  
  loading.value = true
  try {
    const dateStr = dayjs(selectedDate.value).format('YYYY-MM-DD')
    const res = await customerApi.getAvailableTimeSlots(props.technicianId, dateStr)
    
    if (res.code === 200) {
      timeSlots.value = res.data || []
    } else {
      ElMessage.error(res.msg || '加载时间片失败')
      timeSlots.value = []
    }
  } catch (error) {
    console.error('加载时间片失败:', error)
    ElMessage.error('加载时间片失败，请稍后重试')
    timeSlots.value = []
  } finally {
    loading.value = false
  }
}

/**
 * 选择时间片
 */
function handleSelectSlot(slot) {
  // 已占用的时间片不可选择
  if (slot.status === 1) {
    ElMessage.warning('该时间段已被预约，请选择其他时间')
    return
  }
  
  // 正在提交的时间片不可重复点击
  if (isSlotSubmitting(slot.id)) {
    return
  }
  
  selectedSlot.value = slot
  emit('update:modelValue', slot)
  emit('select', slot)
}

/**
 * 检查时间片是否正在提交
 */
function isSlotSubmitting(slotId) {
  return submittingSlotIds.value.has(slotId)
}

/**
 * 标记时间片开始提交
 */
function markSlotSubmitting(slotId, isSubmitting) {
  if (isSubmitting) {
    submittingSlotIds.value.add(slotId)
  } else {
    submittingSlotIds.value.delete(slotId)
  }
}

/**
 * 格式化时间
 */
function formatTime(timeStr) {
  if (!timeStr) return ''
  return timeStr.substring(0, 5) // HH:mm
}

/**
 * 格式化日期
 */
function formatDate(date) {
  if (!date) return ''
  return dayjs(date).format('MM月DD日 dddd')
}

// 监听技师ID变化，重新加载时间片
watch(() => props.technicianId, () => {
  if (props.technicianId) {
    loadTimeSlots()
  }
}, { immediate: true })

// 暴露方法给父组件
defineExpose({
  loadTimeSlots,
  markSlotSubmitting,
  selectedSlot,
  selectedDate
})
</script>

<style scoped>
.time-slot-picker {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
}

.date-selector {
  margin-bottom: 16px;
}

.section-title {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.time-slots-wrapper {
  min-height: 200px;
}

.time-slots-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
}

.time-slot-item {
  padding: 12px 8px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
}

.time-slot-item:hover:not(.disabled):not(.occupied) {
  border-color: #d4a574;
  background: #fdf9f3;
}

.time-slot-item.selected {
  border-color: #d4a574;
  background: #d4a574;
  color: #fff;
}

.time-slot-item.disabled,
.time-slot-item.occupied {
  border-color: #f0f0f0;
  background: #f9f9f9;
  color: #ccc;
  cursor: not-allowed;
}

.time-text {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 4px;
}

.status-text {
  font-size: 12px;
  color: #999;
}

.time-slot-item.selected .status-text {
  color: rgba(255, 255, 255, 0.9);
}

.selected-info {
  margin-top: 16px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .time-slots-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .time-slot-item {
    padding: 8px 4px;
  }
  
  .time-text {
    font-size: 12px;
  }
}
</style>
