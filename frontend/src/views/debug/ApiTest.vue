<template>
  <div class="api-test">
    <h2>API连接测试</h2>
    
    <div class="test-section">
      <h3>1. 健康检查测试</h3>
      <a-button @click="testHealth" :loading="healthLoading" type="primary">
        测试健康检查
      </a-button>
      <div v-if="healthResult" class="result">
        <pre>{{ JSON.stringify(healthResult, null, 2) }}</pre>
      </div>
    </div>

    <div class="test-section">
      <h3>2. Ping测试</h3>
      <a-button @click="testPing" :loading="pingLoading" type="primary">
        测试Ping
      </a-button>
      <div v-if="pingResult" class="result">
        <pre>{{ JSON.stringify(pingResult, null, 2) }}</pre>
      </div>
    </div>

    <div class="test-section">
      <h3>3. 登录测试</h3>
      <a-form layout="inline">
        <a-form-item>
          <a-input v-model:value="loginData.username" placeholder="用户名" />
        </a-form-item>
        <a-form-item>
          <a-input-password v-model:value="loginData.password" placeholder="密码" />
        </a-form-item>
        <a-form-item>
          <a-button @click="testLogin" :loading="loginLoading" type="primary">
            测试登录
          </a-button>
        </a-form-item>
      </a-form>
      <div v-if="loginResult" class="result">
        <pre>{{ JSON.stringify(loginResult, null, 2) }}</pre>
      </div>
    </div>

    <div class="test-section">
      <h3>4. 用户信息测试</h3>
      <a-button @click="testUserInfo" :loading="userInfoLoading" type="primary">
        测试获取用户信息
      </a-button>
      <div v-if="userInfoResult" class="result">
        <pre>{{ JSON.stringify(userInfoResult, null, 2) }}</pre>
      </div>
    </div>

    <div class="test-section">
      <h3>5. 网络信息</h3>
      <div class="network-info">
        <p><strong>当前URL:</strong> {{ window.location.href }}</p>
        <p><strong>API Base URL:</strong> {{ getApiBaseUrl() }}</p>
        <p><strong>Token:</strong> {{ getToken() ? '已设置' : '未设置' }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import request from '@/api/request'
import { authApi } from '@/api/auth'

const healthLoading = ref(false)
const healthResult = ref(null)

const pingLoading = ref(false)
const pingResult = ref(null)

const loginLoading = ref(false)
const loginResult = ref(null)
const loginData = reactive({
  username: '',
  password: ''
})

const userInfoLoading = ref(false)
const userInfoResult = ref(null)

const testHealth = async () => {
  healthLoading.value = true
  try {
    const response = await request.get('/health')
    healthResult.value = response
    message.success('健康检查成功')
  } catch (error) {
    healthResult.value = error
    message.error('健康检查失败')
  } finally {
    healthLoading.value = false
  }
}

const testPing = async () => {
  pingLoading.value = true
  try {
    const response = await request.get('/health/ping')
    pingResult.value = response
    message.success('Ping测试成功')
  } catch (error) {
    pingResult.value = error
    message.error('Ping测试失败')
  } finally {
    pingLoading.value = false
  }
}

const testLogin = async () => {
  if (!loginData.username || !loginData.password) {
    message.warning('请输入用户名和密码')
    return
  }
  
  loginLoading.value = true
  try {
    const response = await authApi.login(loginData)
    loginResult.value = response
    message.success('登录测试成功')
  } catch (error) {
    loginResult.value = error
    message.error('登录测试失败')
  } finally {
    loginLoading.value = false
  }
}

const testUserInfo = async () => {
  userInfoLoading.value = true
  try {
    const response = await authApi.getUserInfo()
    userInfoResult.value = response
    message.success('用户信息获取成功')
  } catch (error) {
    userInfoResult.value = error
    message.error('用户信息获取失败')
  } finally {
    userInfoLoading.value = false
  }
}

const getApiBaseUrl = () => {
  return window.location.origin + '/api'
}

const getToken = () => {
  return localStorage.getItem('token')
}
</script>

<style scoped>
.api-test {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
}

.test-section {
  margin-bottom: 32px;
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
}

.test-section h3 {
  margin-top: 0;
  color: #262626;
}

.result {
  margin-top: 16px;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 4px;
  max-height: 300px;
  overflow-y: auto;
}

.result pre {
  margin: 0;
  font-size: 12px;
  white-space: pre-wrap;
  word-break: break-all;
}

.network-info p {
  margin: 8px 0;
  font-family: monospace;
  font-size: 14px;
}
</style>
