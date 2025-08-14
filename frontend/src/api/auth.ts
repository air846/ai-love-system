import request from './request'
import type {
  LoginRequest,
  RegisterRequest,
  LoginResponse,
  User,
  ApiResponse,
  UpdateUserProfileRequest,
  UserPreferences,
  FileUploadResponse
} from '@/types/user'

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
  updateProfile: (data: UpdateUserProfileRequest): Promise<ApiResponse<User>> => {
    return request.put('/auth/profile', data)
  },

  // 获取用户偏好设置
  getUserPreferences: (): Promise<ApiResponse<UserPreferences>> => {
    return request.get('/auth/preferences')
  },

  // 更新用户偏好设置
  updateUserPreferences: (data: UserPreferences): Promise<ApiResponse<UserPreferences>> => {
    return request.put('/auth/preferences', data)
  },

  // 重置用户偏好设置
  resetUserPreferences: (): Promise<ApiResponse<UserPreferences>> => {
    return request.post('/auth/preferences/reset')
  },

  // 上传头像
  uploadAvatar: (file: File): Promise<ApiResponse<string>> => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/upload/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 删除文件
  deleteFile: (filePath: string): Promise<ApiResponse<string>> => {
    return request.delete('/upload/file', { params: { filePath } })
  }
}
