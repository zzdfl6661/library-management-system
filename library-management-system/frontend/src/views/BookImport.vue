
<template>
  <div class="acquisition-page">
    <el-card class="import-card">
      <template #header>
        <span>新书入库</span>
      </template>
      <el-form ref="importForm" :model="importForm" label-width="120px" class="import-form">
        <el-form-item label="ISBN">
          <el-input v-model="importForm.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="书名">
          <el-input v-model="importForm.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="importForm.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="importForm.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="简介">
          <el-textarea v-model="importForm.summary" placeholder="请输入简介" rows="3" />
        </el-form-item>
        <el-form-item label="副本数量">
          <el-input type="number" v-model="importForm.copyCount" placeholder="请输入副本数量" />
        </el-form-item>
        <el-form-item label="馆藏位置">
          <el-input v-model="importForm.location" placeholder="请输入馆藏位置" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleImport">确认入库</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="actions-card">
      <template #header>
        <span>其他操作</span>
      </template>
      <el-form label-width="120px" class="actions-form">
        <el-form-item label="图书条码">
          <el-input v-model="actionForm.barcode" placeholder="请输入图书条码" />
        </el-form-item>
        <el-form-item>
          <el-button type="danger" @click="handleDiscard">报废图书</el-button>
        </el-form-item>
        <el-form-item label="图书ID">
          <el-input type="number" v-model="actionForm.bookId" placeholder="请输入图书ID" />
        </el-form-item>
        <el-form-item>
          <el-button type="warning" @click="handleWithdraw">下架图书</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const importForm = reactive({
  isbn: '',
  title: '',
  author: '',
  publisher: '',
  summary: '',
  copyCount: 1,
  location: ''
})

const actionForm = reactive({
  barcode: '',
  bookId: ''
})

const handleImport = async () => {
  try {
    const response = await request.post('/acquisition/books', importForm)
    if (response.code === 200) {
      ElMessage.success(response.message)
      importForm.isbn = ''
      importForm.title = ''
      importForm.author = ''
      importForm.publisher = ''
      importForm.summary = ''
      importForm.copyCount = 1
      importForm.location = ''
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('入库失败')
  }
}

const handleDiscard = async () => {
  if (!actionForm.barcode) {
    ElMessage.warning('请输入图书条码')
    return
  }
  try {
    const response = await request.put(`/acquisition/books/${actionForm.barcode}/discard`)
    if (response.code === 200) {
      ElMessage.success(response.message)
      actionForm.barcode = ''
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('报废失败')
  }
}

const handleWithdraw = async () => {
  if (!actionForm.bookId) {
    ElMessage.warning('请输入图书ID')
    return
  }
  try {
    const response = await request.put(`/acquisition/books/${actionForm.bookId}/withdraw`)
    if (response.code === 200) {
      ElMessage.success(response.message)
      actionForm.bookId = ''
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('下架失败')
  }
}
</script>

<style scoped>
.acquisition-page {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.import-card {
  margin-bottom: 20px;
}

.import-form {
  max-width: 600px;
}

.actions-card {
  max-width: 600px;
}
</style>
