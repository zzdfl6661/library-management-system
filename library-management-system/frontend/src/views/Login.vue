<template>
  <div class="login-container">
    <div class="login-box">
      <h2>图书馆管理系统</h2>
      <el-tabs v-model="loginType" class="login-tabs">
        <el-tab-pane label="管理员登录" name="admin"></el-tab-pane>
        <el-tab-pane label="读者登录" name="reader"></el-tab-pane>
      </el-tabs>
      
      <el-form ref="loginFormRef" :model="form" label-width="80px" class="login-form">
        <template v-if="loginType === 'admin'">
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              autocomplete="username"
              clearable
            />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              autocomplete="current-password"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
        </template>
        
        <template v-else>
          <el-form-item label="借书证号" prop="cardNo">
            <el-input
              v-model="form.cardNo"
              placeholder="请输入借书证号"
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
        </template>
        
        <el-form-item>
          <el-button type="primary" @click="handleLogin" class="login-btn" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
      
      <div class="tips">
        <p>管理员账号：office / circulation / acquisition</p>
        <p>读者登录：使用借书证号和密码</p>
        <p>测试账号：借书证号 C001 / C002 / C003</p>
        <p>密码统一为：123456</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)
const loginType = ref('admin')

const form = reactive({
  username: '',
  cardNo: '',
  password: ''
})

const handleLogin = async () => {
  if (loginType.value === 'admin') {
    if (!form.username || !form.password) {
      ElMessage.warning('请输入用户名和密码')
      return
    }
  } else {
    if (!form.cardNo || !form.password) {
      ElMessage.warning('请输入借书证号和密码')
      return
    }
  }

  loading.value = true
  try {
    let response
    if (loginType.value === 'admin') {
      response = await request.post('/auth/login', {
        username: form.username,
        password: form.password
      })
    } else {
      response = await request.post('/auth/reader-login', {
        cardNo: form.cardNo,
        password: form.password
      })
    }
    
    if (response.code === 200) {
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('username', response.data.username || response.data.name)
      localStorage.setItem('role', response.data.role)
      localStorage.setItem('userId', response.data.id)
      if (response.data.role === 'STUDENT') {
        localStorage.setItem('studentNo', response.data.studentNo)
        localStorage.setItem('cardNo', response.data.cardNo)
      } else if (response.data.studentNo) {
        localStorage.setItem('studentNo', response.data.studentNo)
      }
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(response.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.response?.data?.message || '登录失败，请检查网络或账号密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  z-index: 1;
}

.login-box {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  width: 400px;
  max-width: 90%;
  position: relative;
  z-index: 2;
}

.login-box h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.login-tabs {
  margin-bottom: 20px;
}

.login-form {
  margin-top: 10px;
}

.login-form :deep(.el-input) {
  width: 100%;
}

.login-form :deep(.el-input__wrapper) {
  padding: 4px 11px;
}

.login-btn {
  width: 100%;
  height: 40px;
}

.tips {
  margin-top: 20px;
  text-align: center;
  color: #999;
  font-size: 12px;
}

.tips p {
  margin: 5px 0;
}
</style>
