<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h1>创建账户</h1>
        <p>加入AI恋爱系统，开始您的智能对话之旅</p>
      </div>
      
      <a-form
        :model="registerForm"
        :rules="rules"
        @finish="handleRegister"
        layout="vertical"
        class="register-form"
      >
        <a-form-item label="用户名" name="username">
          <a-input
            v-model:value="registerForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix="h(UserOutlined)"
          />
        </a-form-item>
        
        <a-form-item label="邮箱" name="email">
          <a-input
            v-model:value="registerForm.email"
            placeholder="请输入邮箱地址"
            size="large"
            :prefix="h(MailOutlined)"
          />
        </a-form-item>
        
        <a-form-item label="昵称" name="nickname">
          <a-input
            v-model:value="registerForm.nickname"
            placeholder="请输入昵称（可选）"
            size="large"
            :prefix="h(SmileOutlined)"
          />
        </a-form-item>
        
        <a-form-item label="密码" name="password">
          <a-input-password
            v-model:value="registerForm.password"
            placeholder="请输入密码"
            size="large"
            :prefix="h(LockOutlined)"
          />
        </a-form-item>
        
        <a-form-item label="确认密码" name="confirmPassword">
          <a-input-password
            v-model:value="registerForm.confirmPassword"
            placeholder="请再次输入密码"
            size="large"
            :prefix="h(LockOutlined)"
          />
        </a-form-item>
        
        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            block
            :loading="loading"
          >
            注册
          </a-button>
        </a-form-item>
      </a-form>
      
      <div class="register-footer">
        <span>已有账户？</span>
        <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined, MailOutlined, SmileOutlined } from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'
import type { RegisterRequest } from '@/types/user'

const router = useRouter()
const userStore = useUserStore()

const registerForm = reactive<RegisterRequest>({
  username: '',
  email: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

const loading = ref(false)

const rules = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 3, max: 50, message: '用户名长度为3-50个字符' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址' },
    { type: 'email', message: '请输入有效的邮箱地址' }
  ],
  nickname: [
    { max: 50, message: '昵称长度不能超过50个字符' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, max: 100, message: '密码长度为6-100个字符' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    {
      validator: (_rule: any, value: string) => {
        if (value !== registerForm.password) {
          return Promise.reject('两次输入的密码不一致')
        }
        return Promise.resolve()
      }
    }
  ]
}

const handleRegister = async () => {
  try {
    loading.value = true
    const success = await userStore.register(registerForm)
    if (success) {
      router.push('/login')
    }
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
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 450px;
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.register-header p {
  color: #6b7280;
  font-size: 14px;
}

.register-form {
  margin-bottom: 24px;
}

.register-footer {
  text-align: center;
  color: #6b7280;
  font-size: 14px;
}

.register-footer a {
  color: #1890ff;
  text-decoration: none;
  margin-left: 4px;
}

.register-footer a:hover {
  text-decoration: underline;
}

@media (max-width: 576px) {
  .register-card {
    padding: 24px;
  }
}
</style>
