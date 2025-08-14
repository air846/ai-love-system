// 用户相关类型定义

export interface User {
  id: number
  username: string
  email: string
  nickname?: string
  avatarUrl?: string
  status: string
  loginCount: number
  emailVerified: boolean
  phone?: string
  birthDate?: string
  gender?: 'MALE' | 'FEMALE' | 'OTHER'
  genderDescription?: string
  bio?: string
  lastLoginIp?: string
  lastLoginAt?: string
  createdAt: string
  updatedAt: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  nickname?: string
  password: string
  confirmPassword: string
}

export interface UpdateUserProfileRequest {
  nickname?: string
  email?: string
  avatarUrl?: string
  phone?: string
  birthDate?: string
  gender?: 'MALE' | 'FEMALE' | 'OTHER'
  bio?: string
  preferences?: string
}

export interface LoginResponse {
  token: string
  user: User
}

export interface ApiResponse<T = any> {
  success: boolean
  message: string
  data: T
  code?: number
}

// 用户偏好设置类型定义
export interface UserPreferences {
  theme: ThemeSettings
  notification: NotificationSettings
  aiCharacter: AiCharacterPreferences
  privacy: PrivacySettings
}

export interface ThemeSettings {
  colorScheme: 'light' | 'dark' | 'auto'
  primaryColor: string
  language: string
  fontSize: 'small' | 'medium' | 'large'
}

export interface NotificationSettings {
  emailNotification: boolean
  pushNotification: boolean
  soundEnabled: boolean
  vibrationEnabled: boolean
  quietHoursStart: string
  quietHoursEnd: string
}

export interface AiCharacterPreferences {
  preferredPersonality: string
  preferredGender: string
  preferredAge: number
  conversationStyle: 'formal' | 'casual' | 'playful'
  responseSpeed: number
  enableEmotionAnalysis: boolean
  enableContextMemory: boolean
}

export interface PrivacySettings {
  profileVisible: boolean
  allowDataCollection: boolean
  shareUsageStatistics: boolean
  enableAnalytics: boolean
  dataRetentionPeriod: '1month' | '3months' | '6months' | '1year' | 'forever'
}

// 文件上传相关类型
export interface FileUploadResponse {
  url: string
  fileName: string
  fileSize: number
}
