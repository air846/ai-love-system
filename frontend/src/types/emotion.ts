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

// 情感类型枚举
export enum EmotionType {
  JOY = 'JOY',
  SADNESS = 'SADNESS',
  ANGER = 'ANGER',
  FEAR = 'FEAR',
  SURPRISE = 'SURPRISE',
  DISGUST = 'DISGUST',
  LOVE = 'LOVE',
  EXCITEMENT = 'EXCITEMENT',
  CALM = 'CALM',
  ANXIETY = 'ANXIETY',
  HAPPINESS = 'HAPPINESS',
  DISAPPOINTMENT = 'DISAPPOINTMENT',
  CURIOSITY = 'CURIOSITY',
  CONFUSION = 'CONFUSION',
  NEUTRAL = 'NEUTRAL'
}

// 情感描述映射
export const EmotionDescriptions = {
  [EmotionType.JOY]: '喜悦',
  [EmotionType.SADNESS]: '悲伤',
  [EmotionType.ANGER]: '愤怒',
  [EmotionType.FEAR]: '恐惧',
  [EmotionType.SURPRISE]: '惊讶',
  [EmotionType.DISGUST]: '厌恶',
  [EmotionType.LOVE]: '爱意',
  [EmotionType.EXCITEMENT]: '兴奋',
  [EmotionType.CALM]: '平静',
  [EmotionType.ANXIETY]: '焦虑',
  [EmotionType.HAPPINESS]: '快乐',
  [EmotionType.DISAPPOINTMENT]: '失望',
  [EmotionType.CURIOSITY]: '好奇',
  [EmotionType.CONFUSION]: '困惑',
  [EmotionType.NEUTRAL]: '中性'
}
