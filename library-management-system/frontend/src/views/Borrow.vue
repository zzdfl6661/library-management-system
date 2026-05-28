
<template>
  <div class="borrow-page">
    <el-card class="borrow-card">
      <template #header>
        <span>借书操作</span>
      </template>
      <div class="form-section">
        <el-form-item label="借书证号">
          <el-input v-model="borrowForm.cardNo" placeholder="请扫描或输入借书证号" @change="checkCard" />
        </el-form-item>
        <div v-if="cardInfo" class="card-info">
          <p>状态：<span :class="cardInfo.status === 'NORMAL' ? 'text-success' : 'text-danger'">{{ getCardStatusText(cardInfo.status) }}</span></p>
          <p>可借数量：{{ cardInfo.availableCount }}本</p>
        </div>
      </div>
      <div class="form-section">
        <label class="form-label">图书条码（每行一个）</label>
        <textarea v-model="borrowForm.barcodes" class="form-textarea" placeholder="请扫描或输入图书条码，每行一个" rows="5"></textarea>
      </div>
      <el-button type="primary" @click="handleBorrow" :disabled="!canSubmit">确认借书</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const borrowForm = reactive({
  cardNo: '',
  barcodes: ''
})

const cardInfo = ref(null)

const canSubmit = computed(() => {
  return borrowForm.cardNo && borrowForm.barcodes.trim() && cardInfo.value && cardInfo.value.status === 'NORMAL'
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
  if (!borrowForm.cardNo) return
  try {
    const response = await request.get(`/cards/${borrowForm.cardNo}`)
    if (response.code === 200) {
      cardInfo.value = response.data
    }
  } catch (error) {
    cardInfo.value = null
    ElMessage.error('借书证不存在')
  }
}

const handleBorrow = async () => {
  try {
    const barcodes = borrowForm.barcodes.trim().split('\n').filter(b => b.trim())
    const response = await request.post('/borrow', {
      cardNo: borrowForm.cardNo,
      barcodes: barcodes
    })
    if (response.code === 200) {
      ElMessage.success(response.message)
      borrowForm.cardNo = ''
      borrowForm.barcodes = ''
      cardInfo.value = null
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('借书失败')
  }
}
</script>

<style scoped>
.borrow-page {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.borrow-card {
  max-width: 600px;
}

.form-section {
  margin: 15px 0;
}

.card-info {
  margin-top: 10px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 4px;
}

.text-success {
  color: #13ce66;
}

.text-danger {
  color: #f56c6c;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #303133;
}

.form-textarea {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
}

.form-textarea:focus {
  outline: none;
  border-color: #409eff;
}
</style>
