<template>
  <div class="my-borrows">
    <el-card class="summary-card">
      <div class="summary-row">
        <span class="label">当前未缴纳罚款总金额：</span>
        <span class="amount">¥{{ unpaidFinesTotal }}</span>
      </div>
    </el-card>

    <div class="tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="当前借阅" name="current">
          <el-table :data="currentBorrows" border stripe>
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="bookTitle" label="书名" min-width="150" />
            <el-table-column prop="author" label="作者" min-width="100" />
            <el-table-column prop="borrowDate" label="借书日期" width="110" />
            <el-table-column prop="dueDate" label="应还日期" width="110" />
            <el-table-column prop="actualReturnDate" label="实际还书日期" width="120">
              <template #default="scope">
                {{ scope.row.actualReturnDate || '-' }}
              </template>
            </el-table-column>
            <el-table-column label="还书状态" width="100">
              <template #default="scope">
                <span :class="getStatusClass(scope.row)">
                  {{ getStatusText(scope.row) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="罚款金额" width="100">
              <template #default="scope">
                <span v-if="scope.row.fineAmount > 0" :class="scope.row.finePaid ? 'text-success' : 'text-danger'">
                  ¥{{ scope.row.fineAmount }}
                </span>
                <span v-else class="text-gray">-</span>
              </template>
            </el-table-column>
            <el-table-column label="罚款状态" width="100">
              <template #default="scope">
                <span v-if="scope.row.fineAmount > 0">
                  <span :class="scope.row.finePaid ? 'text-success' : 'text-danger'">
                    {{ scope.row.finePaid ? '已缴纳' : '未缴纳' }}
                  </span>
                </span>
                <span v-else class="text-gray">无罚款</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="借阅历史" name="history">
          <el-table :data="historyBorrows" border stripe>
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="bookTitle" label="书名" min-width="150" />
            <el-table-column prop="author" label="作者" min-width="100" />
            <el-table-column prop="borrowDate" label="借书日期" width="110" />
            <el-table-column prop="dueDate" label="应还日期" width="110" />
            <el-table-column prop="actualReturnDate" label="实际还书日期" width="120" />
            <el-table-column label="还书状态" width="100">
              <template #default="scope">
                <span :class="getStatusClass(scope.row)">
                  {{ getStatusText(scope.row) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="罚款金额" width="100">
              <template #default="scope">
                <span v-if="scope.row.fineAmount > 0" :class="scope.row.finePaid ? 'text-success' : 'text-danger'">
                  ¥{{ scope.row.fineAmount }}
                </span>
                <span v-else class="text-gray">-</span>
              </template>
            </el-table-column>
            <el-table-column label="罚款状态" width="100">
              <template #default="scope">
                <span v-if="scope.row.fineAmount > 0">
                  <span :class="scope.row.finePaid ? 'text-success' : 'text-danger'">
                    {{ scope.row.finePaid ? '已缴纳' : '未缴纳' }}
                  </span>
                </span>
                <span v-else class="text-gray">无罚款</span>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="totalHistory"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadHistoryBorrows"
            @current-change="loadHistoryBorrows"
            class="pagination"
          />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const activeTab = ref('current')
const currentBorrows = ref([])
const historyBorrows = ref([])
const unpaidFinesTotal = ref('0.00')

const currentPage = ref(1)
const pageSize = ref(10)
const totalHistory = ref(0)

const isOverdue = (row) => {
  if (!row.dueDate || row.actualReturnDate) return false
  const dueDate = new Date(row.dueDate)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return dueDate < today
}

const getStatusText = (row) => {
  if (row.actualReturnDate) return '已归还'
  if (isOverdue(row)) return '已超期'
  return '借阅中'
}

const getStatusClass = (row) => {
  if (row.actualReturnDate) return 'text-success'
  if (isOverdue(row)) return 'text-danger'
  return 'text-warning'
}

const loadCurrentBorrows = async () => {
  const studentNo = localStorage.getItem('studentNo')
  if (!studentNo) return

  try {
    const response = await request.get(`/borrow/student/${studentNo}/current`)
    if (response.code === 200) {
      currentBorrows.value = response.data || []
    }
  } catch (error) {
    console.error('获取当前借阅失败:', error)
  }
}

const loadHistoryBorrows = async () => {
  const studentNo = localStorage.getItem('studentNo')
  if (!studentNo) return

  try {
    const response = await request.get(`/borrow/student/${studentNo}`, {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })
    if (response.code === 200) {
      historyBorrows.value = response.data?.records || []
      totalHistory.value = response.data?.total || 0
    }
  } catch (error) {
    console.error('获取借阅历史失败:', error)
  }
}

const loadUnpaidFinesTotal = async () => {
  const studentNo = localStorage.getItem('studentNo')
  if (!studentNo) return

  try {
    const response = await request.get(`/fine/student/${studentNo}/total`)
    if (response.code === 200) {
      unpaidFinesTotal.value = response.data || '0.00'
    }
  } catch (error) {
    console.error('获取未缴罚款总金额失败:', error)
  }
}

onMounted(() => {
  loadCurrentBorrows()
  loadHistoryBorrows()
  loadUnpaidFinesTotal()
})
</script>

<style scoped>
.my-borrows {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.summary-card {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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
</style>
