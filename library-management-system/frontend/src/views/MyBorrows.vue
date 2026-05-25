
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
                <span :class="scope.row.isOverdue ? 'text-danger' : 'text-warning'">
                  {{ scope.row.isOverdue ? '已超期' : '借阅中' }}
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
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const activeTab = ref('current')
const currentBorrows = ref([
  { bookTitle: 'Java编程思想', barcode: '97873021056330002', borrowDate: '2024-01-10', dueDate: '2024-02-10', isOverdue: true },
  { bookTitle: '红楼梦', barcode: '97870100425070003', borrowDate: '2024-01-15', dueDate: '2024-02-15', isOverdue: false }
])
const historyBorrows = ref([
  { bookTitle: '三国演义', barcode: '97870200022070001', borrowDate: '2023-11-01', returnDate: '2023-12-01' },
  { bookTitle: '算法导论', barcode: '97871110755470001', borrowDate: '2023-10-15', returnDate: '2023-11-15' }
])

onMounted(async () => {
  try {
    const response = await request.get('/borrow/student/2021001')
    if (response.code === 200) {
      console.log(response.data)
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
