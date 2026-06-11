<template>
  <div class="my-fines">
    <el-card class="fine-summary">
      <div class="summary-row">
        <span class="label">未缴罚款总额：</span>
        <span class="amount">¥{{ unpaidTotal }}</span>
      </div>
    </el-card>

    <el-card class="fine-list">
      <template #header>
        <div class="header-row">
          <span>罚款记录</span>
          <el-button type="primary" size="small" @click="handleExport">导出记录</el-button>
        </div>
      </template>
      <el-table :data="unpaidFines" border stripe @selection-change="handleSelectionChange">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column type="selection" width="50" />
        <el-table-column prop="bookTitle" label="书名" min-width="150" />
        <el-table-column prop="author" label="作者" min-width="100" />
        <el-table-column prop="borDate" label="借书日期" width="110" />
        <el-table-column prop="retDate" label="应还日期" width="110" />
        <el-table-column prop="realRetDate" label="实还日期" width="110">
          <template #default="scope">
            {{ scope.row.realRetDate || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="罚款金额" width="100">
          <template #default="scope">
            <span class="text-danger">¥{{ scope.row.fineMoney }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <span :class="scope.row.fineStatus === '已缴' ? 'text-success' : 'text-danger'">
              {{ scope.row.fineStatus === '已缴' ? '已缴纳' : '未缴纳' }}
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="payment-card" v-if="selectedFines.length > 0">
      <template #header>
        <span>缴纳罚款</span>
      </template>
      <div class="payment-info">
        <span>已选取：{{ selectedFines.length }} 条记录</span>
        <span class="selected-amount">已选取金额合计：¥{{ selectedAmount }}</span>
      </div>
      <div class="payment-actions">
        <el-button type="primary" @click="handlePaySelected" :loading="loading">支付选中</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const unpaidTotal = ref('0.00')
const unpaidFines = ref([])
const selectedFines = ref([])
const loading = ref(false)

const selectedAmount = computed(() => {
  return selectedFines.value.reduce((sum, fine) => sum + (fine.fineMoney || 0), 0).toFixed(2)
})

const loadUnpaidFines = async () => {
  const studentNo = localStorage.getItem('studentNo')
  if (!studentNo) return

  try {
    const [totalResponse, finesResponse] = await Promise.all([
      request.get(`/fine/student/${studentNo}/total`),
      request.get(`/fine/student/${studentNo}/unpaid`)
    ])
    if (totalResponse.code === 200) {
      unpaidTotal.value = totalResponse.data || '0.00'
    }
    if (finesResponse.code === 200) {
      unpaidFines.value = finesResponse.data || []
    }
  } catch (error) {
    console.error('获取罚款记录失败:', error)
  }
}

const handleSelectionChange = (selection) => {
  selectedFines.value = selection
}

const handlePaySelected = async () => {
  if (selectedFines.value.length === 0) {
    ElMessage.warning('请先选择要支付的罚款记录')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要支付选中的 ${selectedFines.value.length} 条罚款记录，总金额 ¥${selectedAmount.value} 吗？`,
      '确认支付',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )

    loading.value = true
    const serNums = selectedFines.value.map(f => f.serNum)
    const response = await request.post('/fine/pay', {
      serNums: serNums,
      amount: parseFloat(selectedAmount.value)
    })

    if (response.code === 200) {
      ElMessage.success('支付成功')
      selectedFines.value = []
      loadUnpaidFines()
    } else {
      ElMessage.error(response.message || '支付失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error('支付失败')
    }
  } finally {
    loading.value = false
  }
}

const handleExport = () => {
  const data = unpaidFines.value.map((fine, index) => ({
    序号: index + 1,
    书名: fine.bookTitle || fine.bname || '',
    作者: fine.author || '',
    借书日期: fine.borDate,
    应还日期: fine.retDate,
    实还日期: fine.realRetDate || '-',
    罚款金额: fine.fineMoney,
    状态: fine.fineStatus === '已缴' ? '已缴纳' : '未缴纳'
  }))

  const headers = ['序号', '书名', '作者', '借书日期', '应还日期', '实还日期', '罚款金额', '状态']
  const csvContent = [
    headers.join(','),
    ...data.map(row => headers.map(h => `"${row[h]}"`).join(','))
  ].join('\n')

  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `罚款记录_${new Date().toISOString().slice(0, 10)}.csv`
  link.click()
  URL.revokeObjectURL(link.href)

  ElMessage.success('导出成功')
}

onMounted(() => {
  loadUnpaidFines()
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
  background: #fff3f3;
  border-color: #f56c6c;
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

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.payment-card {
  margin-top: 20px;
}

.payment-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.selected-amount {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.payment-actions {
  display: flex;
  gap: 10px;
}

.text-danger {
  color: #f56c6c;
}

.text-success {
  color: #67c23a;
}
</style>