<template>
  <div class="dashboard">
    <aside class="sidebar">
      <div class="logo">
        <h2>图书馆管理系统</h2>
      </div>
      <el-menu :default-active="activeMenu" class="sidebar-menu" mode="inline" @select="handleMenuSelect">
        <el-menu-item index="/">
          <i class="el-icon-home"></i>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/books">
          <i class="el-icon-search"></i>
          <span>图书检索</span>
        </el-menu-item>
        <el-menu-item v-if="role === 'OFFICE'" index="/cards">
          <i class="el-icon-user"></i>
          <span>借书证管理</span>
        </el-menu-item>
        <el-menu-item v-if="role === 'CIRCULATION'" index="/borrow">
          <i class="el-icon-plus"></i>
          <span>借书管理</span>
        </el-menu-item>
        <el-menu-item v-if="role === 'CIRCULATION'" index="/return">
          <i class="el-icon-minus"></i>
          <span>还书管理</span>
        </el-menu-item>
        <el-menu-item v-if="role === 'ACQUISITION'" index="/acquisition">
          <i class="el-icon-folder-add"></i>
          <span>新书入库</span>
        </el-menu-item>
        <el-menu-item index="/myborrows">
          <i class="el-icon-document"></i>
          <span>我的借阅</span>
        </el-menu-item>
        <el-menu-item index="/myfines">
          <i class="el-icon-money"></i>
          <span>我的罚款</span>
        </el-menu-item>
      </el-menu>
      <div class="logout" @click="handleLogout">
        <i class="el-icon-log-out"></i>
        <span>退出登录</span>
      </div>
    </aside>
    <main class="main-content">
      <header class="header">
        <div class="user-info">
          <span>欢迎, {{ username }}</span>
          <span class="role">{{ roleName }}</span>
        </div>
      </header>
      <div class="content">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const username = ref('')
const role = ref('')
const activeMenu = ref('/')

const roleName = computed(() => {
  const roles = {
    'OFFICE': '办公室',
    'CIRCULATION': '流通部',
    'ACQUISITION': '采编部',
    'STUDENT': '读者'
  }
  return roles[role.value] || ''
})

onMounted(() => {
  username.value = localStorage.getItem('username') || ''
  role.value = localStorage.getItem('role') || ''
  activeMenu.value = route.path || '/'
})

const handleMenuSelect = (index) => {
  router.push(index)
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  localStorage.removeItem('userId')
  ElMessage.success('退出成功')
  router.push('/login')
}
</script>

<style scoped>
.dashboard {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 220px;
  background: #2f4050;
  color: white;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 20px;
  background: #1f2d3d;
  text-align: center;
}

.logo h2 {
  font-size: 16px;
  margin: 0;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
}

.sidebar-menu .el-menu-item {
  color: #a7b1c2;
  height: 50px;
  line-height: 50px;
}

.sidebar-menu .el-menu-item:hover {
  background: #1f2d3d;
}

.sidebar-menu .el-menu-item.is-active {
  background: #1ab394;
  color: white;
}

.logout {
  padding: 15px 20px;
  background: #1f2d3d;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.logout:hover {
  background: #16202a;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f3f3f4;
}

.header {
  height: 60px;
  background: white;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.role {
  background: #1ab394;
  color: white;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style>
