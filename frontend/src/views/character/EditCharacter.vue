<template>
  <div class="edit-character">
    <div class="page-header">
      <a-breadcrumb>
        <a-breadcrumb-item>
          <router-link to="/dashboard">首页</router-link>
        </a-breadcrumb-item>
        <a-breadcrumb-item>
          <router-link to="/dashboard/characters">AI角色</router-link>
        </a-breadcrumb-item>
        <a-breadcrumb-item>编辑角色</a-breadcrumb-item>
      </a-breadcrumb>
      
      <h1>编辑AI角色</h1>
      <p>修改您的AI角色设置</p>
    </div>

    <div class="edit-form" v-if="!loading">
      <a-form
        :model="characterForm"
        :rules="rules"
        layout="vertical"
        @finish="handleSubmit"
      >
        <a-row :gutter="24">
          <!-- 左侧基本信息 -->
          <a-col :span="12">
            <a-card title="基本信息" class="form-card">
              <a-form-item label="角色名称" name="name" required>
                <a-input
                  v-model:value="characterForm.name"
                  placeholder="给您的AI角色起个名字"
                  :maxlength="50"
                  show-count
                />
              </a-form-item>

              <a-form-item label="角色描述" name="description">
                <a-textarea
                  v-model:value="characterForm.description"
                  placeholder="简单描述一下这个角色..."
                  :rows="3"
                  :maxlength="1000"
                  show-count
                />
              </a-form-item>

              <a-form-item label="头像URL" name="avatarUrl">
                <a-input
                  v-model:value="characterForm.avatarUrl"
                  placeholder="输入头像图片链接（可选）"
                  :maxlength="1500"
                  show-count
                />
              </a-form-item>

              <a-form-item label="性格特征" name="personality" required>
                <a-select
                  v-model:value="characterForm.personality"
                  placeholder="选择角色性格"
                  :options="personalityOptions"
                />
              </a-form-item>

              <a-form-item label="性别" name="gender" required>
                <a-radio-group v-model:value="characterForm.gender">
                  <a-radio value="MALE">男性</a-radio>
                  <a-radio value="FEMALE">女性</a-radio>
                  <a-radio value="OTHER">其他</a-radio>
                </a-radio-group>
              </a-form-item>

              <a-form-item label="年龄" name="age">
                <a-input-number
                  v-model:value="characterForm.age"
                  :min="1"
                  :max="200"
                  placeholder="角色年龄（可选）"
                  style="width: 100%"
                />
              </a-form-item>
            </a-card>
          </a-col>

          <!-- 右侧高级设置 -->
          <a-col :span="12">
            <a-card title="角色设定" class="form-card">
              <a-form-item label="背景故事" name="backgroundStory">
                <a-textarea
                  v-model:value="characterForm.backgroundStory"
                  placeholder="描述角色的背景故事..."
                  :rows="4"
                  :maxlength="2000"
                  show-count
                />
              </a-form-item>

              <a-form-item label="系统提示词" name="systemPrompt">
                <a-textarea
                  v-model:value="characterForm.systemPrompt"
                  placeholder="自定义系统提示词（高级用户）"
                  :rows="4"
                  :maxlength="2000"
                  show-count
                />
              </a-form-item>

              <a-form-item label="创造性">
                <a-slider
                  v-model:value="characterForm.temperature"
                  :min="0"
                  :max="1"
                  :step="0.1"
                  :tooltip-formatter="formatTemperature"
                />
                <div class="slider-labels">
                  <span>保守</span>
                  <span>{{ formatTemperature(characterForm.temperature) }}</span>
                  <span>创新</span>
                </div>
              </a-form-item>

              <a-form-item label="回复长度">
                <a-input-number
                  v-model:value="characterForm.maxTokens"
                  :min="50"
                  :max="2000"
                  :step="50"
                  placeholder="最大回复长度"
                  style="width: 100%"
                />
              </a-form-item>
            </a-card>
          </a-col>
        </a-row>

        <div class="form-actions">
          <a-space>
            <a-button @click="router.back()">取消</a-button>
            <a-button type="primary" html-type="submit" :loading="updating">
              保存修改
            </a-button>
          </a-space>
        </div>
      </a-form>
    </div>

    <div v-else class="loading-container">
      <a-spin size="large" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { characterApi, type UpdateCharacterRequest, type CharacterResponse } from '@/api/character'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const updating = ref(false)

// 获取角色ID
const characterId = Number(route.params.characterId)

// 表单数据
const characterForm = reactive<UpdateCharacterRequest>({
  name: '',
  description: '',
  avatarUrl: '',
  personality: '',
  gender: '',
  age: undefined,
  backgroundStory: '',
  systemPrompt: '',
  temperature: 0.7,
  maxTokens: 500
})

// 性格选项
const personalityOptions = [
  { value: 'GENTLE', label: '温柔体贴' },
  { value: 'CHEERFUL', label: '开朗活泼' },
  { value: 'MATURE', label: '成熟稳重' },
  { value: 'CUTE', label: '可爱天真' },
  { value: 'COOL', label: '冷酷帅气' },
  { value: 'MYSTERIOUS', label: '神秘莫测' },
  { value: 'INTELLECTUAL', label: '知性优雅' },
  { value: 'PASSIONATE', label: '热情奔放' }
]

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 1, max: 50, message: '角色名称长度应在1-50个字符之间', trigger: 'blur' }
  ],
  personality: [
    { required: true, message: '请选择角色性格', trigger: 'change' }
  ],
  gender: [
    { required: true, message: '请选择角色性别', trigger: 'change' }
  ]
}

// 加载角色数据
const loadCharacter = async () => {
  try {
    loading.value = true
    const response = await characterApi.getCharacter(characterId)
    const character = response.data
    
    // 填充表单数据
    Object.assign(characterForm, {
      name: character.name,
      description: character.description,
      avatarUrl: character.avatarUrl,
      personality: character.personality,
      gender: character.gender,
      age: character.age,
      backgroundStory: character.backgroundStory,
      systemPrompt: character.systemPrompt,
      temperature: character.temperature,
      maxTokens: character.maxTokens
    })
  } catch (error) {
    console.error('加载角色失败:', error)
    message.error('加载角色失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    updating.value = true
    await characterApi.updateCharacter(characterId, characterForm)
    message.success('角色更新成功')
    router.push('/dashboard/characters')
  } catch (error) {
    console.error('更新角色失败:', error)
    message.error('更新角色失败')
  } finally {
    updating.value = false
  }
}

// 格式化温度值
const formatTemperature = (value: number) => {
  if (value <= 0.3) return '保守'
  if (value <= 0.7) return '平衡'
  return '创新'
}

onMounted(() => {
  if (!characterId || isNaN(characterId)) {
    message.error('无效的角色ID')
    router.back()
    return
  }
  loadCharacter()
})
</script>

<style scoped>
.edit-character {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 16px 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  color: #666;
  margin: 0;
}

.edit-form {
  max-width: 1200px;
}

.form-card {
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-actions {
  text-align: center;
  padding: 24px 0;
}

.slider-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}
</style>
