
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
          <el-button v-if="role === 'STUDENT' && scope.row.availableCount > 0" type="primary" size="small" @click="handleBorrow(scope.row)">借阅</el-button>
        </template>
      </el-table-column>
    </el-table>
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
const role = ref('')

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

const handleBorrow = async (book) => {
  const studentNo = localStorage.getItem('username')
  if (!studentNo) {
    ElMessage.error('请先登录')
    return
  }
  try {
    const response = await request.post('/borrow/student', {
      bookId: book.id,
      studentNo: studentNo
    })
    if (response.code === 200) {
      ElMessage.success(response.message)
      handleSearch()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('借阅失败')
  }
}

onMounted(() => {
  role.value = localStorage.getItem('role') || ''
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
