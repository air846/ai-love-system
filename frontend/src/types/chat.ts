// 聊天相关类型定义

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
  unreadCount?: number
  highlight?: string
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

export interface CreateConversationRequest {
  characterId: number
  title?: string
}

export interface SendMessageRequest {
  content: string
}

export interface ConversationSettingsRequest {
  title?: string
  description?: string
  aiTemperature?: number
  aiMaxTokens?: number
  aiModel?: string
  autoSaveEnabled?: boolean
  notificationEnabled?: boolean
  contextLength?: number
  responseStyle?: string
  languagePreference?: string
}

export interface ConversationSettingsResponse {
  id: number
  title: string
  description?: string
  status: string
  aiTemperature?: number
  aiMaxTokens?: number
  aiModel?: string
  autoSaveEnabled: boolean
  notificationEnabled: boolean
  contextLength: number
  responseStyle?: string
  languagePreference: string
}
