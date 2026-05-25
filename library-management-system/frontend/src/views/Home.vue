
<template>
  <div class="home-page">
    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="stat-icon book-icon">
          <i class="el-icon-book"></i>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ statistics.totalBooks }}</p>
          <p class="stat-label">馆藏总量</p>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon available-icon">
          <i class="el-icon-check"></i>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ statistics.availableBooks }}</p>
          <p class="stat-label">可借数量</p>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon borrowed-icon">
          <i class="el-icon-user"></i>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ statistics.borrowedBooks }}</p>
          <p class="stat-label">已借数量</p>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon today-icon">
          <i class="el-icon-calendar"></i>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ statistics.todayBorrowCount }}</p>
          <p class="stat-label">今日借书</p>
        </div>
      </el-card>
    </div>
    
    <div class="charts-row">
      <el-card class="chart-card">
        <template #header>
          <span>当月借书学生排行榜</span>
        </template>
        <div ref="studentChartRef" class="chart-container"></div>
      </el-card>
      <el-card class="chart-card">
        <template #header>
          <span>当月热门图书排行榜</span>
        </template>
        <div ref="bookChartRef" class="chart-container"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import request from '../utils/request'

const studentChartRef = ref(null)
const bookChartRef = ref(null)
let studentChart = null
let bookChart = null

const statistics = reactive({
  totalBooks: 0,
  availableBooks: 0,
  borrowedBooks: 0,
  todayBorrowCount: 0,
  todayReturnCount: 0,
  studentRanking: [],
  bookRanking: []
})

const initCharts = () => {
  if (studentChartRef.value) {
    studentChart = echarts.init(studentChartRef.value)
    const studentOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value'
      },
      yAxis: {
        type: 'category',
        data: statistics.studentRanking.map(item => item.name).reverse()
      },
      series: [{
        type: 'bar',
        data: statistics.studentRanking.map(item => item.count).reverse(),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }]
    }
    studentChart.setOption(studentOption)
  }

  if (bookChartRef.value) {
    bookChart = echarts.init(bookChartRef.value)
    const bookOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value'
      },
      yAxis: {
        type: 'category',
        data: statistics.bookRanking.map(item => item.name).reverse()
      },
      series: [{
        type: 'bar',
        data: statistics.bookRanking.map(item => item.count).reverse(),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#23c6c8' },
            { offset: 0.5, color: '#23c6c8' },
            { offset: 1, color: '#009688' }
          ])
        }
      }]
    }
    bookChart.setOption(bookOption)
  }
}

const loadStatistics = async () => {
  try {
    const response = await request.get('/statistics/dashboard')
    if (response.code === 200) {
      Object.assign(statistics, response.data)
      initCharts()
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    statistics.studentRanking = [
      { name: '张三', count: 8 },
      { name: '李四', count: 6 },
      { name: '王五', count: 5 },
      { name: '赵六', count: 4 },
      { name: '钱七', count: 3 }
    ]
    statistics.bookRanking = [
      { name: 'Java编程思想', count: 15 },
      { name: '算法导论', count: 12 },
      { name: '红楼梦', count: 10 },
      { name: '三国演义', count: 8 },
      { name: '深入理解计算机系统', count: 6 }
    ]
    statistics.totalBooks = 20
    statistics.availableBooks = 12
    statistics.borrowedBooks = 8
    statistics.todayBorrowCount = 5
    initCharts()
  }
}

const handleResize = () => {
  studentChart?.resize()
  bookChart?.resize()
}

onMounted(() => {
  loadStatistics()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  studentChart?.dispose()
  bookChart?.dispose()
})
</script>

<style scoped>
.home-page {
  padding: 20px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 24px;
}

.book-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.available-icon {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: white;
}

.borrowed-icon {
  background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%);
  color: white;
}

.today-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin: 0;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin: 5px 0 0 0;
}

.charts-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.chart-card {
  height: 400px;
}

.chart-container {
  width: 100%;
  height: calc(100% - 50px);
}

@media (max-width: 1200px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  .charts-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
}
</style>
