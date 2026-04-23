<template>
  <customer-layout>
    <div class="case-detail-container" v-loading="loading">
      <!-- 案例图片轮播 -->
      <el-card shadow="hover" class="image-card">
        <el-carousel height="500px" indicator-position="outside">
          <el-carousel-item v-for="(image, index) in caseDetail.images" :key="index">
            <el-image
              :src="image"
              fit="contain"
              style="width: 100%; height: 100%;"
            >
              <template #error>
                <div class="image-placeholder">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </el-carousel-item>
        </el-carousel>
      </el-card>
      
      <!-- 案例信息 -->
      <el-card shadow="hover" class="info-card">
        <h1 class="case-title">{{ caseDetail.title }}</h1>
        
        <div class="case-meta">
          <div class="technician-info" @click="viewTechnician">
            <el-avatar :size="48" :src="caseDetail.technicianAvatar">
              {{ caseDetail.technicianName?.charAt(0) || '技' }}
            </el-avatar>
            <div class="technician-details">
              <div class="technician-name">{{ caseDetail.technicianName }}</div>
              <div class="technician-title">{{ caseDetail.technicianTitle }}</div>
            </div>
          </div>
          
          <div class="stats">
            <div class="stat-item">
              <el-icon><View /></el-icon>
              <span>{{ formatNumber(caseDetail.viewCount) }} 浏览</span>
            </div>
            <div class="stat-item" @click="toggleLike">
              <el-icon :class="{ 'liked': caseDetail.isLiked }"><Star /></el-icon>
              <span>{{ formatNumber(caseDetail.likeCount) }} 点赞</span>
            </div>
            <div class="stat-item">
              <el-icon><ChatDotRound /></el-icon>
              <span>{{ formatNumber(caseDetail.commentCount) }} 评论</span>
            </div>
          </div>
        </div>
        
        <div class="case-tags">
          <el-tag
            v-for="tag in caseDetail.tags"
            :key="tag"
            size="large"
            effect="plain"
          >
            {{ tag }}
          </el-tag>
        </div>
        
        <div class="case-description">
          <h3>案例描述</h3>
          <p>{{ caseDetail.description }}</p>
        </div>
        
        <div class="service-info" v-if="caseDetail.serviceName">
          <h3>相关服务</h3>
          <el-tag type="primary" size="large">{{ caseDetail.serviceName }}</el-tag>
          <span class="service-price">¥{{ caseDetail.servicePrice }}</span>
        </div>
        
        <div class="action-buttons">
          <el-button
            type="primary"
            size="large"
            @click="bookTechnician"
            :disabled="!caseDetail.technicianId"
          >
            <el-icon><Calendar /></el-icon>
            预约技师
          </el-button>
          
          <el-button
            size="large"
            @click="shareCase"
          >
            <el-icon><Share /></el-icon>
            分享案例
          </el-button>
        </div>
      </el-card>
      
      <!-- 评论区 -->
      <el-card shadow="hover" class="comment-card">
        <template #header>
          <div class="card-header">
            <span>评论 ({{ caseDetail.commentCount }})</span>
          </div>
        </template>
        
        <!-- 发表评论 -->
        <div class="comment-input">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="分享你的看法..."
            maxlength="500"
            show-word-limit
          />
          <div class="comment-actions">
            <el-button type="primary" @click="submitComment" :loading="submitting">
              发表评论
            </el-button>
          </div>
        </div>
        
        <!-- 评论列表 -->
        <div class="comment-list">
          <div
            v-for="comment in comments"
            :key="comment.id"
            class="comment-item"
          >
            <el-avatar :size="40" :src="comment.userAvatar">
              {{ comment.userName?.charAt(0) || '用' }}
            </el-avatar>
            
            <div class="comment-content">
              <div class="comment-header">
                <span class="user-name">{{ comment.userName }}</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
              
              <p class="comment-text">{{ comment.content }}</p>
              
              <div class="comment-footer">
                <span class="like-btn" @click="likeComment(comment)">
                  <el-icon><Star /></el-icon>
                  {{ comment.likeCount || 0 }}
                </span>
                <span class="reply-btn" @click="replyToComment(comment)">
                  回复
                </span>
              </div>
            </div>
          </div>
          
          <el-empty v-if="comments.length === 0" description="暂无评论，快来发表第一条评论吧！" />
          
          <!-- 加载更多 -->
          <div class="load-more" v-if="hasMoreComments">
            <el-button @click="loadMoreComments" :loading="loadingMore">
              加载更多
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
  </customer-layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture, View, Star, ChatDotRound, Calendar, Share } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import CustomerLayout from '@/components/CustomerLayout.vue'
import { customerApi } from '@/api/customer'

const router = useRouter()
const route = useRoute()

// 状态
const loading = ref(false)
const submitting = ref(false)
const loadingMore = ref(false)
const caseDetail = ref({})
const comments = ref([])
const newComment = ref('')
const hasMoreComments = ref(true)
const commentPage = ref(1)
const commentPageSize = ref(10)

/**
 * 格式化数字
 */
function formatNumber(num) {
  if (!num) return 0
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num
}

/**
 * 格式化时间
 */
function formatTime(time) {
  if (!time) return ''
  const now = dayjs()
  const createTime = dayjs(time)
  const diff = now.diff(createTime, 'minute')
  
  if (diff < 1) return '刚刚'
  if (diff < 60) return `${diff}分钟前`
  if (diff < 1440) return `${Math.floor(diff / 60)}小时前`
  if (diff < 10080) return `${Math.floor(diff / 1440)}天前`
  
  return createTime.format('YYYY-MM-DD HH:mm')
}

/**
 * 加载案例详情
 */
async function loadCaseDetail() {
  loading.value = true
  
  try {
    const caseId = route.params.id
    const res = await customerApi.getCaseDetail(caseId)
    
    if (res.code === 200) {
      caseDetail.value = res.data
      
      // 增加浏览量
      await customerApi.incrementCaseView(caseId)
    } else {
      ElMessage.error(res.msg || '加载失败')
      router.back()
    }
  } catch (error) {
    console.error('加载案例详情失败:', error)
    ElMessage.error('加载失败，请稍后重试')
    router.back()
  } finally {
    loading.value = false
  }
}

/**
 * 加载评论列表
 */
async function loadComments(page = 1) {
  try {
    const caseId = route.params.id
    const res = await customerApi.getCaseComments(caseId, {
      page,
      pageSize: commentPageSize.value
    })
    
    if (res.code === 200) {
      if (page === 1) {
        comments.value = res.data.list || []
      } else {
        comments.value.push(...(res.data.list || []))
      }
      
      hasMoreComments.value = comments.value.length < (res.data.total || 0)
    }
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

/**
 * 加载更多评论
 */
async function loadMoreComments() {
  loadingMore.value = true
  commentPage.value++
  await loadComments(commentPage.value)
  loadingMore.value = false
}

/**
 * 提交评论
 */
async function submitComment() {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  submitting.value = true
  
  try {
    const caseId = route.params.id
    const res = await customerApi.addCaseComment(caseId, {
      content: newComment.value.trim()
    })
    
    if (res.code === 200) {
      ElMessage.success('评论成功')
      newComment.value = ''
      
      // 重新加载评论
      commentPage.value = 1
      await loadComments(1)
      
      // 更新评论数
      caseDetail.value.commentCount = (caseDetail.value.commentCount || 0) + 1
    } else {
      ElMessage.error(res.msg || '评论失败')
    }
  } catch (error) {
    console.error('评论失败:', error)
    ElMessage.error('评论失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

/**
 * 点赞案例
 */
async function toggleLike() {
  try {
    const caseId = route.params.id
    const res = await customerApi.toggleCaseLike(caseId)
    
    if (res.code === 200) {
      caseDetail.value.isLiked = !caseDetail.value.isLiked
      caseDetail.value.likeCount += caseDetail.value.isLiked ? 1 : -1
      ElMessage.success(caseDetail.value.isLiked ? '点赞成功' : '已取消点赞')
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

/**
 * 点赞评论
 */
async function likeComment(comment) {
  try {
    const res = await customerApi.toggleCommentLike(comment.id)
    
    if (res.code === 200) {
      comment.isLiked = !comment.isLiked
      comment.likeCount = (comment.likeCount || 0) + (comment.isLiked ? 1 : -1)
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

/**
 * 回复评论
 */
function replyToComment(comment) {
  newComment.value = `@${comment.userName} `
  // 聚焦到输入框
  document.querySelector('.comment-input textarea')?.focus()
}

/**
 * 查看技师详情
 */
function viewTechnician() {
  if (caseDetail.value.technicianId) {
    router.push(`/customer/technician/${caseDetail.value.technicianId}`)
  }
}

/**
 * 预约技师
 */
function bookTechnician() {
  if (caseDetail.value.technicianId) {
    router.push(`/customer/booking?technicianId=${caseDetail.value.technicianId}`)
  }
}

/**
 * 分享案例
 */
function shareCase() {
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.warning('复制失败，请手动复制链接')
  })
}

onMounted(() => {
  loadCaseDetail()
  loadComments(1)
})
</script>

<style scoped>
.case-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.image-card {
  border-radius: 8px;
  overflow: hidden;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
  font-size: 64px;
}

.info-card {
  border-radius: 8px;
}

.case-title {
  margin: 0 0 24px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
}

.case-meta {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #ebeef5;
}

.technician-info {
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.technician-info:hover {
  opacity: 0.8;
}

.technician-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.technician-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.technician-title {
  font-size: 14px;
  color: #909399;
}

.stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s;
}

.stat-item:hover {
  color: #409eff;
}

.stat-item .el-icon.liked {
  color: #f56c6c;
}

.case-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
}

.case-description,
.service-info {
  margin-bottom: 24px;
}

.case-description h3,
.service-info h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.case-description p {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}

.service-price {
  margin-left: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}

.action-buttons {
  display: flex;
  gap: 16px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

.comment-card {
  border-radius: 8px;
}

.card-header {
  font-weight: 600;
  font-size: 16px;
}

.comment-input {
  margin-bottom: 24px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.comment-item {
  display: flex;
  gap: 16px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-text {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.comment-footer {
  display: flex;
  gap: 16px;
}

.like-btn,
.reply-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
  cursor: pointer;
  transition: all 0.3s;
}

.like-btn:hover,
.reply-btn:hover {
  color: #409eff;
}

.load-more {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .case-detail-container {
    padding: 16px;
  }
  
  .case-title {
    font-size: 20px;
  }
  
  .case-meta {
    flex-direction: column;
    gap: 16px;
  }
  
  .stats {
    width: 100%;
    justify-content: space-around;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons .el-button {
    width: 100%;
  }
}
</style>
