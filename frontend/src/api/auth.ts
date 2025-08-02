import request from './request'
import type { LoginRequest, RegisterRequest, LoginResponse, User, ApiResponse } from '@/types/user'

export const authApi = {
  // 用户登录
  login: (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
    return request.post('/auth/login', data)
  },

  // 用户注册
  register: (data: RegisterRequest): Promise<ApiResponse<void>> => {
    return request.post('/auth/register', data)
  },

  // 获取用户信息
  getUserInfo: (): Promise<ApiResponse<User>> => {
    return request.get('/auth/profile')
  },

  // 刷新token
  refreshToken: (): Promise<ApiResponse<{ token: string }>> => {
    return request.post('/auth/refresh')
  },

  // 修改密码
  changePassword: (data: { oldPassword: string; newPassword: string }): Promise<ApiResponse<void>> => {
    return request.put('/auth/password', data)
  },

  // 更新用户信息
  updateProfile: (data: Partial<User>): Promise<ApiResponse<User>> => {
    return request.put('/auth/profile', data)
  }
}
