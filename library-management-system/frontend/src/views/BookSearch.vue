
<template>
  <div class="book-search">
    <div class="search-header">
      <el-input v-model="searchForm.keyword" placeholder="请输入书名、作者、ISBN或出版社" class="search-input" @keyup.enter="handleSearch" />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>
    <el-table :data="books" border class="book-table">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="书名" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="publisher" label="出版社" />
      <el-table-column prop="isbn" label="ISBN" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button type="text" @click="viewDetail(scope.row.id)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const books = ref([])
const searchForm = reactive({
  keyword: ''
})

const handleSearch = async () => {
  try {
    const response = await request.get('/books/search', {
      params: {
        title: searchForm.keyword,
        author: searchForm.keyword,
        isbn: searchForm.keyword,
        publisher: searchForm.keyword
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
