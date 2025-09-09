<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>用户注册</h2>
        <p>加入程序员技术练兵场，开启你的技术挑战之旅</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="nickname">
          <el-input
            v-model="registerForm.nickname"
            placeholder="请输入昵称"
            size="large"
            :prefix-icon="Avatar"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="checkPassword">
          <el-input
            v-model="registerForm.checkPassword"
            type="password"
            placeholder="请确认密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="register-button"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <p>
          已有账号？
          <el-link type="primary" @click="$router.push('/login')">
            立即登录
          </el-link>
        </p>
        <el-link @click="$router.push('/')">
          返回首页
        </el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { User, Lock, Avatar } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  nickname: '',
  password: '',
  checkPassword: ''
})

// 确认密码验证函数
const validateCheckPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { max: 20, message: '昵称长度不能超过 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  checkPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateCheckPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    loading.value = true
    
    await userStore.registerUser(registerForm)
    
    // 注册成功后跳转到登录页
    router.push('/login')
    
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.register-box {
  background: white;
  border-radius: 10px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 28px;
}

.register-header p {
  color: #666;
  font-size: 14px;
  line-height: 1.4;
}

.register-form {
  margin-bottom: 20px;
}

.register-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
}

.register-footer {
  text-align: center;
}

.register-footer p {
  margin-bottom: 10px;
  color: #666;
}

@media (max-width: 480px) {
  .register-box {
    padding: 30px 20px;
  }
  
  .register-header h2 {
    font-size: 24px;
  }
}
</style>
