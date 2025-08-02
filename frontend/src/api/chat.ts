import request from './request'
import type { ApiResponse } from '@/types/user'
import type { ConversationSettingsRequest, ConversationSettingsResponse } from '@/types/chat'

// 聊天相关类型定义
export interface CreateConversationRequest {
  characterId: number
  title?: string
}

export interface SendMessageRequest {
  content: string
}

export interface ConversationListResponse {
  id: number
  title: string
  description?: string
  status: string
  messageCount: number
  characterId: number
  characterName: string
  characterAvatar?: string
  characterPersonality: string
  lastMessageAt?: string
  createdAt: string
}

export interface MessageResponse {
  id: number
  content: string
  senderType: 'USER' | 'AI' | 'SYSTEM'
  messageType: string
  emotionScore?: number
  tokenCount?: number
  processingTimeMs?: number
  createdAt: string
  conversationId: number
}

export interface ConversationDetailResponse {
  id: number
  title: string
  description?: string
  status: string
  messageCount: number
  contextSummary?: string
  character: {
    id: number
    name: string
    description?: string
    avatarUrl?: string
    personality: string
    personalityDescription: string
    gender: string
    age?: number
    backgroundStory?: string
    temperature: number
    maxTokens: number
    usageCount: number
  }
  lastMessageAt?: string
  createdAt: string
  updatedAt: string
}

export const chatApi = {
  // 创建对话
  createConversation: (data: CreateConversationRequest): Promise<ApiResponse<ConversationListResponse>> => {
    return request.post('/chat/conversations', data)
  },

  // 发送消息
  sendMessage: (conversationId: number, data: SendMessageRequest): Promise<ApiResponse<MessageResponse[]>> => {
    return request.post(`/chat/conversations/${conversationId}/messages`, data)
  },

  // 获取对话列表
  getConversations: (page = 0, size = 20): Promise<ApiResponse<{ content: ConversationListResponse[] }>> => {
    return request.get('/conversations', {
      params: { page, size, sort: 'lastMessageAt,desc' }
    })
  },

  // 获取对话详情
  getConversationDetail: (conversationId: number): Promise<ApiResponse<ConversationDetailResponse>> => {
    return request.get(`/conversations/${conversationId}`)
  },

  // 获取对话消息
  getMessages: (conversationId: number): Promise<ApiResponse<MessageResponse[]>> => {
    return request.get(`/chat/conversations/${conversationId}/messages`)
  },

  // 删除对话
  deleteConversation: (conversationId: number): Promise<ApiResponse<void>> => {
    return request.delete(`/chat/conversations/${conversationId}`)
  },

  // 更新对话
  updateConversation: (conversationId: number, data: { title?: string; description?: string }): Promise<ApiResponse<ConversationDetailResponse>> => {
    return request.put(`/conversations/${conversationId}`, data)
  },

  // 搜索对话
  searchConversations: (keyword: string, page = 0, size = 20): Promise<ApiResponse<{ content: ConversationListResponse[] }>> => {
    return request.get('/conversations/search', {
      params: { keyword, page, size }
    })
  },

  // 获取最近对话
  getRecentConversations: (limit = 10): Promise<ApiResponse<ConversationListResponse[]>> => {
    return request.get('/conversations/recent', {
      params: { limit }
    })
  },

  // 归档对话
  archiveConversation: (conversationId: number): Promise<ApiResponse<void>> => {
    return request.post(`/conversations/${conversationId}/archive`)
  },

  // 恢复对话
  restoreConversation: (conversationId: number): Promise<ApiResponse<void>> => {
    return request.post(`/conversations/${conversationId}/restore`)
  },

  // 批量删除对话
  batchDeleteConversations: (conversationIds: number[]): Promise<ApiResponse<void>> => {
    return request.delete('/conversations/batch', { data: conversationIds })
  },

  // 获取对话统计
  getConversationStats: (): Promise<ApiResponse<{
    totalConversations: number
    activeConversations: number
    statusCounts: Array<[string, number]>
  }>> => {
    return request.get('/conversations/stats')
  },

  // 检查AI服务状态
  checkAiStatus: (): Promise<ApiResponse<boolean>> => {
    return request.get('/chat/status')
  },

  // 获取对话设置
  getConversationSettings: (conversationId: number): Promise<ApiResponse<ConversationSettingsResponse>> => {
    return request.get(`/conversations/${conversationId}/settings`)
  },

  // 更新对话设置
  updateConversationSettings: (conversationId: number, data: ConversationSettingsRequest): Promise<ApiResponse<ConversationSettingsResponse>> => {
    return request.put(`/conversations/${conversationId}/settings`, data)
  }
}
