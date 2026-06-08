
<template>
  <div class="my-fines">
    <el-card class="fine-summary">
      <template #header>
        <span>罚款概览</span>
      </template>
      <div class="summary-row">
        <span class="label">未缴罚款：</span>
        <span class="amount">¥{{ unpaidFine }}</span>
      </div>
    </el-card>
    <el-card class="fine-list">
      <template #header>
        <span>罚款记录</span>
      </template>
      <el-table :data="fineRecords" border>
        <el-table-column prop="borrowRecordId" label="借阅记录ID" />
        <el-table-column prop="amount" label="金额">
          <template #default="scope">
            ¥{{ scope.row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="days" label="超期天数" />
        <el-table-column prop="isPaid" label="状态">
          <template #default="scope">
            <span :class="scope.row.isPaid ? 'text-success' : 'text-danger'">
              {{ scope.row.isPaid ? '已缴纳' : '未缴纳' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button
              v-if="role !== 'OFFICE' && !scope.row.isPaid"
              type="primary"
              size="small"
              @click="handleRepay(scope.row)"
            >
              还款
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-card v-if="role === 'OFFICE'" class="payment-card">
      <template #header>
        <span>缴纳罚款</span>
      </template>
      <el-form label-width="120px" class="payment-form">
        <el-form-item label="学号">
          <el-input v-model="paymentForm.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="缴纳金额">
          <el-input type="number" v-model="paymentForm.amount" placeholder="请输入缴纳金额" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handlePayment">确认缴纳</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const unpaidFine = ref('0.00')
const fineRecords = ref([])

const role = ref(localStorage.getItem('role') || '')

const paymentForm = reactive({
  studentNo: '',
  amount: ''
})

const handlePayment = async () => {
  try {
    const response = await request.post('/fines/pay', {
      studentNo: paymentForm.studentNo,
      amount: paymentForm.amount,
      operatorId: localStorage.getItem('userId')
    })
    if (response.code === 200) {
      ElMessage.success(response.message)
      paymentForm.studentNo = ''
      paymentForm.amount = ''
      loadFines()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('缴纳失败')
  }
}

const handleRepay = async (row) => {
  try {
    console.log('Repaying fine:', row)
    const response = await request.post(`/fines/${row.id}/repay`)
    console.log('Response:', response)
    if (response.code === 200) {
      ElMessage.success(response.message)
      loadFines()
    } else {
      ElMessage.error(response.message || '还款失败')
    }
  } catch (error) {
    console.error('Repay error:', error)
    if (error.response) {
      ElMessage.error('还款失败: ' + error.response.data?.message || error.response.statusText)
    } else {
      ElMessage.error('还款失败: ' + error.message)
    }
  }
}

const loadFines = async () => {
  const studentNo = localStorage.getItem('studentNo') || localStorage.getItem('username')
  if (!studentNo) return
  
  try {
    const [totalResponse, recordsResponse] = await Promise.all([
      request.get(`/fines/student/${studentNo}/total`),
      request.get(`/fines/student/${studentNo}`)
    ])
    if (totalResponse.code === 200) {
      unpaidFine.value = totalResponse.data || '0.00'
    }
    if (recordsResponse.code === 200) {
      fineRecords.value = recordsResponse.data || []
    }
  } catch (error) {
    console.error('获取罚款记录失败:', error)
  }
}

onMounted(() => {
  loadFines()
})
</script>

<style scoped>
.my-fines {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.fine-summary {
  margin-bottom: 20px;
}

.summary-row {
  display: flex;
  align-items: center;
}

.label {
  font-size: 16px;
  color: #666;
}

.amount {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
  margin-left: 10px;
}

.fine-list {
  margin-bottom: 20px;
}

.text-success {
  color: #13ce66;
}

.text-danger {
  color: #f56c6c;
}

.payment-form {
  max-width: 400px;
}
</style>
