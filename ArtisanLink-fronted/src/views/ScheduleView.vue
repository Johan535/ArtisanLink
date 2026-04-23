<template>
  <admin-layout>
    <div class="schedule-container">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>技师排班管理</span>
          </div>
        </template>
        
        <TechnicianSchedule />
      </el-card>
    </div>
  </admin-layout>
</template>

<script setup>
import AdminLayout from '@/components/AdminLayout.vue'
import TechnicianSchedule from '@/components/admin/TechnicianSchedule.vue'
</script>

<style scoped>
.schedule-container {
  padding: 24px;
}

.card-header {
  font-weight: 600;
  font-size: 18px;
}
</style>