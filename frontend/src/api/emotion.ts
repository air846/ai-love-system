import request from './request'
import type { ApiResponse } from '@/types/user'

// 情感分析相关类型定义
export interface EmotionAnalysisResponse {
  id: number
  messageId: number
  conversationId: number
  emotionType: string
  emotionDescription: string
  confidence: number
  intensity: number
  valence: number
  arousal: number
  keywords?: string
  intensityLevel: string
  isPositive: boolean
  isNegative: boolean
  isNeutral: boolean
  isHighIntensity: boolean
  createdAt: string
}

export interface EmotionTrendResponse {
  date: string
  averageValence: number
  averageIntensity: number
  valenceDescription: string
  intensityDescription: string
}

export interface EmotionStatsResponse {
  totalAnalyses: number
  emotionDistribution: Record<string, number>
  positiveRatio: number
  negativeRatio: number
  neutralRatio: number
  dominantEmotion: string
  emotionalHealthScore: number
  emotionalHealthDescription: string
}

export const emotionApi = {
  // 分析消息情感
  analyzeMessage: (messageId: number): Promise<ApiResponse<EmotionAnalysisResponse>> => {
    return request.post(`/emotions/analyze/${messageId}`)
  },

  // 获取对话的情感分析
  getConversationEmotions: (conversationId: number): Promise<ApiResponse<EmotionAnalysisResponse[]>> => {
    return request.get(`/emotions/conversations/${conversationId}`)
  },

  // 获取情感趋势
  getEmotionTrend: (days = 30): Promise<ApiResponse<EmotionTrendResponse[]>> => {
    return request.get('/emotions/trends', {
      params: { days }
    })
  },

  // 获取情感统计
  getEmotionStats: (): Promise<ApiResponse<EmotionStatsResponse>> => {
    return request.get('/emotions/stats')
  }
}
