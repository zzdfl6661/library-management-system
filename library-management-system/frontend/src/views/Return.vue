
<template>
  <div class="return-page">
    <el-card class="return-card">
      <template #header>
        <span>还书操作</span>
      </template>
      <div class="form-section">
        <el-form-item label="借书证号">
          <el-input v-model="returnForm.cardNo" placeholder="请扫描或输入借书证号" @change="checkCard" />
        </el-form-item>
        <div v-if="cardInfo" class="card-info">
          <p>学生ID：{{ cardInfo.studentId }}</p>
          <p>状态：<span :class="cardInfo.status === 'NORMAL' ? 'text-success' : 'text-danger'">{{ getCardStatusText(cardInfo.status) }}</span></p>
        </div>
      </div>
      <div class="form-section">
        <el-form-item label="图书条码">
          <el-input v-model="returnForm.barcode" placeholder="请扫描或输入图书条码" @change="checkBook" />
        </el-form-item>
        <div v-if="bookInfo" class="book-info">
          <p>书名：{{ bookInfo.title }}</p>
          <p>借阅日期：{{ bookInfo.borrowDate }}</p>
          <p>应还日期：{{ bookInfo.dueDate }}</p>
          <p v-if="bookInfo.isOverdue" class="text-danger">已超期 {{ bookInfo.overdueDays }} 天</p>
        </div>
      </div>
      <el-button type="primary" @click="handleReturn" :disabled="!canSubmit">确认还书</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const returnForm = reactive({
  cardNo: '',
  barcode: ''
})

const cardInfo = ref(null)
const bookInfo = ref(null)

const canSubmit = computed(() => {
  return returnForm.cardNo && returnForm.barcode && cardInfo.value && bookInfo.value
})

const getCardStatusText = (status) => {
  const texts = {
    'NORMAL': '正常',
    'LOST': '挂失',
    'CANCELLED': '注销'
  }
  return texts[status] || status
}

const checkCard = async () => {
  if (!returnForm.cardNo) return
  try {
    const response = await request.get(`/cards/${returnForm.cardNo}`)
    if (response.code === 200) {
      cardInfo.value = response.data
    }
  } catch (error) {
    cardInfo.value = null
    ElMessage.error('借书证不存在')
  }
}

const checkBook = async () => {
  if (!returnForm.barcode) return
  try {
    const response = await request.get(`/borrow/check/${returnForm.barcode}`)
    if (response.code === 200) {
      bookInfo.value = response.data
    } else {
      bookInfo.value = null
      ElMessage.error(response.message)
    }
  } catch (error) {
    bookInfo.value = null
    ElMessage.error('无法查询图书信息')
  }
}

const handleReturn = async () => {
  try {
    const response = await request.post('/borrow/return', {
      cardNo: returnForm.cardNo,
      barcode: returnForm.barcode
    })
    if (response.code === 200) {
      ElMessage.success(response.message)
      returnForm.cardNo = ''
      returnForm.barcode = ''
      cardInfo.value = null
      bookInfo.value = null
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('还书失败')
  }
}
</script>

<style scoped>
.return-page {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.return-card {
  max-width: 600px;
}

.form-section {
  margin: 15px 0;
}

.book-info {
  margin-top: 10px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 4px;
}

.text-danger {
  color: #f56c6c;
}
</style>
