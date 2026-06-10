
<template>
  <div class="card-management">
    <div class="tabs">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="借书证列表" name="list">
          <el-input v-model="searchCardNo" placeholder="搜索借书证号" class="search-input" @input="handleSearch" />
          <el-table :data="filteredCards" border>
            <el-table-column prop="cardNo" label="借书证号" />
            <el-table-column prop="sno" label="学号" width="100" />
            <el-table-column prop="sname" label="姓名" width="100" />
            <el-table-column prop="cardStatus" label="状态">
              <template #default="scope">
                <span :class="getStatusClass(scope.row.cardStatus)">{{ getStatusText(scope.row.cardStatus) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button v-if="scope.row.cardStatus === '正常'" type="text" @click="handleLost(scope.row.cardNo)">挂失</el-button>
                <el-button v-if="scope.row.cardStatus === '挂失'" type="text" @click="handleReissue(scope.row.cardNo)">补办</el-button>
                <el-button v-if="scope.row.cardStatus !== '注销'" type="text" @click="handleCancel(scope.row.cardNo)">注销</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="新办借书证" name="create">
          <el-form ref="createForm" :model="createForm" label-width="120px" class="create-form">
            <el-form-item label="学号">
              <el-input v-model="studentNo" placeholder="请输入学号" style="width: 200px;" />
              <el-button type="default" @click="handleSearchStudent" style="margin-left: 10px;">查询</el-button>
            </el-form-item>
            <div v-if="studentInfo" class="student-info">
              <el-card title="学生基本信息" style="margin-bottom: 15px;">
                <div class="info-row">
                  <span class="label">姓名：</span>
                  <span class="value">{{ studentInfo.username }}</span>
                </div>
                <div class="info-row">
                  <span class="label">学号：</span>
                  <span class="value">{{ studentInfo.sno }}</span>
                </div>
              </el-card>
            </div>
            <el-form-item>
              <el-button type="primary" @click="handleCreate" :disabled="!studentInfo">创建借书证</el-button>
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
const studentInfo = ref(null)

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
    '正常': 'status-normal',
    '挂失': 'status-lost',
    '注销': 'status-cancelled'
  }
  return classes[status] || ''
}

const getStatusText = (status) => {
  return status || '未知'
}

const loadCards = async () => {
  try {
    const response = await request.get('/card/list')
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

const handleSearchStudent = async () => {
  if (!studentNo.value) {
    ElMessage.warning('请输入学号')
    return
  }
  try {
    const response = await request.get(`/student/${studentNo.value}`)
    if (response.code === 200) {
      studentInfo.value = response.data
    } else {
      ElMessage.error(response.message || '查询失败')
      studentInfo.value = null
    }
  } catch (error) {
    const errorMsg = error.response?.data?.message || '查询失败'
    ElMessage.error(errorMsg)
    studentInfo.value = null
  }
}

const handleCreate = async () => {
  if (!studentNo.value) {
    ElMessage.warning('请输入学号')
    return
  }
  try {
    const cardNo = prompt('请输入新借书证号：')
    if (!cardNo) return
    const response = await request.post('/card/create', { sno: studentNo.value, cardNo })
    if (response.code === 200) {
      ElMessage.success(response.message)
      studentNo.value = ''
      studentInfo.value = null
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
  // Need sno to report lost - find it from the card list
  const card = cards.value.find(c => c.cardNo === cardNo)
  if (!card) {
    ElMessage.error('未找到该借书证信息')
    return
  }
  try {
    const response = await request.put(`/card/report-lost/${card.sno}`)
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

const handleReissue = async (oldCardNo) => {
  const card = cards.value.find(c => c.cardNo === oldCardNo)
  if (!card) {
    ElMessage.error('未找到该借书证信息')
    return
  }
  const newCardNo = prompt('请输入新借书证号：')
  if (!newCardNo) return
  try {
    const response = await request.put('/card/reissue', { sno: card.sno, newCardNo })
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
  const card = cards.value.find(c => c.cardNo === cardNo)
  if (!card) {
    ElMessage.error('未找到该借书证信息')
    return
  }
  try {
    const response = await request.put(`/card/cancel/${card.sno}`)
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

.student-info {
  margin-top: 15px;
}

.info-row {
  display: flex;
  margin-bottom: 10px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row .label {
  font-weight: bold;
  color: #606266;
  min-width: 60px;
}

.info-row .value {
  color: #303133;
}
</style>
