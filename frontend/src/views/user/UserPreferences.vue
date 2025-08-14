<template>
  <div class="user-preferences">
    <div class="page-header">
      <h2>偏好设置</h2>
      <p>个性化您的使用体验</p>
    </div>

    <div class="preferences-sections">
      <!-- 主题设置 -->
      <div class="preference-section">
        <div class="section-header">
          <h3>主题设置</h3>
          <p>自定义界面外观和语言</p>
        </div>
        
        <a-form layout="vertical" class="preference-form">
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item label="主题模式">
                <a-select 
                  v-model:value="preferences.theme.colorScheme" 
                  @change="handlePreferenceChange"
                >
                  <a-select-option value="light">浅色主题</a-select-option>
                  <a-select-option value="dark">深色主题</a-select-option>
                  <a-select-option value="auto">跟随系统</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            
            <a-col :span="12">
              <a-form-item label="字体大小">
                <a-select 
                  v-model:value="preferences.theme.fontSize" 
                  @change="handlePreferenceChange"
                >
                  <a-select-option value="small">小</a-select-option>
                  <a-select-option value="medium">中</a-select-option>
                  <a-select-option value="large">大</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
          
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item label="主色调">
                <a-input 
                  v-model:value="preferences.theme.primaryColor" 
                  type="color"
                  @change="handlePreferenceChange"
                />
              </a-form-item>
            </a-col>
            
            <a-col :span="12">
              <a-form-item label="语言">
                <a-select 
                  v-model:value="preferences.theme.language" 
                  @change="handlePreferenceChange"
                >
                  <a-select-option value="zh-CN">简体中文</a-select-option>
                  <a-select-option value="en-US">English</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <!-- 通知设置 -->
      <div class="preference-section">
        <div class="section-header">
          <h3>通知设置</h3>
          <p>管理消息通知和提醒</p>
        </div>
        
        <a-form layout="vertical" class="preference-form">
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item>
                <a-switch 
                  v-model:checked="preferences.notification.emailNotification"
                  @change="handlePreferenceChange"
                />
                <span class="switch-label">邮件通知</span>
              </a-form-item>
              
              <a-form-item>
                <a-switch 
                  v-model:checked="preferences.notification.pushNotification"
                  @change="handlePreferenceChange"
                />
                <span class="switch-label">推送通知</span>
              </a-form-item>
            </a-col>
            
            <a-col :span="12">
              <a-form-item>
                <a-switch 
                  v-model:checked="preferences.notification.soundEnabled"
                  @change="handlePreferenceChange"
                />
                <span class="switch-label">声音提醒</span>
              </a-form-item>
              
              <a-form-item>
                <a-switch 
                  v-model:checked="preferences.notification.vibrationEnabled"
                  @change="handlePreferenceChange"
                />
                <span class="switch-label">震动提醒</span>
              </a-form-item>
            </a-col>
          </a-row>
          
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item label="免打扰开始时间">
                <a-time-picker 
                  v-model:value="quietHoursStart" 
                  format="HH:mm"
                  @change="handleQuietHoursChange"
                />
              </a-form-item>
            </a-col>
            
            <a-col :span="12">
              <a-form-item label="免打扰结束时间">
                <a-time-picker 
                  v-model:value="quietHoursEnd" 
                  format="HH:mm"
                  @change="handleQuietHoursChange"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <!-- AI角色偏好 -->
      <div class="preference-section">
        <div class="section-header">
          <h3>AI角色偏好</h3>
          <p>设置您喜欢的AI角色类型和对话风格</p>
        </div>
        
        <a-form layout="vertical" class="preference-form">
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item label="偏好性格">
                <a-select 
                  v-model:value="preferences.aiCharacter.preferredPersonality" 
                  @change="handlePreferenceChange"
                >
                  <a-select-option value="FRIENDLY">友善</a-select-option>
                  <a-select-option value="SHY">害羞</a-select-option>
                  <a-select-option value="OUTGOING">外向</a-select-option>
                  <a-select-option value="MYSTERIOUS">神秘</a-select-option>
                  <a-select-option value="PLAYFUL">活泼</a-select-option>
                  <a-select-option value="SERIOUS">严肃</a-select-option>
                  <a-select-option value="ROMANTIC">浪漫</a-select-option>
                  <a-select-option value="INTELLECTUAL">理性</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            
            <a-col :span="12">
              <a-form-item label="偏好性别">
                <a-select 
                  v-model:value="preferences.aiCharacter.preferredGender" 
                  @change="handlePreferenceChange"
                >
                  <a-select-option value="MALE">男性</a-select-option>
                  <a-select-option value="FEMALE">女性</a-select-option>
                  <a-select-option value="OTHER">其他</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
          
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item label="对话风格">
                <a-select 
                  v-model:value="preferences.aiCharacter.conversationStyle" 
                  @change="handlePreferenceChange"
                >
                  <a-select-option value="formal">正式</a-select-option>
                  <a-select-option value="casual">随意</a-select-option>
                  <a-select-option value="playful">俏皮</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            
            <a-col :span="12">
              <a-form-item label="偏好年龄">
                <a-input-number 
                  v-model:value="preferences.aiCharacter.preferredAge" 
                  :min="18" 
                  :max="100"
                  @change="handlePreferenceChange"
                />
              </a-form-item>
            </a-col>
          </a-row>
          
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item label="响应速度">
                <a-slider 
                  v-model:value="preferences.aiCharacter.responseSpeed" 
                  :min="0.5" 
                  :max="2" 
                  :step="0.1"
                  @change="handlePreferenceChange"
                />
                <div class="slider-labels">
                  <span>慢</span>
                  <span>快</span>
                </div>
              </a-form-item>
            </a-col>
            
            <a-col :span="12">
              <a-form-item>
                <a-switch 
                  v-model:checked="preferences.aiCharacter.enableEmotionAnalysis"
                  @change="handlePreferenceChange"
                />
                <span class="switch-label">启用情感分析</span>
              </a-form-item>
              
              <a-form-item>
                <a-switch 
                  v-model:checked="preferences.aiCharacter.enableContextMemory"
                  @change="handlePreferenceChange"
                />
                <span class="switch-label">启用上下文记忆</span>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <!-- 隐私设置 -->
      <div class="preference-section">
        <div class="section-header">
          <h3>隐私设置</h3>
          <p>管理您的隐私和数据使用偏好</p>
        </div>
        
        <a-form layout="vertical" class="preference-form">
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item>
                <a-switch 
                  v-model:checked="preferences.privacy.profileVisible"
                  @change="handlePreferenceChange"
                />
                <span class="switch-label">公开个人资料</span>
              </a-form-item>
              
              <a-form-item>
                <a-switch 
                  v-model:checked="preferences.privacy.allowDataCollection"
                  @change="handlePreferenceChange"
                />
                <span class="switch-label">允许数据收集</span>
              </a-form-item>
            </a-col>
            
            <a-col :span="12">
              <a-form-item>
                <a-switch 
                  v-model:checked="preferences.privacy.shareUsageStatistics"
                  @change="handlePreferenceChange"
                />
                <span class="switch-label">分享使用统计</span>
              </a-form-item>
              
              <a-form-item>
                <a-switch 
                  v-model:checked="preferences.privacy.enableAnalytics"
                  @change="handlePreferenceChange"
                />
                <span class="switch-label">启用分析功能</span>
              </a-form-item>
            </a-col>
          </a-row>
          
          <a-form-item label="数据保留期限">
            <a-select 
              v-model:value="preferences.privacy.dataRetentionPeriod" 
              @change="handlePreferenceChange"
            >
              <a-select-option value="1month">1个月</a-select-option>
              <a-select-option value="3months">3个月</a-select-option>
              <a-select-option value="6months">6个月</a-select-option>
              <a-select-option value="1year">1年</a-select-option>
              <a-select-option value="forever">永久</a-select-option>
            </a-select>
          </a-form-item>
        </a-form>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <a-space>
        <a-button 
          type="primary" 
          @click="savePreferences" 
          :loading="saving"
          size="large"
        >
          保存设置
        </a-button>
        <a-button 
          @click="resetPreferences" 
          size="large"
        >
          恢复默认
        </a-button>
      </a-space>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { authApi } from '@/api/auth'
import type { UserPreferences } from '@/types/user'
import dayjs, { type Dayjs } from 'dayjs'

const saving = ref(false)

const preferences = reactive<UserPreferences>({
  theme: {
    colorScheme: 'light',
    primaryColor: '#1890ff',
    language: 'zh-CN',
    fontSize: 'medium'
  },
  notification: {
    emailNotification: true,
    pushNotification: true,
    soundEnabled: true,
    vibrationEnabled: true,
    quietHoursStart: '22:00',
    quietHoursEnd: '08:00'
  },
  aiCharacter: {
    preferredPersonality: 'FRIENDLY',
    preferredGender: 'FEMALE',
    preferredAge: 25,
    conversationStyle: 'casual',
    responseSpeed: 1.0,
    enableEmotionAnalysis: true,
    enableContextMemory: true
  },
  privacy: {
    profileVisible: true,
    allowDataCollection: true,
    shareUsageStatistics: false,
    enableAnalytics: true,
    dataRetentionPeriod: '1year'
  }
})

const quietHoursStart = computed({
  get: () => dayjs(preferences.notification.quietHoursStart, 'HH:mm'),
  set: (value: Dayjs | null) => {
    if (value) {
      preferences.notification.quietHoursStart = value.format('HH:mm')
    }
  }
})

const quietHoursEnd = computed({
  get: () => dayjs(preferences.notification.quietHoursEnd, 'HH:mm'),
  set: (value: Dayjs | null) => {
    if (value) {
      preferences.notification.quietHoursEnd = value.format('HH:mm')
    }
  }
})

onMounted(() => {
  loadPreferences()
})

const loadPreferences = async () => {
  try {
    const response = await authApi.getUserPreferences()
    if (response.success && response.data) {
      Object.assign(preferences, response.data)
    }
  } catch (error) {
    console.error('加载偏好设置失败:', error)
  }
}

const handlePreferenceChange = () => {
  // 可以在这里添加实时预览功能
}

const handleQuietHoursChange = () => {
  handlePreferenceChange()
}

const savePreferences = async () => {
  try {
    saving.value = true
    const response = await authApi.updateUserPreferences(preferences)
    if (response.success) {
      message.success('偏好设置保存成功')
    }
  } catch (error) {
    console.error('保存偏好设置失败:', error)
    message.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

const resetPreferences = async () => {
  try {
    const response = await authApi.resetUserPreferences()
    if (response.success && response.data) {
      Object.assign(preferences, response.data)
      message.success('偏好设置已恢复默认')
    }
  } catch (error) {
    console.error('重置偏好设置失败:', error)
    message.error('重置失败，请重试')
  }
}
</script>

<style scoped>
.user-preferences {
  max-width: 900px;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.page-header p {
  margin: 0;
  color: #8c8c8c;
  font-size: 14px;
}

.preferences-sections {
  display: flex;
  flex-direction: column;
  gap: 32px;
  margin-bottom: 32px;
}

.preference-section {
  background: #fafafa;
  padding: 24px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
}

.section-header {
  margin-bottom: 24px;
}

.section-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #262626;
}

.section-header p {
  margin: 0;
  color: #8c8c8c;
  font-size: 14px;
}

.preference-form {
  max-width: 100%;
}

.switch-label {
  margin-left: 8px;
  color: #262626;
}

.slider-labels {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 4px;
}

.action-buttons {
  text-align: center;
  padding: 24px 0;
  border-top: 1px solid #f0f0f0;
}

:deep(.ant-form-item-label) {
  font-weight: 500;
}

:deep(.ant-slider) {
  margin: 8px 0;
}
</style>
