<template>
  <div class="card-reissue">
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
      :editable="canReissue"
      @update:cardNo="handleCardNoUpdate"
    />

    <el-card v-if="studentData && canReissue" class="form-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="新借书证号" prop="newCardNo">
          <el-input
            v-model="form.newCardNo"
            placeholder="请输入新借书证号"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleReissue" :loading="loading">确认补办</el-button>
          <el-button @click="handleResetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="studentData && !canReissue" class="form-card">
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
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import StudentInfoPanel from './StudentInfoPanel.vue'

const sno = ref('')
const snoInputRef = ref(null)

const studentData = ref(null)
const cardData = ref(null)
const loading = ref(false)

const form = reactive({
  newCardNo: ''
})

const rules = {
  newCardNo: [{ required: true, message: '请输入新借书证号', trigger: 'blur' }]
}

const canReissue = computed(() => {
  return cardData.value && cardData.value.cardStatus === '挂失'
})

const resultIcon = computed(() => {
  if (!studentData.value) return 'info'
  if (!cardData.value) return 'warning'
  if (cardData.value.cardStatus === '正常') return 'success'
  if (cardData.value.cardStatus === '注销') return 'info'
  return 'warning'
})

const resultTitle = computed(() => {
  if (!studentData.value) return ''
  if (!cardData.value) return '该学生未办理借书证'
  if (cardData.value.cardStatus === '正常') return '该借书证状态正常'
  if (cardData.value.cardStatus === '注销') return '该借书证已注销'
  return ''
})

const resultSubTitle = computed(() => {
  if (!cardData.value) return '请先办理借书证'
  return `借书证号：${cardData.value.cardNo || '-'}`
})

const handleCardNoUpdate = (val) => {
  form.newCardNo = val
}

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

const handleReissue = async () => {
  if (!form.newCardNo) {
    ElMessage.warning('请输入新借书证号')
    return
  }

  loading.value = true
  try {
    const response = await request.put('/card/reissue', {
      sno: sno.value,
      newCardNo: form.newCardNo
    })
    if (response.code === 200) {
      ElMessage.success(response.message || '补办成功')
      handleReset()
    } else {
      ElMessage.error(response.message || '补办失败')
    }
  } catch (error) {
    const errorMsg = error.response?.data?.message || '补办失败'
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
  }
}

const handleResetForm = () => {
  form.newCardNo = ''
}

const handleReset = () => {
  sno.value = ''
  studentData.value = null
  cardData.value = null
  form.newCardNo = ''
  snoInputRef.value?.focus()
}
</script>

<style scoped>
.card-reissue {
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
</style>
