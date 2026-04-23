<template>
  <customer-layout>
    <div class="review-container">
      <!-- 顶部标题 -->
      <div class="page-header">
        <h2>{{ isEditMode ? '编辑评价' : '发表评价' }}</h2>
        <el-button @click="goBack">返回</el-button>
      </div>
      
      <!-- 评价表单 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="review-form"
      >
        <!-- 订单信息 -->
        <el-card shadow="hover" class="order-info-card">
          <template #header>
            <div class="card-header">
              <span>订单信息</span>
            </div>
          </template>
          
          <div class="order-detail">
            <div class="detail-item">
              <span class="label">技师：</span>
              <span class="value">{{ orderInfo.technicianName || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">服务：</span>
              <span class="value">{{ orderInfo.serviceName || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">时间：</span>
              <span class="value">{{ orderInfo.appointmentTime || '-' }}</span>
            </div>
          </div>
        </el-card>
        
        <!-- 评分 -->
        <el-form-item label="整体评分" prop="rating">
          <el-rate
            v-model="form.rating"
            :max="5"
            show-score
            score-template="{value}分"
            size="large"
          />
        </el-form-item>
        
        <!-- 各维度评分 -->
        <el-form-item label="服务评分" prop="serviceRating">
          <el-rate
            v-model="form.serviceRating"
            :max="5"
            show-score
            score-template="{value}分"
          />
        </el-form-item>
        
        <el-form-item label="技术评分" prop="skillRating">
          <el-rate
            v-model="form.skillRating"
            :max="5"
            show-score
            score-template="{value}分"
          />
        </el-form-item>
        
        <el-form-item label="环境评分" prop="environmentRating">
          <el-rate
            v-model="form.environmentRating"
            :max="5"
            show-score
            score-template="{value}分"
          />
        </el-form-item>
        
        <!-- 评价内容 -->
        <el-form-item label="评价内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="请分享您的体验感受，帮助其他人做出更好的选择..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <!-- 上传图片 -->
        <el-form-item label="上传图片">
          <el-upload
            v-model:file-list="fileList"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :limit="9"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">最多上传9张图片，支持jpg/png格式</div>
        </el-form-item>
        
        <!-- 匿名评价 -->
        <el-form-item label="匿名评价">
          <el-switch v-model="form.isAnonymous" />
          <span class="tip-text">开启后，您的评价将不会显示用户名</span>
        </el-form-item>
        
        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEditMode ? '保存修改' : '提交评价' }}
          </el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </customer-layout>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import CustomerLayout from '@/components/CustomerLayout.vue'
import { customerApi } from '@/api/customer'

const router = useRouter()
const route = useRoute()

// 状态
const formRef = ref(null)
const submitting = ref(false)
const fileList = ref([])
const isEditMode = computed(() => !!route.query.editId)
const orderId = computed(() => route.query.orderId)

// 订单信息
const orderInfo = ref({
  technicianName: '',
  serviceName: '',
  appointmentTime: ''
})

// 表单数据
const form = reactive({
  rating: 5,
  serviceRating: 5,
  skillRating: 5,
  environmentRating: 5,
  content: '',
  isAnonymous: false,
  images: []
})

// 表单验证规则
const rules = {
  rating: [
    { required: true, message: '请选择整体评分', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 10, message: '评价内容至少10个字符', trigger: 'blur' }
  ]
}

/**
 * 处理文件变化
 */
function handleFileChange(file, files) {
  if (files.length > 9) {
    ElMessage.warning('最多只能上传9张图片')
    return false
  }
  
  // 验证图片格式
  const isImage = file.raw.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  
  // 验证图片大小（不超过5MB）
  const isLt5M = file.raw.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  
  return true
}

/**
 * 处理文件删除
 */
function handleFileRemove(file, files) {
  // 可以在这里处理删除逻辑
}

/**
 * 加载订单信息
 */
async function loadOrderInfo() {
  if (!orderId.value) {
    ElMessage.error('订单ID不存在')
    goBack()
    return
  }
  
  try {
    const res = await customerApi.getOrderDetail(orderId.value)
    
    if (res.code === 200) {
      const data = res.data
      orderInfo.value = {
        technicianName: data.technicianName || '-',
        serviceName: data.serviceName || '-',
        appointmentTime: data.appointmentTime || '-'
      }
    } else {
      ElMessage.error(res.msg || '加载订单信息失败')
      goBack()
    }
  } catch (error) {
    console.error('加载订单信息失败:', error)
    ElMessage.error('加载失败，请稍后重试')
    goBack()
  }
}

/**
 * 加载已有评价（编辑模式）
 */
async function loadExistingReview() {
  if (!isEditMode.value) return
  
  const reviewId = route.query.editId
  
  try {
    const res = await customerApi.getReviewDetail(reviewId)
    
    if (res.code === 200) {
      const data = res.data
      form.rating = data.rating || 5
      form.serviceRating = data.serviceRating || 5
      form.skillRating = data.skillRating || 5
      form.environmentRating = data.environmentRating || 5
      form.content = data.content || ''
      form.isAnonymous = data.isAnonymous || false
      
      // 加载已有图片
      if (data.images && data.images.length > 0) {
        fileList.value = data.images.map((url, index) => ({
          uid: index,
          name: `image-${index}`,
          url: url
        }))
      }
    } else {
      ElMessage.error(res.msg || '加载评价失败')
    }
  } catch (error) {
    console.error('加载评价失败:', error)
    ElMessage.error('加载失败，请稍后重试')
  }
}

/**
 * 提交评价
 */
async function handleSubmit() {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请完善表单信息')
      return
    }
    
    submitting.value = true
    
    try {
      // 上传图片（如果有）
      let imageUrls = []
      if (fileList.value.length > 0) {
        for (const file of fileList.value) {
          if (file.raw) {
            // 实际项目中需要调用上传接口
            // const uploadRes = await customerApi.uploadImage(file.raw)
            // if (uploadRes.code === 200) {
            //   imageUrls.push(uploadRes.data.url)
            // }
            // 这里模拟上传成功
            imageUrls.push(`https://example.com/images/${Date.now()}-${Math.random()}.jpg`)
          } else if (file.url) {
            imageUrls.push(file.url)
          }
        }
      }
      
      // 构建提交数据
      const submitData = {
        orderId: orderId.value,
        rating: form.rating,
        serviceRating: form.serviceRating,
        skillRating: form.skillRating,
        environmentRating: form.environmentRating,
        content: form.content,
        isAnonymous: form.isAnonymous,
        images: imageUrls
      }
      
      // 如果是编辑模式，添加评价ID
      if (isEditMode.value) {
        submitData.id = route.query.editId
      }
      
      // 提交评价
      const res = isEditMode.value
        ? await customerApi.updateReview(submitData)
        : await customerApi.createReview(submitData)
      
      if (res.code === 200) {
        ElMessage.success(isEditMode.value ? '修改成功' : '评价提交成功')
        
        // 延迟跳转，让用户看到成功提示
        setTimeout(() => {
          goBack()
        }, 1500)
      } else {
        ElMessage.error(res.msg || '提交失败')
      }
    } catch (error) {
      console.error('提交评价失败:', error)
      ElMessage.error('提交失败，请稍后重试')
    } finally {
      submitting.value = false
    }
  })
}

/**
 * 返回上一页
 */
function goBack() {
  router.back()
}

onMounted(() => {
  loadOrderInfo()
  loadExistingReview()
})
</script>

<style scoped>
.review-container {
  padding: 24px;
  background: #fff;
  border-radius: 8px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.review-form {
  max-width: 800px;
}

.order-info-card {
  margin-bottom: 24px;
}

.card-header {
  font-weight: 600;
}

.order-detail {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
}

.detail-item .label {
  color: #909399;
  margin-right: 8px;
}

.detail-item .value {
  color: #303133;
  font-weight: 500;
}

.tip-text {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

:deep(.el-rate__icon) {
  font-size: 24px;
}
</style>
