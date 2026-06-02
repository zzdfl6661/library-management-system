
<template>
  <div class="card-management">
    <div class="tabs">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="借书证列表" name="list">
          <el-input v-model="searchCardNo" placeholder="搜索借书证号" class="search-input" @input="handleSearch" />
          <el-table :data="filteredCards" border>
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
          <el-form ref="createForm" :model="createForm" label-width="120px" class="create-form">
            <el-form-item label="学号">
              <el-input v-model="studentNo" placeholder="请输入学号" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleCreate">创建借书证</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const activeTab = ref('list')
const cards = ref([])
const searchCardNo = ref('')
const studentNo = ref('')

const filteredCards = computed(() => {
  if (!searchCardNo.value || !searchCardNo.value.trim()) {
    return cards.value
  }
  return cards.value.filter(card => 
    card.cardNo && card.cardNo.includes(searchCardNo.value.trim())
  )
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
    ElMessage.error('获取借书证列表失败')
  }
}

const handleSearch = () => {
}

const handleTabChange = () => {
  if (activeTab.value === 'list') {
    loadCards()
  }
}

const handleCreate = async () => {
  if (!studentNo.value) {
    ElMessage.warning('请输入学号')
    return
  }
  try {
    const response = await request.post('/cards', { studentNo: studentNo.value })
    if (response.code === 200) {
      ElMessage.success(response.message)
      studentNo.value = ''
      activeTab.value = 'list'
    } else {
      ElMessage.error(response.message || '创建失败')
    }
  } catch (error) {
    const errorMsg = error.response?.data?.message || '创建失败'
    ElMessage.error(errorMsg)
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

.create-form {
  max-width: 400px;
}
</style>
