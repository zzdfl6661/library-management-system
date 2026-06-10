<template>
  <div class="card-create">
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

    <el-card v-if="studentData && !hasCard" class="form-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="借书证号" prop="cardNo">
          <el-input
            v-model="form.cardNo"
            ref="cardNoInputRef"
            placeholder="请输入借书证号"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCreate" :loading="loading">确认办理</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="hasCard" class="form-card">
      <el-result
        icon="info"
        title="该学生已办理借书证"
        :sub-title="`借书证号：${cardData?.cardNo || '-'}`"
      >
        <template #extra>
          <el-button type="primary" @click="handleReset">查询其他学生</el-button>
        </template>
      </el-result>
    </el-card>

    <el-divider>批量办理模式</el-divider>

    <el-card class="batch-card">
      <template #header>
        <div class="card-header">
          <span>批量新办借书证</span>
        </div>
      </template>
      <el-form ref="batchFormRef" :model="batchForm" label-width="100px">
        <el-form-item label="学号列表">
          <el-input
            v-model="batchForm.snosText"
            type="textarea"
            :rows="4"
            placeholder="请输入学号，每行一个"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item label="借书证号列表">
          <el-input
            v-model="batchForm.cardNosText"
            type="textarea"
            :rows="4"
            placeholder="请输入借书证号，每行一个，与学号一一对应"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleBatchCreate" :loading="batchLoading">批量办理</el-button>
          <el-button @click="handleBatchReset">清空</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import StudentInfoPanel from './StudentInfoPanel.vue'

const sno = ref('')
const snoInputRef = ref(null)
const cardNoInputRef = ref(null)

const studentData = ref(null)
const cardData = ref(null)
const hasCard = ref(false)
const loading = ref(false)

const form = reactive({
  cardNo: ''
})

const rules = {
  cardNo: [{ required: true, message: '请输入借书证号', trigger: 'blur' }]
}

const batchForm = reactive({
  snosText: '',
  cardNosText: ''
})
const batchFormRef = ref(null)
const batchLoading = ref(false)

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
      hasCard.value = response.data.hasCard || false

      if (!hasCard.value && cardNoInputRef.value) {
        cardNoInputRef.value.focus()
      }
    } else {
      ElMessage.error(response.message || '查询失败')
      studentData.value = null
      cardData.value = null
      hasCard.value = false
    }
  } catch (error) {
    const errorMsg = error.response?.data?.message || '查询失败'
    ElMessage.error(errorMsg)
    studentData.value = null
    cardData.value = null
    hasCard.value = false
  }
}

const handleCreate = async () => {
  if (!form.cardNo) {
    ElMessage.warning('请输入借书证号')
    return
  }

  loading.value = true
  try {
    const response = await request.post('/card/create', {
      sno: sno.value,
      cardNo: form.cardNo
    })
    if (response.code === 200) {
      ElMessage.success(response.message || '办理成功')
      handleReset()
    } else {
      ElMessage.error(response.message || '办理失败')
    }
  } catch (error) {
    const errorMsg = error.response?.data?.message || '办理失败'
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  sno.value = ''
  studentData.value = null
  cardData.value = null
  hasCard.value = false
  form.cardNo = ''
  snoInputRef.value?.focus()
}

const handleBatchCreate = async () => {
  const snos = batchForm.snosText.split('\n').map(s => s.trim()).filter(s => s)
  const cardNos = batchForm.cardNosText.split('\n').map(s => s.trim()).filter(s => s)

  if (snos.length === 0) {
    ElMessage.warning('请输入学号列表')
    return
  }

  if (cardNos.length === 0) {
    ElMessage.warning('请输入借书证号列表')
    return
  }

  if (snos.length !== cardNos.length) {
    ElMessage.warning('学号数量和借书证号数量不一致')
    return
  }

  batchLoading.value = true
  try {
    const response = await request.post('/card/create/batch', {
      snos,
      cardNos
    })
    if (response.code === 200) {
      ElMessage.success(response.message || '批量办理成功')
      handleBatchReset()
    } else {
      ElMessage.error(response.message || '批量办理失败')
    }
  } catch (error) {
    const errorMsg = error.response?.data?.message || '批量办理失败'
    ElMessage.error(errorMsg)
  } finally {
    batchLoading.value = false
  }
}

const handleBatchReset = () => {
  batchForm.snosText = ''
  batchForm.cardNosText = ''
}
</script>

<style scoped>
.card-create {
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
