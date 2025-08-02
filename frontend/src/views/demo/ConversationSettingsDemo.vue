<template>
  <div class="demo-container">
    <div class="demo-header">
      <h1>对话设置功能演示</h1>
      <p>展示完整的对话设置界面和功能</p>
    </div>

    <div class="demo-content">
      <a-card title="功能演示" class="demo-card">
        <a-button type="primary" @click="showSettings = true">
          <SettingOutlined />
          打开对话设置
        </a-button>
        
        <div class="demo-info">
          <h3>功能特性：</h3>
          <ul>
            <li>✅ 基本信息设置（标题、描述、语言偏好、回复风格）</li>
            <li>✅ AI参数调整（模型选择、温度参数、令牌数、上下文长度）</li>
            <li>✅ 偏好设置（自动保存、消息通知）</li>
            <li>✅ 完整的表单验证和用户体验优化</li>
            <li>✅ 响应式设计，支持不同屏幕尺寸</li>
          </ul>
        </div>
      </a-card>
    </div>

    <!-- 对话设置组件 -->
    <ConversationSettings
      v-model="showSettings"
      :conversation-id="demoConversationId"
      @updated="handleSettingsUpdated"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { SettingOutlined } from '@ant-design/icons-vue'
import ConversationSettings from '@/components/ConversationSettings.vue'
import type { ConversationSettingsResponse } from '@/types/chat'

// 响应式数据
const showSettings = ref(false)
const demoConversationId = ref(1) // 演示用的对话ID

// 处理设置更新
const handleSettingsUpdated = (settings: ConversationSettingsResponse) => {
  message.success('对话设置已更新（演示模式）')
  console.log('更新的设置:', settings)
}
</script>

<style scoped>
.demo-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.demo-header {
  text-align: center;
  margin-bottom: 32px;
}

.demo-header h1 {
  color: #1890ff;
  margin-bottom: 8px;
}

.demo-header p {
  color: #666;
  font-size: 16px;
}

.demo-content {
  display: flex;
  justify-content: center;
}

.demo-card {
  width: 100%;
  max-width: 600px;
}

.demo-info {
  margin-top: 24px;
}

.demo-info h3 {
  color: #333;
  margin-bottom: 12px;
}

.demo-info ul {
  list-style: none;
  padding: 0;
}

.demo-info li {
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  color: #555;
}

.demo-info li:last-child {
  border-bottom: none;
}
</style>
