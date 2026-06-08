
<template>
  <div class="my-borrows">
    <div v-if="paidFines.length > 0" class="paid-fines-box">
      <h3>已还款记录</h3>
      <el-table :data="paidFines" border>
        <el-table-column prop="bookTitle" label="关联图书" />
        <el-table-column prop="fineAmount" label="还款金额" formatter="¥{0}" />
        <el-table-column prop="createTime" label="还款时间" />
      </el-table>
    </div>
    
    <div class="tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="当前借阅" name="current">
          <el-table :data="currentBorrows" border>
            <el-table-column prop="bookTitle" label="书名" />
            <el-table-column prop="barcode" label="条码" />
            <el-table-column prop="borrowDate" label="借阅日期" />
            <el-table-column prop="dueDate" label="应还日期" />
            <el-table-column label="状态">
              <template #default="scope">
                <span :class="isOverdue(scope.row) ? 'text-danger' : 'text-warning'">
                  {{ isOverdue(scope.row) ? '已超期' : '借阅中' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="罚款状态">
              <template #default="scope">
                <div v-if="scope.row.hasFine === 1" class="fine-info">
                  <span :class="scope.row.finePaid === 1 ? 'text-success' : 'text-danger'">
                    {{ scope.row.finePaid === 1 ? '已还款' : '待还款' }}
                  </span>
                  <span v-if="scope.row.finePaid === 0" class="fine-amount">
                    ¥{{ scope.row.fineAmount }}
                  </span>
                </div>
                <span v-else class="text-gray">无罚款</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="借阅历史" name="history">
          <el-table :data="historyBorrows" border>
            <el-table-column prop="bookTitle" label="书名" />
            <el-table-column prop="barcode" label="条码" />
            <el-table-column prop="borrowDate" label="借阅日期" />
            <el-table-column prop="returnDate" label="归还日期" />
            <el-table-column label="罚款状态">
              <template #default="scope">
                <div v-if="scope.row.hasFine === 1" class="fine-info">
                  <span :class="scope.row.finePaid === 1 ? 'text-success' : 'text-danger'">
                    {{ scope.row.finePaid === 1 ? '已还款' : '待还款' }}
                  </span>
                  <span class="fine-amount">¥{{ scope.row.fineAmount }}</span>
                </div>
                <span v-else class="text-gray">无罚款</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '../utils/request'

const activeTab = ref('current')
const allBorrows = ref([])

const isOverdue = (row) => {
  if (!row.dueDate) return false
  const dueDate = new Date(row.dueDate)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return dueDate < today
}

const currentBorrows = computed(() => {
  return allBorrows.value.filter(b => !b.returnDate)
})

const historyBorrows = computed(() => {
  return allBorrows.value.filter(b => b.returnDate)
})

const paidFines = computed(() => {
  return allBorrows.value.filter(b => b.hasFine === 1 && b.finePaid === 1)
})

onMounted(async () => {
  const studentNo = localStorage.getItem('studentNo') || localStorage.getItem('username')
  if (!studentNo) return
  
  try {
    const response = await request.get(`/borrow/student/${studentNo}`)
    if (response.code === 200) {
      allBorrows.value = response.data || []
    }
  } catch (error) {
    console.error('获取借阅记录失败:', error)
  }
})
</script>

<style scoped>
.my-borrows {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.text-danger {
  color: #f56c6c;
}

.text-warning {
  color: #ff9800;
}

.text-success {
  color: #67c23a;
}

.text-gray {
  color: #909399;
}

.fine-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.fine-amount {
  font-weight: bold;
}

.paid-fines-box {
  background: #f0f9ff;
  border: 1px solid #e0f2fe;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.paid-fines-box h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #1f2937;
}
</style>
