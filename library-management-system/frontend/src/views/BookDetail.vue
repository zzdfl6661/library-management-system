<template>
  <div class="book-detail" v-loading="loading">
    <el-button @click="goBack" class="back-button">
      <i class="el-icon-arrow-left"></i> 返回列表
    </el-button>

    <el-card class="book-info-card">
      <template #header>
        <div class="card-header-title">
          <i class="el-icon-reading"></i>
          <span>图书基本信息</span>
        </div>
      </template>
      <div class="info-grid">
        <div class="info-item">
          <span class="label">ISBN：</span>
          <span class="value">{{ book.isbn || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">书名：</span>
          <span class="value">{{ book.title || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">作者：</span>
          <span class="value">{{ book.author || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">出版社：</span>
          <span class="value">{{ book.publisher || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">出版日期：</span>
          <span class="value">{{ book.publishDate || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">中图分类号：</span>
          <span class="value">{{ book.clc || '-' }}</span>
        </div>
        <div class="info-item full-width">
          <span class="label">简介：</span>
          <span class="value summary">{{ book.summary || '暂无简介' }}</span>
        </div>
      </div>
    </el-card>

    <el-card class="copies-card">
      <template #header>
        <div class="card-header-title">
          <i class="el-icon-notebook-2"></i>
          <span>馆藏副本</span>
        </div>
      </template>
      <el-table :data="copies" border>
        <el-table-column prop="barcode" label="条码号" width="150" />
        <el-table-column prop="location" label="藏书位置" min-width="150" />
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="copies.length === 0" class="empty-copies">暂无副本信息</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../utils/request'

const route = useRoute()
const router = useRouter()

const book = ref({})
const copies = ref([])
const loading = ref(false)

const getStatusType = (status) => {
  const types = {
    'AVAILABLE': 'success',
    'BORROWED': 'warning',
    'DAMAGED': 'danger',
    'WITHDRAWN': 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    'AVAILABLE': '可借',
    'BORROWED': '借出',
    'DAMAGED': '损坏',
    'WITHDRAWN': '注销'
  }
  return texts[status] || status
}

const goBack = () => {
  router.push('/books')
}

const fetchBookDetail = async () => {
  loading.value = true
  try {
    const response = await request.get(`/book/${route.params.id}`)
    if (response.code === 200) {
      book.value = response.data
      copies.value = response.data.copies || []
    }
  } catch (error) {
    console.error('获取图书详情失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchBookDetail()
})
</script>

<style scoped>
.book-detail {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.back-button {
  margin-bottom: 20px;
}

.card-header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
}

.card-header-title i {
  font-size: 16px;
  color: #409eff;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-item {
  display: flex;
  align-items: flex-start;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-item .label {
  color: #666;
  font-weight: bold;
  min-width: 90px;
}

.info-item .value {
  color: #333;
  flex: 1;
}

.info-item .value.summary {
  line-height: 1.6;
  white-space: pre-wrap;
}

.copies-card {
  margin-top: 20px;
}

.empty-copies {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
