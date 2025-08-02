<template>
  <div class="create-character">
    <div class="page-header">
      <a-button type="text" @click="goBack">
        <ArrowLeftOutlined />
        返回
      </a-button>
      <h1>创建AI角色</h1>
      <p>设计您的专属AI角色，定制独特的个性和对话风格</p>
    </div>

    <div class="create-form">
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
                  :maxlength="500"
                  show-count
                />
              </a-form-item>

              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item label="性格类型" name="personality" required>
                    <a-select
                      v-model:value="characterForm.personality"
                      placeholder="选择性格"
                    >
                      <a-select-option
                        v-for="(desc, key) in PersonalityDescriptions"
                        :key="key"
                        :value="key"
                      >
                        {{ desc }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                
                <a-col :span="12">
                  <a-form-item label="性别" name="gender" required>
                    <a-select
                      v-model:value="characterForm.gender"
                      placeholder="选择性别"
                    >
                      <a-select-option
                        v-for="(desc, key) in GenderDescriptions"
                        :key="key"
                        :value="key"
                      >
                        {{ desc }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
              </a-row>

              <a-form-item label="年龄" name="age">
                <a-input-number
                  v-model:value="characterForm.age"
                  placeholder="年龄（可选）"
                  :min="1"
                  :max="200"
                  style="width: 100%"
                />
              </a-form-item>
            </a-card>
          </a-col>

          <!-- 右侧高级设置 -->
          <a-col :span="12">
            <a-card title="高级设置" class="form-card">
              <a-form-item label="背景故事" name="backgroundStory">
                <a-textarea
                  v-model:value="characterForm.backgroundStory"
                  placeholder="描述角色的背景故事，这将影响对话风格..."
                  :rows="4"
                  :maxlength="2000"
                  show-count
                />
              </a-form-item>

              <a-form-item label="系统提示词" name="systemPrompt">
                <a-textarea
                  v-model:value="characterForm.systemPrompt"
                  placeholder="自定义系统提示词（高级用户）"
                  :rows="3"
                  :maxlength="5000"
                  show-count
                />
                <div class="form-tip">
                  留空将自动根据角色信息生成提示词
                </div>
              </a-form-item>

              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item label="创造性" name="temperature">
                    <a-slider
                      v-model:value="characterForm.temperature"
                      :min="0"
                      :max="1"
                      :step="0.1"
                      :tooltip-formatter="formatTemperature"
                    />
                    <div class="form-tip">
                      值越高回复越有创意，越低越稳定
                    </div>
                  </a-form-item>
                </a-col>
                
                <a-col :span="12">
                  <a-form-item label="最大令牌数" name="maxTokens">
                    <a-input-number
                      v-model:value="characterForm.maxTokens"
                      :min="100"
                      :max="4096"
                      :step="100"
                      style="width: 100%"
                    />
                    <div class="form-tip">
                      控制回复的最大长度
                    </div>
                  </a-form-item>
                </a-col>
              </a-row>
            </a-card>
          </a-col>
        </a-row>

        <!-- 预览区域 -->
        <a-card title="角色预览" class="preview-card">
          <div class="character-preview">
            <div class="preview-avatar">
              <a-avatar :size="80" :src="characterForm.avatarUrl">
                {{ characterForm.name?.charAt(0) || '?' }}
              </a-avatar>
            </div>
            
            <div class="preview-info">
              <h3>{{ characterForm.name || '未命名角色' }}</h3>
              <p>{{ characterForm.description || '暂无描述' }}</p>
              
              <div class="preview-tags">
                <a-tag v-if="characterForm.personality" color="blue">
                  {{ PersonalityDescriptions[characterForm.personality as keyof typeof PersonalityDescriptions] }}
                </a-tag>
                <a-tag v-if="characterForm.gender" color="green">
                  {{ GenderDescriptions[characterForm.gender as keyof typeof GenderDescriptions] }}
                </a-tag>
                <a-tag v-if="characterForm.age" color="orange">
                  {{ characterForm.age }}岁
                </a-tag>
              </div>
            </div>
          </div>
        </a-card>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <a-button @click="goBack" size="large">
            取消
          </a-button>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            :loading="creating"
          >
            创建角色
          </a-button>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { ArrowLeftOutlined } from '@ant-design/icons-vue'
import { characterApi } from '@/api/character'
import { PersonalityDescriptions, GenderDescriptions } from '@/types/character'
import type { CreateCharacterRequest } from '@/types/character'

const router = useRouter()

// 响应式数据
const creating = ref(false)

const characterForm = reactive<CreateCharacterRequest>({
  name: '',
  description: '',
  avatarUrl: '',
  personality: '',
  gender: '',
  age: undefined,
  backgroundStory: '',
  systemPrompt: '',
  temperature: 0.7,
  maxTokens: 2048
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入角色名称' },
    { min: 1, max: 50, message: '角色名称长度为1-50个字符' }
  ],
  description: [
    { max: 1000, message: '描述不能超过1000个字符' }
  ],
  avatarUrl: [
    { max: 500, message: '头像URL不能超过500个字符' }
  ],
  personality: [
    { required: true, message: '请选择性格类型' }
  ],
  gender: [
    { required: true, message: '请选择性别' }
  ],
  age: [
    { type: 'number', min: 1, max: 200, message: '年龄必须在1-200之间' }
  ],
  backgroundStory: [
    { max: 2000, message: '背景故事不能超过2000个字符' }
  ],
  systemPrompt: [
    { max: 5000, message: '系统提示词不能超过5000个字符' }
  ],
  temperature: [
    { type: 'number', min: 0, max: 1, message: '创造性参数必须在0-1之间' }
  ],
  maxTokens: [
    { type: 'number', min: 100, max: 4096, message: '最大令牌数必须在100-4096之间' }
  ]
}

// 方法
const goBack = () => {
  router.go(-1)
}

const handleSubmit = async () => {
  try {
    creating.value = true
    const response = await characterApi.createCharacter(characterForm)
    message.success('角色创建成功')
    router.push('/dashboard/characters')
  } catch (error) {
    console.error('创建角色失败:', error)
    message.error('创建角色失败')
  } finally {
    creating.value = false
  }
}

const formatTemperature = (value: number) => {
  if (value <= 0.3) return '保守'
  if (value <= 0.7) return '平衡'
  return '创新'
}
</script>

<style scoped>
.create-character {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 60px;
  /* 确保底部有足够空间显示按钮 */
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 16px 0 8px 0;
}

.page-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.create-form {
  background: white;
  border-radius: 8px;
  padding: 24px;
}

.form-card {
  margin-bottom: 24px;
}

.form-card .ant-card-head {
  border-bottom: 1px solid #f0f0f0;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.preview-card {
  margin-bottom: 24px;
}

.character-preview {
  display: flex;
  align-items: center;
  gap: 24px;
}

.preview-info h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.preview-info p {
  color: #666;
  margin-bottom: 16px;
  line-height: 1.5;
}

.preview-tags .ant-tag {
  margin-bottom: 4px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding: 24px;
  margin-top: 24px;
  /* 移除白色背景框样式 */
  background: transparent;
  /* 确保按钮区域可见 */
  min-height: 80px;
  align-items: center;
  width: 100%;
  position: sticky;
  bottom: 0;
  z-index: 100;
}

@media (max-width: 768px) {
  .create-form .ant-row {
    flex-direction: column;
  }
  
  .create-form .ant-col {
    width: 100% !important;
  }
  
  .character-preview {
    flex-direction: column;
    text-align: center;
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style>
