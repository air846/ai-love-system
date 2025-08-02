<template>
  <div class="login-container">
    <!-- 治愈系装饰元素 -->
    <div class="floating-elements">
      <div class="bubble bubble-1"></div>
      <div class="bubble bubble-2"></div>
      <div class="bubble bubble-3"></div>
      <div class="petal petal-1">🌸</div>
      <div class="petal petal-2">🌺</div>
      <div class="sparkle sparkle-1">✨</div>
      <div class="sparkle sparkle-2">⭐</div>
    </div>
    
    <div class="login-card">
      <div class="login-header">
        <h1 class="healing-title">AI恋爱系统</h1>
        <p class="healing-subtitle">欢迎回来，请登录您的账户</p>
      </div>
      
      <a-form
        :model="loginForm"
        :rules="rules"
        @finish="handleLogin"
        layout="vertical"
        class="login-form"
      >
        <a-form-item label="用户名" name="username" class="healing-form-item">
          <a-input
            v-model:value="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix="h(UserOutlined)"
            class="healing-input"
          />
        </a-form-item>
        
        <a-form-item label="密码" name="password" class="healing-form-item">
          <a-input-password
            v-model:value="loginForm.password"
            placeholder="请输入密码"
            size="large"
            :prefix="h(LockOutlined)"
            class="healing-input"
          />
        </a-form-item>
        
        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            block
            :loading="loading"
            class="healing-button"
          >
            登录
          </a-button>
        </a-form-item>
      </a-form>
      
      <div class="login-footer">
        <span>还没有账户？</span>
        <router-link to="/register" class="healing-link">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'
import type { LoginRequest } from '@/types/user'

const router = useRouter()
const userStore = useUserStore()

const loginForm = reactive<LoginRequest>({
  username: '',
  password: ''
})

const loading = ref<boolean>(false)

const rules = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 3, max: 50, message: '用户名长度为3-50个字符' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, max: 100, message: '密码长度为6-100个字符' }
  ]
}

const handleLogin = async () => {
  try {
    loading.value = true
    const success = await userStore.login(loginForm)
    if (success) {
      router.push('/dashboard')
    }
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 治愈系清新风格样式 */
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  /* 马卡龙柔和渐变背景 */
  background: linear-gradient(135deg, #D9F8E4 0%, #FCE7F3 50%, #FFF8E1 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 治愈系装饰元素 */
.floating-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

/* 透明气泡动画 */
.bubble {
  position: absolute;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: float 6s ease-in-out infinite;
}

.bubble-1 {
  width: 60px;
  height: 60px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.bubble-2 {
  width: 40px;
  height: 40px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.bubble-3 {
  width: 80px;
  height: 80px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

/* 花瓣装饰 */
.petal {
  position: absolute;
  font-size: 24px;
  animation: gentle-sway 8s ease-in-out infinite;
}

.petal-1 {
  top: 15%;
  right: 20%;
  animation-delay: 1s;
}

.petal-2 {
  bottom: 25%;
  right: 10%;
  animation-delay: 3s;
}

/* 星光粒子 */
.sparkle {
  position: absolute;
  font-size: 16px;
  animation: twinkle 4s ease-in-out infinite;
}

.sparkle-1 {
  top: 30%;
  left: 15%;
  animation-delay: 0.5s;
}

.sparkle-2 {
  bottom: 40%;
  right: 25%;
  animation-delay: 2.5s;
}

/* 登录卡片 - 圆润治愈风格 */
.login-card {
  width: 100%;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px; /* 更圆润的边角 */
  padding: 48px;
  /* 轻量投影效果 - 模拟阳光透过云层 */
  box-shadow: 
    0 8px 32px rgba(217, 248, 228, 0.3),
    0 4px 16px rgba(252, 231, 243, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.6);
  position: relative;
  z-index: 2;
  transition: all 0.3s ease;
}

.login-card:hover {
  transform: translateY(-2px);
  box-shadow: 
    0 12px 40px rgba(217, 248, 228, 0.4),
    0 6px 20px rgba(252, 231, 243, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

/* 标题 - 圆润手写字体风格 */
.healing-title {
  font-size: 32px;
  font-weight: 500;
  color: #5B4B49; /* 暖棕色 */
  margin-bottom: 12px;
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  letter-spacing: 2px;
  text-shadow: 0 2px 4px rgba(91, 75, 73, 0.1);
}

.healing-subtitle {
  color: #8B7D7B;
  font-size: 15px;
  font-weight: 400;
  opacity: 0.8;
}

.login-form {
  margin-bottom: 28px;
}

/* 表单项样式优化 */
.healing-form-item :deep(.ant-form-item-label > label) {
  color: #6B5B73;
  font-weight: 500;
  font-size: 14px;
}

/* 输入框 - 圆角柔和设计 */
.healing-input :deep(.ant-input),
.healing-input :deep(.ant-input-password) {
  border-radius: 16px !important;
  border: 2px solid #EDEDED !important;
  background: rgba(255, 255, 255, 0.8) !important;
  padding: 12px 16px !important;
  font-size: 15px !important;
  transition: all 0.3s ease !important;
}

.healing-input :deep(.ant-input::placeholder),
.healing-input :deep(.ant-input-password input::placeholder) {
  color: #C8C8C8 !important;
  font-weight: 300;
}

.healing-input :deep(.ant-input:focus),
.healing-input :deep(.ant-input-password:focus),
.healing-input :deep(.ant-input-focused) {
  border-color: #D9F8E4 !important;
  box-shadow: 0 0 0 3px rgba(217, 248, 228, 0.3) !important;
  background: rgba(255, 255, 255, 0.95) !important;
}

.healing-input :deep(.ant-input:hover),
.healing-input :deep(.ant-input-password:hover) {
  border-color: #FCE7F3 !important;
}

/* 登录按钮 - 渐变柔色设计 */
.healing-button {
  border-radius: 16px !important;
  height: 48px !important;
  font-size: 16px !important;
  font-weight: 500 !important;
  border: 1px solid rgba(255, 225, 189, 0.5) !important;
  background: linear-gradient(135deg, #FFE1BD 0%, #FCE7F3 100%) !important;
  color: #fff !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1) !important;
  transition: all 0.3s ease !important;
  position: relative;
  overflow: hidden;
}

.healing-button:hover {
  transform: translateY(-1px) !important;
  background: linear-gradient(135deg, #FFD4A3 0%, #F8D7E8 100%) !important;
  box-shadow: 0 6px 20px rgba(255, 225, 189, 0.4) !important;
}

.healing-button:active {
  transform: translateY(0) !important;
}

/* 底部链接样式 */
.login-footer {
  text-align: center;
  color: #8B7D7B;
  font-size: 14px;
  font-weight: 400;
}

.healing-link {
  color: #E69A8D !important; /* 柔粉色 */
  text-decoration: none !important;
  margin-left: 6px;
  font-weight: 500;
  position: relative;
  transition: all 0.3s ease;
}

.healing-link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #E69A8D, #FCE7F3);
  transition: width 0.3s ease;
}

.healing-link:hover {
  color: #D4847A !important;
}

.healing-link:hover::after {
  width: 100%;
}

/* 动画定义 */
@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
    opacity: 0.7;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 1;
  }
}

@keyframes gentle-sway {
  0%, 100% {
    transform: translateX(0) rotate(0deg);
  }
  25% {
    transform: translateX(10px) rotate(5deg);
  }
  75% {
    transform: translateX(-10px) rotate(-5deg);
  }
}

@keyframes twinkle {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.2);
  }
}

/* 响应式设计 */
@media (max-width: 576px) {
  .login-card {
    padding: 32px 24px;
    margin: 16px;
    border-radius: 20px;
  }
  
  .healing-title {
    font-size: 28px;
  }
  
  .bubble, .petal, .sparkle {
    display: none; /* 移动端隐藏装饰元素 */
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 12px;
  }
  
  .login-card {
    padding: 24px 20px;
  }
}
</style>
