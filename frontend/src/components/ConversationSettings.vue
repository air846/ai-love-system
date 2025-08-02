<template>
  <a-modal
    v-model:open="visible"
    title="对话设置"
    width="800px"
    :confirm-loading="loading"
    @ok="handleSave"
    @cancel="handleCancel"
    class="conversation-settings-modal"
  >
    <div class="settings-content">
      <a-tabs v-model:activeKey="activeTab" type="card">
        <!-- 基本信息 -->
        <a-tab-pane key="basic" tab="基本信息">
          <a-form
            :model="formData"
            :rules="rules"
            layout="vertical"
            ref="basicFormRef"
          >
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="对话标题" name="title">
                  <a-input
                    v-model:value="formData.title"
                    placeholder="请输入对话标题"
                    :maxlength="100"
                    show-count
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="语言偏好" name="languagePreference">
                  <a-select
                    v-model:value="formData.languagePreference"
                    placeholder="选择语言偏好"
                  >
                    <a-select-option value="zh-CN">中文</a-select-option>
                    <a-select-option value="en-US">English</a-select-option>
                    <a-select-option value="ja-JP">日本語</a-select-option>
                    <a-select-option value="ko-KR">한국어</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>

            <a-form-item label="对话描述" name="description">
              <a-textarea
                v-model:value="formData.description"
                placeholder="请输入对话描述（可选）"
                :rows="3"
                :maxlength="500"
                show-count
              />
            </a-form-item>

            <a-form-item label="回复风格" name="responseStyle">
              <a-select
                v-model:value="formData.responseStyle"
                placeholder="选择回复风格"
                allow-clear
              >
                <a-select-option value="formal">正式</a-select-option>
                <a-select-option value="casual">随意</a-select-option>
                <a-select-option value="romantic">浪漫</a-select-option>
                <a-select-option value="humorous">幽默</a-select-option>
                <a-select-option value="professional">专业</a-select-option>
              </a-select>
            </a-form-item>
          </a-form>
        </a-tab-pane>

        <!-- AI参数 -->
        <a-tab-pane key="ai" tab="AI参数">
          <a-form
            :model="formData"
            :rules="rules"
            layout="vertical"
            ref="aiFormRef"
          >
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="AI模型" name="aiModel">
                  <a-select
                    v-model:value="formData.aiModel"
                    placeholder="选择AI模型"
                    allow-clear
                  >
                    <a-select-option value="glm-4-flash">GLM-4-Flash</a-select-option>
                    <a-select-option value="glm-4">GLM-4</a-select-option>
                    <a-select-option value="glm-3-turbo">GLM-3-Turbo</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="最大令牌数" name="aiMaxTokens">
                  <a-input-number
                    v-model:value="formData.aiMaxTokens"
                    placeholder="最大令牌数"
                    :min="1"
                    :max="8192"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-form-item label="温度参数" name="aiTemperature">
              <div class="temperature-setting">
                <a-slider
                  v-model:value="formData.aiTemperature"
                  :min="0"
                  :max="1"
                  :step="0.1"
                  :marks="temperatureMarks"
                />
                <div class="temperature-desc">
                  <span v-if="(formData.aiTemperature || 0) <= 0.3">保守回复</span>
                  <span v-else-if="(formData.aiTemperature || 0) <= 0.7">平衡回复</span>
                  <span v-else>创意回复</span>
                </div>
              </div>
            </a-form-item>

            <a-form-item label="上下文长度" name="contextLength">
              <a-input-number
                v-model:value="formData.contextLength"
                placeholder="上下文长度"
                :min="1"
                :max="50"
                style="width: 100%"
              />
              <div class="form-tip">
                控制AI记忆的对话轮数，数值越大消耗的令牌越多
              </div>
            </a-form-item>
          </a-form>
        </a-tab-pane>

        <!-- 偏好设置 -->
        <a-tab-pane key="preferences" tab="偏好设置">
          <a-form layout="vertical">
            <a-form-item label="功能开关">
              <a-space direction="vertical" style="width: 100%">
                <a-switch
                  v-model:checked="formData.autoSaveEnabled"
                  checked-children="开启"
                  un-checked-children="关闭"
                >
                  <template #checkedChildren>开启</template>
                  <template #unCheckedChildren>关闭</template>
                </a-switch>
                <span class="switch-label">自动保存对话</span>
              </a-space>
            </a-form-item>

            <a-form-item>
              <a-space direction="vertical" style="width: 100%">
                <a-switch
                  v-model:checked="formData.notificationEnabled"
                  checked-children="开启"
                  un-checked-children="关闭"
                >
                  <template #checkedChildren>开启</template>
                  <template #unCheckedChildren>关闭</template>
                </a-switch>
                <span class="switch-label">消息通知</span>
              </a-space>
            </a-form-item>
          </a-form>
        </a-tab-pane>
      </a-tabs>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { message } from 'ant-design-vue'
import { chatApi } from '@/api/chat'
import type { ConversationSettingsRequest, ConversationSettingsResponse } from '@/types/chat'

interface Props {
  conversationId?: number
  modelValue: boolean
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'updated', settings: ConversationSettingsResponse): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 响应式数据
const visible = ref(false)
const loading = ref(false)
const activeTab = ref('basic')
const basicFormRef = ref()
const aiFormRef = ref()

const formData = reactive<ConversationSettingsRequest>({
  title: '',
  description: '',
  aiTemperature: 0.7,
  aiMaxTokens: 2048,
  aiModel: 'glm-4-flash',
  autoSaveEnabled: true,
  notificationEnabled: true,
  contextLength: 10,
  responseStyle: '',
  languagePreference: 'zh-CN'
})

// 温度参数标记
const temperatureMarks = {
  0: '0',
  0.3: '0.3',
  0.7: '0.7',
  1: '1'
}

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入对话标题', trigger: 'blur' },
    { max: 100, message: '标题不能超过100个字符', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '描述不能超过500个字符', trigger: 'blur' }
  ],
  aiTemperature: [
    { type: 'number', min: 0, max: 1, message: '温度参数必须在0-1之间', trigger: 'change' }
  ],
  aiMaxTokens: [
    { type: 'number', min: 1, max: 8192, message: '令牌数必须在1-8192之间', trigger: 'change' }
  ],
  contextLength: [
    { type: 'number', min: 1, max: 50, message: '上下文长度必须在1-50之间', trigger: 'change' }
  ]
}

// 监听props变化
watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
  if (newVal && props.conversationId) {
    loadSettings()
  }
})

watch(visible, (newVal) => {
  emit('update:modelValue', newVal)
})

// 加载设置
const loadSettings = async () => {
  if (!props.conversationId) return

  try {
    loading.value = true
    const response = await chatApi.getConversationSettings(props.conversationId)
    const settings = response.data

    // 更新表单数据
    Object.assign(formData, {
      title: settings.title,
      description: settings.description || '',
      aiTemperature: settings.aiTemperature || 0.7,
      aiMaxTokens: settings.aiMaxTokens || 2048,
      aiModel: settings.aiModel || 'glm-4-flash',
      autoSaveEnabled: settings.autoSaveEnabled,
      notificationEnabled: settings.notificationEnabled,
      contextLength: settings.contextLength,
      responseStyle: settings.responseStyle || '',
      languagePreference: settings.languagePreference
    })
  } catch (error) {
    console.error('加载对话设置失败:', error)
    message.error('加载对话设置失败')
  } finally {
    loading.value = false
  }
}

// 保存设置
const handleSave = async () => {
  if (!props.conversationId) return

  try {
    // 验证表单
    await Promise.all([
      basicFormRef.value?.validate(),
      aiFormRef.value?.validate()
    ])

    loading.value = true
    const response = await chatApi.updateConversationSettings(props.conversationId, formData)
    
    message.success('对话设置保存成功')
    emit('updated', response.data)
    visible.value = false
  } catch (error) {
    console.error('保存对话设置失败:', error)
    if (error && typeof error === 'object' && 'errorFields' in error) {
      message.error('请检查表单输入')
    } else {
      message.error('保存对话设置失败')
    }
  } finally {
    loading.value = false
  }
}

// 取消
const handleCancel = () => {
  visible.value = false
}
</script>

<style scoped>
.conversation-settings-modal :deep(.ant-modal-body) {
  padding: 16px;
}

.settings-content {
  max-height: 600px;
  overflow-y: auto;
}

.temperature-setting {
  margin-bottom: 8px;
}

.temperature-desc {
  text-align: center;
  color: #666;
  font-size: 12px;
  margin-top: 8px;
}

.form-tip {
  color: #999;
  font-size: 12px;
  margin-top: 4px;
}

.switch-label {
  margin-left: 8px;
  color: #666;
}

:deep(.ant-tabs-card .ant-tabs-tab) {
  border-radius: 6px 6px 0 0;
}

:deep(.ant-tabs-card .ant-tabs-content) {
  margin-top: -1px;
  border: 1px solid #d9d9d9;
  border-radius: 0 6px 6px 6px;
  padding: 16px;
}
</style>
