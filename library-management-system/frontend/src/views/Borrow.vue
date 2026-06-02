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
        <el-form-item label="图书条码">
          <el-input v-model="borrowForm.barcode" placeholder="请扫描或输入图书条码" />
        </el-form-item>
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
  barcode: ''
})

const cardInfo = ref(null)

const canSubmit = computed(() => {
  return borrowForm.cardNo && borrowForm.barcode && cardInfo.value && cardInfo.value.status === 'NORMAL'
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
    const response = await request.get(`/cards/${borrowForm.cardNo}/info`)
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
    const response = await request.post('/borrow', {
      cardNo: borrowForm.cardNo,
      barcodes: [borrowForm.barcode]
    })
    if (response.code === 200) {
      ElMessage.success(response.message)
      borrowForm.cardNo = ''
      borrowForm.barcode = ''
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
</style>
