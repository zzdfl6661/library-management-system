<template>
  <div class="borrow-page">
    <el-card class="borrow-card">
      <template #header>
        <span>借书操作</span>
      </template>
      
      <el-steps :active="currentStep" finish-status="success" class="borrow-steps">
        <el-step title="验证借书证" />
        <el-step title="添加借书清单" />
        <el-step title="完成借书" />
      </el-steps>

      <div class="step-content">
        <!-- 步骤1: 验证借书证 -->
        <div v-show="currentStep === 0" class="step-1">
          <el-form label-width="120px">
            <el-form-item label="借书证号">
              <el-input 
                v-model="cardNo" 
                placeholder="请扫描或输入借书证号" 
                @keyup.enter="checkCard"
                clearable
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="checkCard" :loading="checking">检查</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 步骤2: 添加借书清单 -->
        <div v-show="currentStep === 1" class="step-2">
          <el-alert
            v-if="borrowList.length >= availableCount"
            title="已达到借书上限"
            type="warning"
            :closable="false"
            show-icon
            class="mb-20"
          />
          
          <div class="reader-info">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="读者姓名">{{ readerInfo.name }}</el-descriptions-item>
              <el-descriptions-item label="学号">{{ readerInfo.sno }}</el-descriptions-item>
              <el-descriptions-item label="可借数量">{{ availableCount - borrowList.length }} / {{ availableCount }}</el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="add-book-form">
            <el-input 
              v-model="barCode" 
              placeholder="请扫描或输入图书条码" 
              @keyup.enter="addBook"
              clearable
              class="barcode-input"
            >
              <template #append>
                <el-button @click="addBook" :disabled="!barCode">添加</el-button>
              </template>
            </el-input>
          </div>

          <el-table :data="borrowList" border class="borrow-table" v-if="borrowList.length > 0">
            <el-table-column prop="barCode" label="条码号" width="180" />
            <el-table-column prop="isbn" label="ISBN" width="150" />
            <el-table-column prop="bname" label="书名" min-width="150" />
            <el-table-column prop="author" label="作者" width="100" />
            <el-table-column prop="publisher" label="出版社" width="120" />
            <el-table-column prop="place" label="藏书位置" width="100" />
            <el-table-column label="操作" width="80" fixed="right">
              <template #default="scope">
                <el-button type="danger" size="small" @click="removeBook(scope.$index)">移除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div v-else class="empty-tip">
            <el-empty description="暂无添加的图书" />
          </div>
        </div>

        <!-- 步骤3: 完成借书 -->
        <div v-show="currentStep === 2" class="step-3">
          <el-alert
            title="确认借书信息"
            type="info"
            :closable="false"
            show-icon
            class="mb-20"
          />
          
          <el-descriptions :column="2" border class="confirm-info">
            <el-descriptions-item label="借书证号">{{ cardNo }}</el-descriptions-item>
            <el-descriptions-item label="读者姓名">{{ readerInfo.name }}</el-descriptions-item>
            <el-descriptions-item label="学号">{{ readerInfo.sno }}</el-descriptions-item>
            <el-descriptions-item label="借书数量">{{ borrowList.length }} 本</el-descriptions-item>
          </el-descriptions>

          <el-table :data="borrowList" border class="borrow-table mt-20">
            <el-table-column prop="barCode" label="条码号" width="180" />
            <el-table-column prop="bname" label="书名" min-width="150" />
            <el-table-column prop="author" label="作者" width="100" />
          </el-table>
        </div>
      </div>

      <div class="step-actions">
        <el-button @click="prevStep" v-if="currentStep > 0">上一步</el-button>
        <el-button type="primary" @click="nextStep" v-if="currentStep < 2" :disabled="!canNext">
          {{ currentStep === 1 ? '下一步' : '下一步' }}
        </el-button>
        <el-button type="success" @click="confirmBorrow" v-if="currentStep === 2" :loading="borrowing">
          确认借书
        </el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const currentStep = ref(0)
const cardNo = ref('')
const barCode = ref('')
const checking = ref(false)
const borrowing = ref(false)

const readerInfo = ref({
  name: '',
  sno: ''
})
const availableCount = ref(0)
const borrowList = ref([])

// 检查借书证
const checkCard = async () => {
  if (!cardNo.value) {
    ElMessage.warning('请输入借书证号')
    return
  }
  
  checking.value = true
  try {
    const response = await request.post('/borrow/card-check', { cardNo: cardNo.value })
    if (response.code === 200) {
      const data = response.data
      if (data.canBorrow) {
        readerInfo.value = {
          name: data.name || '读者',
          sno: data.sno || cardNo.value
        }
        availableCount.value = data.availableCount || 5
        currentStep.value = 1
      } else {
        ElMessageBox.alert(data.reason || '该借书证不可用', '不能借书', {
          confirmButtonText: '确定',
          type: 'warning'
        })
      }
    } else {
      ElMessage.error(response.message || '验证失败')
    }
  } catch (error) {
    ElMessage.error('验证借书证失败')
  } finally {
    checking.value = false
  }
}

// 添加图书到借书清单
const addBook = async () => {
  if (!barCode.value) return
  
  if (borrowList.value.length >= availableCount.value) {
    ElMessage.warning('已达到借书上限')
    return
  }

  // 检查是否已添加
  if (borrowList.value.some(item => item.barCode === barCode.value)) {
    ElMessage.warning('该图书已在借书清单中')
    return
  }

  try {
    const response = await request.get(`/borrow/check/${barCode.value}`)
    if (response.code === 200) {
      const data = response.data
      borrowList.value.push({
        barCode: barCode.value,
        isbn: data.isbn || '',
        bname: data.bname || data.title || '未知书名',
        author: data.author || '未知作者',
        publisher: data.publisher || '',
        place: data.place || ''
      })
      barCode.value = ''
    } else {
      ElMessage.error(response.message || '该图书不可借')
    }
  } catch (error) {
    ElMessage.error('添加图书失败')
  }
}

// 移除图书
const removeBook = (index) => {
  borrowList.value.splice(index, 1)
}

// 能否进行下一步
const canNext = computed(() => {
  if (currentStep.value === 0) {
    return !!cardNo.value
  }
  if (currentStep.value === 1) {
    return borrowList.value.length > 0
  }
  return true
})

// 上一步
const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

// 下一步
const nextStep = () => {
  if (currentStep.value === 1 && borrowList.value.length === 0) {
    ElMessage.warning('请添加至少一本图书')
    return
  }
  if (currentStep.value < 2) {
    currentStep.value++
  }
}

// 确认借书
const confirmBorrow = async () => {
  if (borrowList.value.length === 0) {
    ElMessage.warning('借书清单为空')
    return
  }

  borrowing.value = true
  try {
    const response = await request.post('/borrow/borrow', {
      cardNo: cardNo.value,
      barCodes: borrowList.value.map(item => item.barCode)
    })
    if (response.code === 200) {
      ElMessage.success('借书成功')
      reset()
    } else {
      ElMessage.error(response.message || '借书失败')
    }
  } catch (error) {
    ElMessage.error('借书失败')
  } finally {
    borrowing.value = false
  }
}

// 重置
const reset = () => {
  currentStep.value = 0
  cardNo.value = ''
  barCode.value = ''
  readerInfo.value = { name: '', sno: '' }
  availableCount.value = 0
  borrowList.value = []
}
</script>

<style scoped>
.borrow-page {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.borrow-card {
  max-width: 900px;
}

.borrow-steps {
  margin-bottom: 30px;
}

.step-content {
  min-height: 300px;
  padding: 20px 0;
}

.step-1 {
  max-width: 400px;
}

.reader-info {
  margin-bottom: 20px;
}

.add-book-form {
  margin-bottom: 20px;
}

.barcode-input {
  max-width: 400px;
}

.borrow-table {
  margin-top: 20px;
}

.empty-tip {
  margin-top: 40px;
}

.confirm-info {
  margin-top: 10px;
}

.mb-20 {
  margin-bottom: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.step-actions {
  margin-top: 30px;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>
