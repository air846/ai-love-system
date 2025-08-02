// 角色相关类型定义

export interface CharacterResponse {
  id: number
  name: string
  description?: string
  avatarUrl?: string
  personality: string
  personalityDescription: string
  gender: string
  genderDescription: string
  age?: number
  backgroundStory?: string
  systemPrompt?: string
  temperature: number
  maxTokens: number
  status: string
  usageCount: number
  createdAt: string
  updatedAt: string
}

export interface CreateCharacterRequest {
  name: string
  description?: string
  avatarUrl?: string
  personality: string
  gender: string
  age?: number
  backgroundStory?: string
  systemPrompt?: string
  temperature?: number
  maxTokens?: number
}

export interface UpdateCharacterRequest {
  name?: string
  description?: string
  avatarUrl?: string
  personality?: string
  gender?: string
  age?: number
  backgroundStory?: string
  systemPrompt?: string
  temperature?: number
  maxTokens?: number
}

// 性格枚举
export enum Personality {
  FRIENDLY = 'FRIENDLY',
  SHY = 'SHY',
  OUTGOING = 'OUTGOING',
  MYSTERIOUS = 'MYSTERIOUS',
  PLAYFUL = 'PLAYFUL',
  SERIOUS = 'SERIOUS',
  ROMANTIC = 'ROMANTIC',
  INTELLECTUAL = 'INTELLECTUAL'
}

// 性别枚举
export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  OTHER = 'OTHER'
}

// 性格描述映射
export const PersonalityDescriptions = {
  [Personality.FRIENDLY]: '友善',
  [Personality.SHY]: '害羞',
  [Personality.OUTGOING]: '外向',
  [Personality.MYSTERIOUS]: '神秘',
  [Personality.PLAYFUL]: '顽皮',
  [Personality.SERIOUS]: '严肃',
  [Personality.ROMANTIC]: '浪漫',
  [Personality.INTELLECTUAL]: '知性'
}

// 性别描述映射
export const GenderDescriptions = {
  [Gender.MALE]: '男性',
  [Gender.FEMALE]: '女性',
  [Gender.OTHER]: '其他'
}
