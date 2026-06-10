<template>
  <div class="acquisition-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>图书管理</span>
          <el-button type="primary" @click="openAddDialog">新增图书</el-button>
        </div>
      </template>

      <!-- 查询区域 -->
      <div class="search-area">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入ISBN、书名或作者进行搜索"
          @keyup.enter="searchBooks"
          clearable
          class="search-input"
        >
          <template #append>
            <el-button @click="searchBooks">查询</el-button>
          </template>
        </el-input>
      </div>

      <!-- 图书列表 -->
      <el-table
        :data="bookList"
        border
        v-loading="loading"
        class="book-table"
      >
        <el-table-column prop="ISBN" label="ISBN" width="150" />
        <el-table-column prop="bname" label="书名" min-width="150" />
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column prop="publisher" label="出版社" width="120" />
        <el-table-column prop="pubDate" label="出版日期" width="100" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="scope">
            {{ scope.row.bookStatus === 'ONSHELF' ? '上架' : '下架' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              @click="handleOffshelf(scope.row)"
              :disabled="scope.row.bookStatus === 'OFFSHELF'"
            >
              下架
            </el-button>
            <el-button
              size="small"
              type="warning"
              @click="handleOnshelf(scope.row)"
              :disabled="scope.row.bookStatus !== 'OFFSHELF'"
            >
              上架
            </el-button>
            <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>

    <!-- 新增/编辑图书弹窗 -->
    <el-dialog
      v-model="bookDialogVisible"
      :title="isEdit ? '编辑图书' : '新增图书'"
      width="600px"
      @close="resetBookForm"
    >
      <el-form :model="bookForm" label-width="120px" ref="bookFormRef" :rules="bookRules">
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="bookForm.isbn" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="书名" prop="bname">
          <el-input v-model="bookForm.bname" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="bookForm.publisher" />
        </el-form-item>
        <el-form-item label="简介" prop="summary">
          <el-input v-model="bookForm.summary" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker
            v-model="bookForm.publishDate"
            type="date"
            placeholder="选择出版日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="中图分类号" prop="clc">
          <el-input v-model="bookForm.clc" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bookDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBook" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 副本管理弹窗 -->
    <el-dialog
      v-model="copiesDialogVisible"
      title="副本管理"
      width="700px"
      @close="resetCopiesForm"
    >
      <div class="book-basic-info" v-if="currentBook">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="ISBN">{{ currentBook.isbn }}</el-descriptions-item>
          <el-descriptions-item label="书名">{{ currentBook.bname }}</el-descriptions-item>
          <el-descriptions-item label="作者">{{ currentBook.author }}</el-descriptions-item>
          <el-descriptions-item label="出版社">{{ currentBook.publisher }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <div class="add-copy-form">
        <el-input
          v-model="copyForm.barCode"
          placeholder="请输入条码号"
          clearable
          class="copy-barcode-input"
        />
        <el-input
          v-model="copyForm.place"
          placeholder="请输入藏书位置"
          clearable
          class="copy-place-input"
        />
        <el-button type="primary" @click="addCopy" :disabled="!copyForm.barCode || !copyForm.place">新增副本</el-button>
      </div>

      <el-table
        :data="copiesList"
        border
        v-loading="copiesLoading"
        class="copies-table"
      >
        <el-table-column prop="barCode" label="条码号" width="180" />
        <el-table-column prop="isbn" label="ISBN" width="150" />
        <el-table-column prop="place" label="藏书位置" width="120" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'AVAILABLE' ? 'success' : 'info'">
              {{ scope.row.status === 'AVAILABLE' ? '可用' : '已借出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button 
              size="small" 
              type="danger" 
              @click="cancelCopy(scope.row)"
              :disabled="scope.row.status !== 'AVAILABLE'"
            >
              注销
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

// 搜索相关
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const bookList = ref([])

// 图书表单弹窗
const bookDialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const bookFormRef = ref(null)
const bookForm = reactive({
  id: null,
  isbn: '',
  bname: '',
  author: '',
  publisher: '',
  summary: '',
  publishDate: '',
  clc: ''
})

const bookRules = {
  isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
  bname: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  publisher: [{ required: true, message: '请输入出版社', trigger: 'blur' }]
}

// 副本弹窗
const copiesDialogVisible = ref(false)
const copiesLoading = ref(false)
const currentBook = ref(null)
const copiesList = ref([])
const copyForm = reactive({
  barCode: '',
  place: ''
})

// 初始化
onMounted(() => {
  searchBooks()
})

// 搜索图书
const searchBooks = async () => {
  loading.value = true
  try {
    const response = await request.get('/book/search', {
      params: {
        keyword: searchKeyword.value,
        page: currentPage.value,
        size: pageSize.value
      }
    })
    if (response.code === 200) {
      bookList.value = response.data.records || response.data || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

// 分页
const handleSizeChange = () => {
  currentPage.value = 1
  searchBooks()
}

const handleCurrentChange = () => {
  searchBooks()
}

// 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  bookDialogVisible.value = true
}

// 打开编辑弹窗
const openEditDialog = (row) => {
  isEdit.value = true
  bookForm.id = row.id
  bookForm.isbn = row.isbn
  bookForm.bname = row.bname
  bookForm.author = row.author
  bookForm.publisher = row.publisher
  bookForm.summary = row.summary || ''
  bookForm.publishDate = row.publishDate || ''
  bookForm.clc = row.clc || ''
  bookDialogVisible.value = true
}

// 重置图书表单
const resetBookForm = () => {
  bookForm.id = null
  bookForm.isbn = ''
  bookForm.bname = ''
  bookForm.author = ''
  bookForm.publisher = ''
  bookForm.summary = ''
  bookForm.publishDate = ''
  bookForm.clc = ''
  bookFormRef.value?.resetFields()
}

// 提交图书
const submitBook = async () => {
  try {
    await bookFormRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    const url = isEdit.value ? `/book/${bookForm.id}` : '/book'
    const method = isEdit.value ? 'PUT' : 'POST'
    const data = isEdit.value ? bookForm : bookForm
    
    const response = await request({
      method,
      url,
      data
    })
    
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
      bookDialogVisible.value = false
      searchBooks()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 下架
const handleOffshelf = async (row) => {
  try {
    await ElMessageBox.confirm('确定要下架该图书吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await request.put(`/book/${row.id}/offshelf`)
    if (response.code === 200) {
      ElMessage.success('下架成功')
      searchBooks()
    } else {
      ElMessage.error(response.message || '下架失败')
    }
  } catch {
    // 取消
  }
}

// 上架
const handleOnshelf = async (row) => {
  try {
    const response = await request.put(`/book/${row.id}/onshelf`)
    if (response.code === 200) {
      ElMessage.success('上架成功')
      searchBooks()
    } else {
      ElMessage.error(response.message || '上架失败')
    }
  } catch {
    // 
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该图书吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await request.delete(`/book/${row.id}`)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      searchBooks()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch {
    // 取消
  }
}

// 打开副本弹窗
const openCopiesDialog = async (row) => {
  currentBook.value = row
  copiesDialogVisible.value = true
  await loadCopies(row.isbn)
}

// 加载副本列表
const loadCopies = async (isbn) => {
  copiesLoading.value = true
  try {
    const response = await request.get(`/book/${isbn}/copies`)
    if (response.code === 200) {
      copiesList.value = response.data || []
    }
  } catch (error) {
    ElMessage.error('加载副本失败')
  } finally {
    copiesLoading.value = false
  }
}

// 重置副本表单
const resetCopiesForm = () => {
  copyForm.barCode = ''
  copyForm.place = ''
  copiesList.value = []
  currentBook.value = null
}

// 新增副本
const addCopy = async () => {
  if (!currentBook.value) return
  
  try {
    const response = await request.post(`/book/${currentBook.value.isbn}/copies`, {
      barCode: copyForm.barCode,
      place: copyForm.place
    })
    if (response.code === 200) {
      ElMessage.success('新增成功')
      await loadCopies(currentBook.value.isbn)
      copyForm.barCode = ''
      copyForm.place = ''
    } else {
      ElMessage.error(response.message || '新增失败')
    }
  } catch (error) {
    ElMessage.error('新增失败')
  }
}

// 注销副本
const cancelCopy = async (row) => {
  if (!currentBook.value) return
  
  try {
    await ElMessageBox.confirm('确定要注销该副本吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await request.put(`/book/${currentBook.value.isbn}/copies/${row.barCode}/cancel`)
    if (response.code === 200) {
      ElMessage.success('注销成功')
      await loadCopies(currentBook.value.isbn)
      searchBooks()
    } else {
      ElMessage.error(response.message || '注销失败')
    }
  } catch {
    // 取消
  }
}
</script>

<style scoped>
.acquisition-page {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-area {
  margin-bottom: 20px;
}

.search-input {
  max-width: 400px;
}

.book-table {
  margin-top: 10px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.book-basic-info {
  margin-bottom: 20px;
}

.add-copy-form {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.copy-barcode-input {
  width: 200px;
}

.copy-place-input {
  width: 150px;
}

.copies-table {
  margin-top: 10px;
}
</style>
