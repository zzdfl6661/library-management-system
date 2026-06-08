
<template>
  <div class="my-borrows">
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
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="借阅历史" name="history">
          <el-table :data="historyBorrows" border>
            <el-table-column prop="bookTitle" label="书名" />
            <el-table-column prop="barcode" label="条码" />
            <el-table-column prop="borrowDate" label="借阅日期" />
            <el-table-column prop="returnDate" label="归还日期" />
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
</style>
