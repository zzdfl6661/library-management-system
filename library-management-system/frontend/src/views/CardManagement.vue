
<template>
  <div class="card-management">
    <div class="tabs">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="借书证列表" name="list">
          <el-input v-model="searchCardNo" placeholder="搜索借书证号" class="search-input" @keyup.enter="loadCards" />
          <el-table :data="cards" border>
            <el-table-column prop="cardNo" label="借书证号" />
            <el-table-column prop="studentId" label="学生ID" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <span :class="getStatusClass(scope.row.status)">{{ getStatusText(scope.row.status) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="issueDate" label="发证日期" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button v-if="scope.row.status === 'NORMAL'" type="text" @click="handleLost(scope.row.cardNo)">挂失</el-button>
                <el-button v-if="scope.row.status === 'LOST'" type="text" @click="handleReissue(scope.row.cardNo)">补办</el-button>
                <el-button v-if="scope.row.status !== 'CANCELLED'" type="text" @click="handleCancel(scope.row.cardNo)">注销</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="新办借书证" name="create">
          <div class="create-card-form">
            <el-form-item label="学号">
              <el-input v-model="createForm.studentNo" placeholder="请输入学号" class="form-input" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleCreate">创建借书证</el-button>
            </el-form-item>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const activeTab = ref('list')
const cards = ref([])
const searchCardNo = ref('')
const createForm = reactive({
  studentNo: ''
})

const getStatusClass = (status) => {
  const classes = {
    'NORMAL': 'status-normal',
    'LOST': 'status-lost',
    'CANCELLED': 'status-cancelled'
  }
  return classes[status] || ''
}

const getStatusText = (status) => {
  const texts = {
    'NORMAL': '正常',
    'LOST': '挂失',
    'CANCELLED': '注销'
  }
  return texts[status] || status
}

const loadCards = async () => {
  try {
    const response = await request.get('/cards')
    if (response.code === 200) {
      cards.value = response.data
    }
  } catch (error) {
    console.error('获取借书证列表失败:', error)
  }
}

const handleTabChange = () => {
  if (activeTab.value === 'list') {
    loadCards()
  }
}

const handleCreate = async () => {
  try {
    const response = await request.post('/cards', createForm)
    if (response.code === 200) {
      ElMessage.success(response.message)
      createForm.studentNo = ''
      activeTab.value = 'list'
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

const handleLost = async (cardNo) => {
  try {
    const response = await request.put(`/cards/${cardNo}/lost`)
    if (response.code === 200) {
      ElMessage.success(response.message)
      loadCards()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('挂失失败')
  }
}

const handleReissue = async (cardNo) => {
  try {
    const response = await request.put(`/cards/${cardNo}/reissue`)
    if (response.code === 200) {
      ElMessage.success(response.message)
      loadCards()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('补办失败')
  }
}

const handleCancel = async (cardNo) => {
  try {
    const response = await request.put(`/cards/${cardNo}/cancel`)
    if (response.code === 200) {
      ElMessage.success(response.message)
      loadCards()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('注销失败')
  }
}

onMounted(() => {
  loadCards()
})
</script>

<style scoped>
.card-management {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.search-input {
  margin-bottom: 15px;
  width: 300px;
}

.status-normal {
  color: #13ce66;
}

.status-lost {
  color: #ff9800;
}

.status-cancelled {
  color: #909399;
}

.create-card-form {
  max-width: 400px;
  padding: 20px;
}

.form-item {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #303133;
}

.form-input {
  width: 100%;
  height: 40px;
  padding: 0 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #409eff;
}

.submit-btn {
  width: 100%;
  height: 40px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.submit-btn:hover {
  background: #66b1ff;
}
</style>
