<template>
  <div class="reader-login-container">
    <div class="reader-login-box">
      <h2>读者登录</h2>
      <el-form ref="loginFormRef" :model="form" :rules="rules" label-width="100px" class="login-form">
        <el-form-item label="借书证号" prop="cardNo">
          <el-input
            v-model="form.cardNo"
            placeholder="请输入借书证号"
            clearable
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
        <el-form-item>
          <el-button type="primary" @click="handleLogin" class="login-btn" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="tips">
        <p>测试账号：借书证号 C001 / C002 / C003，密码 123456</p>
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

const form = reactive({
  cardNo: '',
  password: ''
})

const rules = {
  cardNo: [{ required: true, message: '请输入借书证号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!form.cardNo || !form.password) {
    ElMessage.warning('请输入借书证号和密码')
    return
  }

  loading.value = true
  try {
    const response = await request.post('/auth/reader-login', form)
    if (response.code === 200) {
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('cardNo', response.data.cardNo)
      localStorage.setItem('studentNo', response.data.studentNo)
      localStorage.setItem('role', 'STUDENT')
      ElMessage.success('登录成功')
      router.push('/myborrows')
    } else {
      ElMessage.error(response.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请检查借书证号和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.reader-login-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  z-index: 1;
}

.reader-login-box {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  width: 400px;
  max-width: 90%;
  position: relative;
  z-index: 2;
}

.reader-login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.login-form {
  margin-top: 20px;
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
