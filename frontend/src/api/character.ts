import request from './request'
import type { ApiResponse } from '@/types/user'

// 角色相关类型定义
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

export interface TestCharacterRequest {
  message: string
}

export interface CloneCharacterRequest {
  newName: string
}

export const characterApi = {
  // 创建角色
  createCharacter: (data: CreateCharacterRequest): Promise<ApiResponse<CharacterResponse>> => {
    return request.post('/characters', data)
  },

  // 获取角色列表
  getCharacters: (): Promise<ApiResponse<CharacterResponse[]>> => {
    return request.get('/characters')
  },

  // 获取角色详情
  getCharacter: (characterId: number): Promise<ApiResponse<CharacterResponse>> => {
    return request.get(`/characters/${characterId}`)
  },

  // 更新角色
  updateCharacter: (characterId: number, data: UpdateCharacterRequest): Promise<ApiResponse<CharacterResponse>> => {
    return request.put(`/characters/${characterId}`, data)
  },

  // 删除角色
  deleteCharacter: (characterId: number): Promise<ApiResponse<void>> => {
    return request.delete(`/characters/${characterId}`)
  },

  // 测试角色
  testCharacter: (characterId: number, data: TestCharacterRequest): Promise<ApiResponse<string>> => {
    return request.post(`/characters/${characterId}/test`, data)
  },

  // 复制角色
  cloneCharacter: (characterId: number, data: CloneCharacterRequest): Promise<ApiResponse<CharacterResponse>> => {
    return request.post(`/characters/${characterId}/clone`, data)
  },

  // 搜索角色
  searchCharacters: (keyword: string, page = 0, size = 20): Promise<ApiResponse<{ content: CharacterResponse[] }>> => {
    return request.get('/characters/search', {
      params: { keyword, page, size }
    })
  },

  // 获取热门角色
  getPopularCharacters: (page = 0, size = 20): Promise<ApiResponse<{ content: CharacterResponse[] }>> => {
    return request.get('/characters/popular', {
      params: { page, size }
    })
  }
}
