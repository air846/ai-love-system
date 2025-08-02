import { defineStore } from 'pinia'
import { ref, computed, readonly } from 'vue'
import type { User, LoginRequest, RegisterRequest } from '@/types/user'
import { authApi } from '@/api/auth'
import { message } from 'ant-design-vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))
  const loading = ref(false)

  // 计算属性
  const isAuthenticated = computed(() => !!token.value && !!user.value)

  // 登录
  const login = async (loginData: LoginRequest) => {
    try {
      loading.value = true
      const response = await authApi.login(loginData)
      
      if (response.success) {
        token.value = response.data.token
        user.value = response.data.user
        localStorage.setItem('token', response.data.token)
        message.success('登录成功')
        return true
      } else {
        message.error(response.message || '登录失败')
        return false
      }
    } catch (error: any) {
      message.error(error.message || '登录失败')
      return false
    } finally {
      loading.value = false
    }
  }

  // 注册
  const register = async (registerData: RegisterRequest) => {
    try {
      loading.value = true
      const response = await authApi.register(registerData)
      
      if (response.success) {
        message.success('注册成功，请登录')
        return true
      } else {
        message.error(response.message || '注册失败')
        return false
      }
    } catch (error: any) {
      message.error(error.message || '注册失败')
      return false
    } finally {
      loading.value = false
    }
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const response = await authApi.getUserInfo()
      if (response.success) {
        user.value = response.data
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  // 登出
  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    message.success('已退出登录')
  }

  // 初始化用户信息
  const initUser = async () => {
    if (token.value && !user.value) {
      await fetchUserInfo()
    }
  }

  return {
    user: readonly(user),
    token: readonly(token),
    loading: readonly(loading),
    isAuthenticated,
    login,
    register,
    logout,
    fetchUserInfo,
    initUser
  }
})
