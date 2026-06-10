<template>
  <div class="my-profile">
    <el-card class="profile-card">
      <template #header>
        <span>个人信息</span>
      </template>
      <el-descriptions :column="2" border v-if="profile">
        <el-descriptions-item label="学号">{{ profile.sno }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ profile.sname }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ profile.sex }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ profile.class }}</el-descriptions-item>
        <el-descriptions-item label="借书证号">{{ profile.cardNo }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ profile.tel }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="password-card">
      <template #header>
        <span>修改密码</span>
      </template>
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="120px" class="password-form">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入旧密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleChangePassword" :loading="loading">修改密码</el-button>
          <el-button @click="resetPasswordForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const profile = ref(null)
const loading = ref(false)
const passwordFormRef = ref(null)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const loadProfile = async () => {
  const cardNo = localStorage.getItem('cardNo')
  const studentNo = localStorage.getItem('studentNo')
  
  if (!cardNo || !studentNo) {
    ElMessage.error('请先登录')
    return
  }

  try {
    const response = await request.get(`/card/student/${studentNo}`)
    if (response.code === 200) {
      profile.value = response.data
    }
  } catch (error) {
    console.error('获取个人信息失败:', error)
  }
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const cardNo = localStorage.getItem('cardNo')
        const response = await request.put('/card/reader/password', {
          cardNo: cardNo,
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        if (response.code === 200) {
          ElMessage.success('密码修改成功')
          resetPasswordForm()
        } else {
          ElMessage.error(response.message || '密码修改失败')
        }
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error('修改密码失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.my-profile {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.profile-card {
  margin-bottom: 20px;
}

.password-card {
  max-width: 500px;
}

.password-form :deep(.el-input) {
  width: 300px;
}
</style>
