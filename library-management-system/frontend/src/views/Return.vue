<template>
  <div class="return-page">
    <el-card class="return-card">
      <template #header>
        <span>还书操作</span>
      </template>
      
      <div class="return-form">
        <el-form label-width="120px">
          <el-form-item label="图书条码">
            <el-input 
              v-model="barCode" 
              placeholder="请扫描或输入图书条码" 
              @keyup.enter="handleReturn"
              clearable
              class="barcode-input"
            >
              <template #append>
                <el-button @click="handleReturn" :loading="returning" :disabled="!barCode">确认</el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="returnResult" class="return-result">
        <el-alert
          :title="returnResult.message"
          :type="returnResult.success ? 'success' : 'error'"
          :closable="false"
          show-icon
          class="mb-20"
        />
        
        <el-descriptions v-if="returnResult.success" :column="2" border>
          <el-descriptions-item label="超期天数">
            <span :class="returnResult.ovdDays > 0 ? 'text-danger' : ''">
              {{ returnResult.ovdDays || 0 }} 天
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="罚款金额">
            <span v-if="returnResult.fineMoney > 0" class="text-danger">
              ¥{{ returnResult.fineMoney.toFixed(2) }}
            </span>
            <span v-else class="text-success">无</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const barCode = ref('')
const returning = ref(false)
const returnResult = ref(null)

// 还书
const handleReturn = async () => {
  if (!barCode.value) {
    ElMessage.warning('请输入图书条码')
    return
  }

  returning.value = true
  returnResult.value = null
  
  try {
    const response = await request.post('/borrow/return', { barCode: barCode.value })
    if (response.code === 200) {
      returnResult.value = {
        success: true,
        message: '还书成功',
        ovdDays: response.data?.ovdDays || 0,
        fineMoney: response.data?.fineMoney || 0
      }
      barCode.value = ''
    } else {
      returnResult.value = {
        success: false,
        message: response.message || '还书失败'
      }
    }
  } catch (error) {
    returnResult.value = {
      success: false,
      message: '还书失败，请稍后重试'
    }
  } finally {
    returning.value = false
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

.return-form {
  margin-bottom: 20px;
}

.barcode-input {
  max-width: 400px;
}

.return-result {
  margin-top: 20px;
}

.mb-20 {
  margin-bottom: 20px;
}

.text-danger {
  color: #f56c6c;
}

.text-success {
  color: #13ce66;
}
</style>
