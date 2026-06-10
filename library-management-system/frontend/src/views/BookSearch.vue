<template>
  <div class="book-search">
    <div class="search-header">
      <el-input v-model="keyword" placeholder="请输入书名、作者、ISBN或出版社" class="search-input" @keyup.enter="handleSearch" />
      <el-button type="primary" @click="handleSearch">检索</el-button>
    </div>

    <el-table :data="books" border class="book-table" v-loading="loading" @row-click="handleRowClick">
      <el-table-column prop="ISBN" label="ISBN" width="150" />
      <el-table-column prop="bname" label="书名" min-width="150" />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="publisher" label="出版社" width="150" />
      <el-table-column label="状态" width="80" align="center">
        <template #default="scope">
          <span :class="scope.row.bookStatus === 'ONSHELF' ? 'available-high' : 'available-zero'">{{ scope.row.bookStatus === 'ONSHELF' ? '可借' : '下架' }}</span>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const route = useRoute()

const keyword = ref('')
const books = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getAvailableClass = (count) => {
  if (count > 0) return 'available-high'
  return 'available-zero'
}

const handleSearch = () => {
  currentPage.value = 1
  fetchBooks()
}

const handleRowClick = (row) => {
  router.push(`/books/${row.id}`)
}

const fetchBooks = async () => {
  loading.value = true
  try {
    let response
    if (keyword.value.trim()) {
      response = await request.get('/book/search', {
        params: { keyword: keyword.value.trim() }
      })
    } else {
      response = await request.get('/book/onshelf', {
        params: { page: currentPage.value, size: pageSize.value }
      })
    }
    
    if (response.code === 200) {
      if (keyword.value.trim() && Array.isArray(response.data)) {
        books.value = response.data
        total.value = response.data.length
      } else if (response.data.records) {
        books.value = response.data.records
        total.value = response.data.total
      } else if (Array.isArray(response.data)) {
        books.value = response.data
        total.value = response.data.length
      } else {
        books.value = []
        total.value = 0
      }
    }
  } catch (error) {
    console.error('获取图书列表失败:', error)
    books.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchBooks()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchBooks()
}

onMounted(() => {
  if (route.query.keyword) {
    keyword.value = route.query.keyword
  }
  fetchBooks()
})

watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword) {
    keyword.value = newKeyword
    handleSearch()
  }
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
  cursor: pointer;
}

.book-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

.available-high {
  color: #13ce66;
  font-weight: bold;
}

.available-zero {
  color: #f56c6c;
  font-weight: bold;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
