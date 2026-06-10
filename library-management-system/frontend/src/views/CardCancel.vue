<template>
  <div class="card-cancel">
    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="学号">
          <el-input
            v-model="sno"
            ref="snoInputRef"
            placeholder="请输入学号"
            @keyup.enter="handleQuery"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <StudentInfoPanel
      v-if="studentData"
      :student="studentData"
      :card="cardData"
      :show-card-no="true"
    />

    <el-card v-if="studentData && canCancel" class="form-card">
      <el-form label-width="120px">
        <el-form-item>
          <el-button type="danger" @click="handleCancel" :loading="loading">确认注销</el-button>
          <el-button @click="handleReset">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="studentData && !canCancel" class="form-card">
      <el-result
        :icon="resultIcon"
        :title="resultTitle"
        :sub-title="resultSubTitle"
      >
        <template #extra>
          <el-button type="primary" @click="handleReset">查询其他学生</el-button>
        </template>
      </el-result>
    </el-card>

    <el-divider>批量注销模式</el-divider>

    <el-card class="batch-card">
      <template #header>
        <div class="card-header">
          <span>批量注销借书证</span>
        </div>
      </template>
      <el-form ref="batchFormRef" :model="batchForm" label-width="100px">
        <el-form-item label="学号列表">
          <el-input
            v-model="batchForm.snosText"
            type="textarea"
            :rows="6"
            placeholder="请输入学号，每行一个"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="danger" @click="handleBatchCancel" :loading="batchLoading">批量注销</el-button>
          <el-button @click="handleBatchReset">清空</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'
import StudentInfoPanel from './StudentInfoPanel.vue'

const sno = ref('')
const snoInputRef = ref(null)

const studentData = ref(null)
const cardData = ref(null)
const loading = ref(false)

const batchForm = reactive({
  snosText: ''
})
const batchFormRef = ref(null)
const batchLoading = ref(false)

const canCancel = computed(() => {
  return cardData.value && cardData.value.cardStatus !== '注销'
})

const resultIcon = computed(() => {
  if (!studentData.value) return 'info'
  if (!cardData.value) return 'warning'
  if (cardData.value.cardStatus === '注销') return 'info'
  return 'success'
})

const resultTitle = computed(() => {
  if (!studentData.value) return ''
  if (!cardData.value) return '该学生未办理借书证'
  if (cardData.value.cardStatus === '注销') return '该借书证已注销'
  return ''
})

const resultSubTitle = computed(() => {
  if (!cardData.value) return '请先办理借书证'
  return `借书证号：${cardData.value.cardNo || '-'}`
})

const handleQuery = async () => {
  if (!sno.value) {
    ElMessage.warning('请输入学号')
    return
  }

  try {
    const response = await request.get(`/card/student/${sno.value}`)
    if (response.code === 200) {
      studentData.value = response.data.student
      cardData.value = response.data.card
    } else {
      ElMessage.error(response.message || '查询失败')
      studentData.value = null
      cardData.value = null
    }
  } catch (error) {
    const errorMsg = error.response?.data?.message || '查询失败'
    ElMessage.error(errorMsg)
    studentData.value = null
    cardData.value = null
  }
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要注销该借书证吗？此操作不可逆', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  loading.value = true
  try {
    const response = await request.put(`/card/cancel/${sno.value}`)
    if (response.code === 200) {
      ElMessage.success(response.message || '注销成功')
      handleQuery()
    } else {
      ElMessage.error(response.message || '注销失败')
    }
  } catch (error) {
    const errorMsg = error.response?.data?.message || '注销失败'
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
  }
}

const handleBatchCancel = async () => {
  const snos = batchForm.snosText.split('\n').map(s => s.trim()).filter(s => s)

  if (snos.length === 0) {
    ElMessage.warning('请输入学号列表')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要注销 ${snos.length} 个借书证吗？此操作不可逆`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  batchLoading.value = true
  try {
    const response = await request.put('/card/cancel/batch', {
      snos
    })
    if (response.code === 200) {
      ElMessage.success(response.message || '批量注销成功')
      handleBatchReset()
    } else {
      ElMessage.error(response.message || '批量注销失败')
    }
  } catch (error) {
    const errorMsg = error.response?.data?.message || '批量注销失败'
    ElMessage.error(errorMsg)
  } finally {
    batchLoading.value = false
  }
}

const handleBatchReset = () => {
  batchForm.snosText = ''
}

const handleReset = () => {
  sno.value = ''
  studentData.value = null
  cardData.value = null
  snoInputRef.value?.focus()
}
</script>

<style scoped>
.card-cancel {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.search-card {
  margin-bottom: 20px;
}

.form-card {
  margin-bottom: 20px;
}

.batch-card {
  margin-top: 20px;
}

.card-header {
  font-weight: bold;
}
</style>
