
<template>
  <div class="book-detail">
    <el-card class="book-info">
      <h2>{{ book.title }}</h2>
      <div class="info-row">
        <span class="label">作者：</span>
        <span>{{ book.author }}</span>
      </div>
      <div class="info-row">
        <span class="label">出版社：</span>
        <span>{{ book.publisher }}</span>
      </div>
      <div class="info-row">
        <span class="label">ISBN：</span>
        <span>{{ book.isbn }}</span>
      </div>
      <div class="info-row">
        <span class="label">简介：</span>
        <span>{{ book.summary }}</span>
      </div>
    </el-card>
    <el-card class="copies-info">
      <template #header>
        <span>馆藏副本</span>
      </template>
      <el-table :data="copies" border>
        <el-table-column prop="barcode" label="条形码" />
        <el-table-column prop="location" label="馆藏位置" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <span :class="getStatusClass(scope.row.status)">{{ getStatusText(scope.row.status) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-button @click="goBack">返回列表</el-button>
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

const getStatusClass = (status) => {
  const classes = {
    'AVAILABLE': 'status-available',
    'BORROWED': 'status-borrowed',
    'DAMAGED': 'status-damaged',
    'WITHDRAWN': 'status-withdrawn'
  }
  return classes[status] || ''
}

const getStatusText = (status) => {
  const texts = {
    'AVAILABLE': '可借',
    'BORROWED': '已借出',
    'DAMAGED': '损坏',
    'WITHDRAWN': '下架'
  }
  return texts[status] || status
}

const goBack = () => {
  router.push('/books')
}

onMounted(async () => {
  try {
    const response = await request.get(`/books/${route.params.id}`)
    if (response.code === 200) {
      book.value = response.data
      copies.value = response.data.copies || []
    }
  } catch (error) {
    console.error('获取图书详情失败:', error)
  }
})
</script>

<style scoped>
.book-detail {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.book-info {
  margin-bottom: 20px;
}

.book-info h2 {
  margin-bottom: 20px;
  color: #333;
}

.info-row {
  margin: 10px 0;
}

.label {
  color: #666;
  font-weight: bold;
}

.copies-info {
  margin-bottom: 20px;
}

.status-available {
  color: #13ce66;
}

.status-borrowed {
  color: #ff9800;
}

.status-damaged {
  color: #f56c6c;
}

.status-withdrawn {
  color: #909399;
}
</style>
