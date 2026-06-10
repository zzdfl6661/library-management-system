<template>
  <div class="home-page">
    <div class="search-header">
      <el-input v-model="keyword" placeholder="请输入书名、作者、ISBN搜索图书" class="search-input" @keyup.enter="handleSearch" />
      <el-button type="primary" @click="handleSearch">检索</el-button>
      <div class="header-right">
        <el-button v-if="!isLoggedIn" type="primary" @click="goToLogin">登录</el-button>
      </div>
    </div>

    <div class="cards-container">
      <el-card class="card-item">
        <template #header>
          <div class="card-header">
            <i class="el-icon-reading"></i>
            <span>热读推荐</span>
          </div>
        </template>
        <div v-loading="loading.hotBooks">
          <div v-if="hotBooks.length === 0 && !loading.hotBooks" class="empty-text">暂无数据</div>
          <div v-else class="book-list">
            <div v-for="book in hotBooks" :key="book.id" class="book-item" @click="goToDetail(book.id)">
              <div class="book-cover">
                <i class="el-icon-book"></i>
              </div>
              <div class="book-info">
                <p class="book-title">{{ book.name }}</p>
                <p class="book-count">借阅 {{ book.count }} 次</p>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="card-item">
        <template #header>
          <div class="card-header">
            <i class="el-icon-user"></i>
            <span>借书明星</span>
          </div>
        </template>
        <div v-loading="loading.topReaders">
          <div v-if="topReaders.length === 0 && !loading.topReaders" class="empty-text">暂无数据</div>
          <div v-else class="reader-list">
            <div v-for="(reader, index) in topReaders" :key="reader.id" class="reader-item">
              <div class="reader-rank" :class="getRankClass(index)">{{ index + 1 }}</div>
              <div class="reader-info">
                <p class="reader-name">{{ reader.name || reader.username }}</p>
                <p class="reader-count">借阅 {{ reader.count }} 次</p>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="card-item">
        <template #header>
          <div class="card-header">
            <i class="el-icon-collection-tag"></i>
            <span>新书速递</span>
          </div>
        </template>
        <div v-loading="loading.newBooks">
          <div v-if="newBooks.length === 0 && !loading.newBooks" class="empty-text">暂无数据</div>
          <div v-else class="book-list">
            <div v-for="book in newBooks" :key="book.id" class="book-item" @click="goToDetail(book.id)">
              <div class="book-cover new-book">
                <i class="el-icon-book"></i>
              </div>
              <div class="book-info">
                <p class="book-title">{{ book.bname }}</p>
                <p class="book-author">{{ book.author }}</p>
                <p class="book-date">{{ book.pubDate }}</p>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()

const keyword = ref('')
const isLoggedIn = computed(() => !!localStorage.getItem('token'))

const hotBooks = ref([])
const topReaders = ref([])
const newBooks = ref([])

const loading = reactive({
  hotBooks: false,
  topReaders: false,
  newBooks: false
})

const getRankClass = (index) => {
  if (index === 0) return 'rank-gold'
  if (index === 1) return 'rank-silver'
  if (index === 2) return 'rank-bronze'
  return ''
}

const handleSearch = () => {
  if (keyword.value.trim()) {
    router.push({ path: '/books', query: { keyword: keyword.value.trim() } })
  } else {
    router.push('/books')
  }
}

const goToLogin = () => {
  router.push('/login')
}

const goToDetail = (id) => {
  router.push(`/books/${id}`)
}

const fetchHotBooks = async () => {
  loading.hotBooks = true
  try {
    const response = await request.get('/statistics/hot-books', { params: { limit: 10 } })
    if (response.code === 200) {
      hotBooks.value = response.data || []
    }
  } catch (error) {
    console.error('获取热读推荐失败:', error)
  } finally {
    loading.hotBooks = false
  }
}

const fetchTopReaders = async () => {
  loading.topReaders = true
  try {
    const response = await request.get('/statistics/top-readers', { params: { limit: 10 } })
    if (response.code === 200) {
      topReaders.value = response.data || []
    }
  } catch (error) {
    console.error('获取借书明星失败:', error)
  } finally {
    loading.topReaders = false
  }
}

const fetchNewBooks = async () => {
  loading.newBooks = true
  try {
    const response = await request.get('/statistics/new-books', { params: { limit: 10 } })
    if (response.code === 200) {
      newBooks.value = response.data || []
    }
  } catch (error) {
    console.error('获取新书速递失败:', error)
  } finally {
    loading.newBooks = false
  }
}

onMounted(() => {
  fetchHotBooks()
  fetchTopReaders()
  fetchNewBooks()
})
</script>

<style scoped>
.home-page {
  padding: 20px;
}

.search-header {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  align-items: center;
}

.search-input {
  width: 400px;
}

.header-right {
  margin-left: auto;
}

.cards-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.card-item {
  height: auto;
  min-height: 450px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  font-size: 16px;
}

.card-header i {
  font-size: 18px;
  color: #409eff;
}

.empty-text {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

.book-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.book-item {
  display: flex;
  gap: 12px;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.book-item:hover {
  background-color: #f5f7fa;
}

.book-cover {
  width: 50px;
  height: 65px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  flex-shrink: 0;
}

.book-cover.new-book {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.book-info {
  flex: 1;
  overflow: hidden;
}

.book-title {
  margin: 0;
  font-size: 14px;
  font-weight: bold;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-author {
  margin: 4px 0 0 0;
  font-size: 12px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-count,
.book-date {
  margin: 4px 0 0 0;
  font-size: 12px;
  color: #999;
}

.reader-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reader-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border-radius: 8px;
}

.reader-rank {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  color: #666;
  flex-shrink: 0;
}

.rank-gold {
  background: linear-gradient(135deg, #f5af19 0%, #f12711 100%);
  color: white;
}

.rank-silver {
  background: linear-gradient(135deg, #e6e9f0 0%, #eef2f3 100%);
  color: #666;
}

.rank-bronze {
  background: linear-gradient(135deg, #d7b08c 0%, #a67c52 100%);
  color: white;
}

.reader-info {
  flex: 1;
}

.reader-name {
  margin: 0;
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.reader-count {
  margin: 4px 0 0 0;
  font-size: 12px;
  color: #999;
}

@media (max-width: 1200px) {
  .cards-container {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .cards-container {
    grid-template-columns: 1fr;
  }

  .search-input {
    width: 100%;
  }
}
</style>
