import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { message } from 'ant-design-vue'
import router from '@/router'

// 获取API基础URL
const getBaseURL = () => {
  // 在开发环境中使用代理
  if (import.meta.env.DEV) {
    return '/api'
  }
  // 在生产环境中使用环境变量或默认值
  return import.meta.env.VITE_API_BASE_URL || '/api'
}

// 创建axios实例
const request: AxiosInstance = axios.create({
  baseURL: getBaseURL(),
  timeout: 30000, // 增加超时时间到30秒，因为AI响应可能需要更长时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 在开发环境中输出调试信息
if (import.meta.env.DEV) {
  console.log('API Base URL:', getBaseURL())
  console.log('Environment:', import.meta.env.MODE)
}

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加token
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response
    
    // 如果是文件下载等特殊响应，直接返回
    if (response.config.responseType === 'blob') {
      return response
    }
    
    // 统一处理响应数据
    if (data.success === false) {
      message.error(data.message || '请求失败')
      
      // 如果是认证失败，跳转到登录页
      if (data.code === 401) {
        localStorage.removeItem('token')
        router.push('/login')
      }
      
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    
    return data
  },
  (error) => {
    let errorMessage = '网络错误'
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          errorMessage = data.message || '请求参数错误'
          break
        case 401:
          errorMessage = '未授权，请重新登录'
          localStorage.removeItem('token')
          router.push('/login')
          break
        case 403:
          errorMessage = '拒绝访问'
          break
        case 404:
          errorMessage = '请求地址不存在'
          break
        case 500:
          errorMessage = '服务器内部错误'
          break
        default:
          errorMessage = data.message || `请求失败 (${status})`
      }
    } else if (error.request) {
      errorMessage = '网络连接失败'
    }
    
    message.error(errorMessage)
    return Promise.reject(error)
  }
)

export default request
