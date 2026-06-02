
<template>
  <div class="book-search">
    <div class="search-header">
      <el-input v-model="searchForm.keyword" placeholder="请输入书名、作者、ISBN或出版社" class="search-input" @keyup.enter="handleSearch" />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>
    <el-table :data="books" border class="book-table">
      <el-table-column prop="id" label="图书ID" width="100" />
      <el-table-column prop="title" label="书名" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="publisher" label="出版社" />
      <el-table-column prop="isbn" label="ISBN" />
      <el-table-column prop="copyCount" label="副本数" width="100" />
      <el-table-column prop="availableCount" label="可借数" width="100" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button type="text" @click="viewDetail(scope.row.id)">查看详情</el-button>
          <el-button v-if="role === 'STUDENT'" type="primary" size="small" @click="showBorrowDialog(scope.row)">借阅</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog title="借阅图书" :visible.sync="borrowDialogVisible" width="400px">
      <el-form :model="borrowForm" label-width="100px">
        <el-form-item label="借书证号">
          <el-input v-model="borrowForm.cardNo" placeholder="请输入借书证号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="borrowDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBorrow">确认借阅</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const books = ref([])
const searchForm = reactive({
  keyword: ''
})
const role = ref(localStorage.getItem('role') || '')
const borrowDialogVisible = ref(false)
const borrowForm = reactive({
  cardNo: ''
})
const currentBook = ref(null)

const handleSearch = async () => {
  try {
    const response = await request.get('/books/search', {
      params: {
        keyword: searchForm.keyword
      }
    })
    if (response.code === 200) {
      books.value = response.data
    }
  } catch (error) {
    console.error('搜索失败:', error)
  }
}

const viewDetail = (id) => {
  router.push(`/books/${id}`)
}

const showBorrowDialog = (book) => {
  if (book.availableCount <= 0) {
    ElMessage.warning('该书暂无可借副本')
    return
  }
  currentBook.value = book
  borrowForm.cardNo = ''
  borrowDialogVisible.value = true
}

const confirmBorrow = async () => {
  if (!borrowForm.cardNo || !borrowForm.cardNo.trim()) {
    ElMessage.warning('请输入借书证号')
    return
  }
  if (!currentBook.value) {
    ElMessage.error('请选择图书')
    return
  }
  try {
    const response = await request.post('/borrow/card', {
      bookId: currentBook.value.id,
      cardNo: borrowForm.cardNo.trim()
    })
    if (response.code === 200) {
      ElMessage.success(response.message)
      borrowDialogVisible.value = false
      handleSearch()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    const errorMsg = error.response?.data?.message || '借阅失败'
    ElMessage.error(errorMsg)
  }
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.book-search {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.search-header {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  max-width: 500px;
}

.book-table {
  margin-top: 10px;
}
</style>
