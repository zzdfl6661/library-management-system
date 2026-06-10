<template>
  <div class="student-info-panel">
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <span>学生基本信息</span>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ student?.username || '-' }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ student?.sno || '-' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ student?.gender || '-' }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ student?.className || '-' }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ student?.major || '-' }}</el-descriptions-item>
        <el-descriptions-item label="籍贯">{{ student?.originPlace || '-' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ student?.idCard || '-' }}</el-descriptions-item>
        <el-descriptions-item label="是否已办理">
          <el-tag :type="cardStatus === '正常' ? 'success' : 'info'" size="small">
            {{ cardStatus === '正常' ? '已办理' : '未办理' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="showCardNo" label="借书证号">
          <span v-if="card?.cardNo">{{ card.cardNo }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item v-if="showCardNo" label="证件状态">
          <el-tag :type="getStatusType(cardStatus)" size="small">
            {{ getStatusText(cardStatus) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <div v-if="editable && showCardNo" class="card-input">
        <el-form-item label="新借书证号">
          <el-input v-model="localCardNo" placeholder="请输入新借书证号" :disabled="!editable" />
        </el-form-item>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  student: Object,
  card: Object,
  showCardNo: {
    type: Boolean,
    default: false
  },
  editable: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:cardNo'])

const localCardNo = ref('')

const cardStatus = ref('')

watch(() => props.card, (newCard) => {
  if (newCard) {
    cardStatus.value = newCard.cardStatus || '正常'
  } else {
    cardStatus.value = ''
  }
}, { immediate: true })

watch(localCardNo, (val) => {
  emit('update:cardNo', val)
})

const getStatusType = (status) => {
  const types = {
    '正常': 'success',
    '挂失': 'warning',
    '注销': 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    '正常': '正常',
    '挂失': '挂失',
    '注销': '已注销'
  }
  return texts[status] || status
}
</script>

<style scoped>
.student-info-panel {
  margin-bottom: 20px;
}

.info-card {
  margin-bottom: 0;
}

.card-header {
  font-weight: bold;
}

.card-input {
  margin-top: 20px;
  max-width: 400px;
}
</style>
