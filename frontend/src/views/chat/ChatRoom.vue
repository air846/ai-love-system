<template>
  <div class="chat-room">
    <div class="chat-sidebar" :class="{ 'sidebar-expanded': sidebarExpanded }" 
         @mouseenter="expandSidebar" @mouseleave="collapseSidebar">
      <div class="sidebar-header">
        <h3>
          <HeartOutlined class="sidebar-icon" />
          <span class="sidebar-text">甜蜜对话</span>
        </h3>
        <a-button type="primary" class="create-btn" @click="showNewChatModal">
          <PlusOutlined />
          <span class="sidebar-text">新建对话</span>
        </a-button>
      </div>
      
      <div class="conversation-list">
        <div
          v-for="conversation in conversations"
          :key="conversation.id"
          class="conversation-item"
          :class="{ active: currentConversationId === conversation.id }"
          @click="selectConversation(conversation.id)"
        >
          <div class="conversation-avatar">
            <a-avatar :src="conversation.characterAvatar" size="small">
              {{ conversation.characterName?.charAt(0) }}
            </a-avatar>
            <div class="unread-badge" v-if="conversation.unreadCount">{{ conversation.unreadCount }}</div>
          </div>
          <div class="conversation-info">
            <div class="conversation-title">{{ conversation.title }}</div>
            <div class="conversation-preview">
               {{ conversation.characterName }} • {{ formatTime(conversation.lastMessageAt || '') }}
            </div>
            <div class="conversation-highlight" v-if="conversation.highlight">{{ conversation.highlight }}</div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="chat-main">
      <div v-if="!currentConversationId" class="chat-welcome">
        <div class="welcome-content">
          <div class="welcome-illustration">
            <img src="/images/love-illustration.svg" alt="爱心插画" />
          </div>
          <h2>欢迎来到甜蜜对话空间</h2>
          <p>在这里，与AI伙伴开启一段温暖治愈的对话之旅</p>
          <a-button type="primary" class="welcome-btn" @click="showNewChatModal">
            <HeartOutlined />
            开始一段新对话
          </a-button>
        </div>
      </div>
      
      <div v-else class="chat-container">
        <div class="chat-header">
          <div class="chat-info">
            <a-avatar :src="currentCharacter?.avatarUrl" class="character-avatar">
              {{ currentCharacter?.name?.charAt(0) }}
            </a-avatar>
            <div class="character-info">
              <div class="character-name">{{ currentCharacter?.name }}</div>
              <div class="character-status">
                <span class="status-dot"></span>
                在线
              </div>
            </div>
          </div>
          
          <div class="chat-actions">
            <a-button type="text" class="action-btn" @click="showEmotionAnalysis">
              <HeartOutlined />
            </a-button>
            <a-button type="text" class="action-btn" @click="showConversationSettings">
              <SettingOutlined />
            </a-button>
          </div>
        </div>
        
        <div class="message-list" ref="messageListRef">
          <div class="message-date-divider">{{ formatDate(currentDate) }}</div>
          <div v-if="messages.length === 0" style="text-align: center; color: #999; padding: 20px;">
            暂无消息
          </div>
          <div
            v-for="(message, index) in messages"
            :key="`message-${message.id}-${index}`"
            class="message-item"
            :class="{
              'message-user': message.senderType === 'USER',
              'message-ai': message.senderType === 'AI'
            }"
          >
            <a-avatar
              v-if="message.senderType === 'AI'"
              :src="currentCharacter?.avatarUrl"
              size="small"
              class="message-avatar"
            >
              {{ currentCharacter?.name?.charAt(0) }}
            </a-avatar>
            
            <div class="message-content">
              <div class="message-text" :class="getEmotionClass(message)">
                {{ message.content }}
                <div class="message-tail"></div>
              </div>
              <div class="message-time">{{ formatTime(message.createdAt) }}</div>
            </div>
            
            <a-avatar
              v-if="message.senderType === 'USER'"
              size="small"
              class="message-avatar user-avatar"
              :title="`用户: ${userStore.user?.username || '未知用户'}`"
            >
              {{ userStore.user?.username?.charAt(0) || 'U' }}
            </a-avatar>
          </div>
          <div class="typing-indicator" v-if="sending">
            <div class="typing-dot"></div>
            <div class="typing-dot"></div>
            <div class="typing-dot"></div>
          </div>
        </div>
        
        <div class="message-input">
          <a-input
            v-model:value="inputMessage"
            placeholder="说点什么吧..."
            @press-enter="sendMessage"
            :disabled="sending"
            class="input-field"
          >
            <template #prefix>
              <SmileOutlined class="input-icon" @click="showEmojiPicker" />
            </template>
            <template #suffix>
              <a-button
                type="primary"
                shape="circle"
                @click="sendMessage"
                :loading="sending"
                :disabled="!inputMessage.trim()"
                class="send-button"
              >
                <SendOutlined />
              </a-button>
            </template>
          </a-input>
        </div>
      </div>
    </div>
    
    <!-- 新建对话弹窗 -->
    <a-modal
      v-model:open="newChatModalVisible"
      title="开启新的对话"
      @ok="createConversation"
      :confirm-loading="creating"
      class="custom-modal"
    >
      <div class="modal-illustration">
        <img src="/images/new-chat-illustration.svg" alt="新对话插画" />
      </div>
      <a-form layout="vertical">
        <a-form-item label="选择你的AI伙伴">
          <a-select
            v-model:value="newChatForm.characterId"
            placeholder="请选择一位AI伙伴"
            :options="characterOptions"
            class="custom-select"
          >
            <template #option="{ label, value }">
              <div class="character-option">
                <a-avatar :src="getCharacterAvatar(value)" size="small"></a-avatar>
                <span>{{ label }}</span>
              </div>
            </template>
          </a-select>
        </a-form-item>
        
        <a-form-item label="给这段对话起个名字">
          <a-input
            v-model:value="newChatForm.title"
            placeholder="例如：初次相遇、午后闲聊..."
            class="custom-input"
          />
        </a-form-item>
      </a-form>
    </a-modal>
    
    <!-- 情感分析弹窗 -->
    <a-modal
      v-model:open="emotionModalVisible"
      title="情感分析"
      footer={null}
      class="emotion-modal"
    >
      <div class="emotion-visualization">
        <div class="emotion-plant" :style="{ height: `${emotionHealth}%` }">
          <div class="emotion-flower" :class="emotionFlowerClass"></div>
          <div class="emotion-stem"></div>
        </div>
        <div class="emotion-stats">
          <div class="emotion-stat">
            <div class="stat-label">积极情绪</div>
            <div class="stat-bar">
              <div class="stat-fill positive" :style="{ width: `${emotionStats.positive}%` }"></div>
            </div>
            <div class="stat-value">{{ emotionStats.positive }}%</div>
          </div>
          <div class="emotion-stat">
            <div class="stat-label">消极情绪</div>
            <div class="stat-bar">
              <div class="stat-fill negative" :style="{ width: `${emotionStats.negative}%` }"></div>
            </div>
            <div class="stat-value">{{ emotionStats.negative }}%</div>
          </div>
          <div class="emotion-stat">
            <div class="stat-label">中性情绪</div>
            <div class="stat-bar">
              <div class="stat-fill neutral" :style="{ width: `${emotionStats.neutral}%` }"></div>
            </div>
            <div class="stat-value">{{ emotionStats.neutral }}%</div>
          </div>
        </div>
      </div>
    </a-modal>

    <!-- 对话设置弹窗 -->
    <ConversationSettings
      v-model="settingsModalVisible"
      :conversation-id="currentConversationId || undefined"
      @updated="handleSettingsUpdated"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick, watch } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  HeartOutlined,
  SettingOutlined,
  SendOutlined,
  SmileOutlined,
  CloudOutlined,
  MailOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'
import { chatApi } from '@/api/chat'
import { characterApi } from '@/api/character'
import ConversationSettings from '@/components/ConversationSettings.vue'
import type { ConversationListResponse, MessageResponse, ConversationSettingsResponse } from '@/types/chat'
import type { CharacterResponse } from '@/types/character'

const userStore = useUserStore()

// 响应式数据
const conversations = ref<ConversationListResponse[]>([])
const messages = ref<MessageResponse[]>([])
const characters = ref<CharacterResponse[]>([])
const currentConversationId = ref<number | null>(null)
const inputMessage = ref('')
const sending = ref(false)
const newChatModalVisible = ref(false)
const emotionModalVisible = ref(false)
const settingsModalVisible = ref(false)
const creating = ref(false)
const messageListRef = ref<HTMLElement>()
const sidebarExpanded = ref(true)
const currentDate = ref(new Date())

const newChatForm = reactive({
  characterId: null as number | null,
  title: ''
})

// 情感分析数据
const emotionStats = reactive({
  positive: 65,
  negative: 15,
  neutral: 20
})

const emotionHealth = computed(() => {
  return emotionStats.positive - emotionStats.negative + 50
})

const emotionFlowerClass = computed(() => {
  if (emotionHealth.value > 70) return 'flower-blooming'
  if (emotionHealth.value > 40) return 'flower-growing'
  return 'flower-wilting'
})

// 计算属性
const currentCharacter = computed(() => {
  const conversation = conversations.value.find(c => c.id === currentConversationId.value)
  return conversation ? {
    id: conversation.characterId,
    name: conversation.characterName,
    avatarUrl: conversation.characterAvatar
  } : null
})

const characterOptions = computed(() => {
  return characters.value.map(char => ({
    label: char.name,
    value: char.id
  }))
})

// 方法
const loadConversations = async () => {
  try {
    const response = await chatApi.getConversations()
    conversations.value = response.data.content || []

    // 为每个对话添加高亮语句和未读数量（模拟数据）
    conversations.value = conversations.value.map(conv => ({
      ...conv,
      highlight: Math.random() > 0.7 ? getRandomHighlight() : '',
      unreadCount: Math.random() > 0.7 ? Math.floor(Math.random() * 5) + 1 : 0
    }))

    // 自动选择第一个对话
    if (conversations.value.length > 0 && !currentConversationId.value) {
      await selectConversation(conversations.value[0].id)
    }
  } catch (error) {
    console.error('加载对话列表失败:', error)
  }
}

const getRandomHighlight = () => {
  const highlights = [
    '今天天气真好，要不要出去走走？',
    '我一直在想你呢...',
    '你知道吗？我学会了一首新歌',
    '希望你今天过得开心！',
    '我有个小秘密想告诉你'
  ]
  return highlights[Math.floor(Math.random() * highlights.length)]
}

const loadCharacters = async () => {
  try {
    const response = await characterApi.getCharacters()
    characters.value = response.data || []
  } catch (error) {
    console.error('加载角色列表失败:', error)
  }
}

const getCharacterAvatar = (characterId: number) => {
  const character = characters.value.find(c => c.id === characterId)
  return character?.avatarUrl || ''
}

const selectConversation = async (conversationId: number) => {
  currentConversationId.value = conversationId
  await loadMessages(conversationId)
  
  // 清除未读消息计数
  conversations.value = conversations.value.map(conv => {
    if (conv.id === conversationId) {
      return { ...conv, unreadCount: 0 }
    }
    return conv
  })
}

const loadMessages = async (conversationId: number) => {
  try {
    const response = await chatApi.getMessages(conversationId)
    messages.value = response.data || []
    console.log('加载的消息:', messages.value)
    await nextTick()
    scrollToBottom()
  } catch (error) {
    console.error('加载消息失败:', error)
  }
}

const getEmotionClass = (message: MessageResponse) => {
  // 根据消息内容分析情感（简单模拟）
  const content = message.content.toLowerCase()
  if (content.includes('喜欢') || content.includes('开心') || content.includes('好')) {
    return 'emotion-positive'
  }
  if (content.includes('难过') || content.includes('伤心') || content.includes('不')) {
    return 'emotion-negative'
  }
  return ''
}

  const sendMessage = async () => {
    if (!inputMessage.value.trim() || !currentConversationId.value) return
    
    try {
      sending.value = true
      const content = inputMessage.value.trim()
      inputMessage.value = ''
      
      // 创建用户消息对象并直接添加到界面
      const tempUserMessage = {
        id: Date.now(), // 使用时间戳作为临时ID
        content: content,
        senderType: 'USER' as const,
        messageType: 'TEXT',
        createdAt: new Date().toISOString(),
        conversationId: currentConversationId.value
      }
      
      // 直接添加用户消息到消息列表
      messages.value.push(tempUserMessage)
      console.log('添加用户消息:', content, '消息数组长度:', messages.value.length)

      // 滚动到底部显示用户消息
      await nextTick()
      scrollToBottom()
      
      // 发送消息到服务器
      console.log('发送消息到服务器:', content)
      const response = await chatApi.sendMessage(currentConversationId.value, { content })
      console.log('服务器响应:', response)

      // 处理服务器返回的消息
      if (response.data && response.data.length > 0) {
        console.log('服务器返回的消息数组:', response.data)

        // 找到服务器返回的用户消息和AI回复消息
        const serverUserMsg = response.data.find(msg => msg.senderType === 'USER')
        const aiMsg = response.data.find(msg => msg.senderType === 'AI')

        console.log('服务器用户消息:', serverUserMsg)
        console.log('AI回复消息:', aiMsg)

        // 如果找到了服务器返回的用户消息，更新临时用户消息的ID
        if (serverUserMsg) {
          const tempIndex = messages.value.findIndex(msg => msg.id === tempUserMessage.id)
          console.log('临时消息索引:', tempIndex, '临时消息ID:', tempUserMessage.id)
          if (tempIndex !== -1) {
            // 保持用户消息显示，只更新ID和其他服务器字段
            messages.value[tempIndex] = {
              ...tempUserMessage,
              id: serverUserMsg.id,
              createdAt: serverUserMsg.createdAt
            }
            console.log('更新用户消息成功')
          }
        } else {
          // 如果服务器没有返回用户消息，保持临时消息显示
          console.log('服务器未返回用户消息，保持临时消息显示')
        }

        // 如果找到了AI回复消息，添加到消息列表
        if (aiMsg) {
          messages.value.push(aiMsg)
          console.log('收到AI回复:', aiMsg.content)

          // 播放接收消息的声音效果
          playSound('message-received')

          // 更新情感分析数据
          updateEmotionStats(content)
        }
      } else {
        console.log('服务器没有返回消息数据，保持用户消息显示')
      }
      
      // 最终滚动到底部显示所有消息
      await nextTick()
      scrollToBottom()
    } catch (error) {
      console.error('发送消息失败:', error)
      
      // 根据错误类型显示不同的提示信息
      let errorMessage = '发送消息失败'
      if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
        errorMessage = 'AI正在思考中，请稍后重试或尝试简化问题'
      } else if (error.response?.status === 401) {
        errorMessage = '登录已过期，请重新登录'
      } else if (error.response?.status >= 500) {
        errorMessage = '服务器暂时不可用，请稍后重试'
      } else {
        errorMessage = error.response?.data?.message || error.message || '发送消息失败'
      }
      
      message.error(errorMessage)
      
      // 发送失败时，移除临时用户消息
      const tempIndex = messages.value.findIndex(msg => msg.content === inputMessage.value.trim() && msg.senderType === 'USER')
      if (tempIndex !== -1) {
        messages.value.splice(tempIndex, 1)
      }
    } finally {
      sending.value = false
    }
  }

const playSound = (soundType: string) => {
  try {
    // 检查音频文件是否存在，如果不存在则不播放
    // 这里我们只是记录一下，不实际播放声音，避免控制台错误
    console.log(`尝试播放声音: ${soundType}`)
    // const audio = new Audio(`/sounds/${soundType}.mp3`)
    // audio.volume = 0.5
    // audio.play()
  } catch (error) {
    console.error('播放声音失败:', error)
  }
}

const updateEmotionStats = (content: string) => {
  // 简单的情感分析模拟
  const isPositive = /喜欢|开心|好|爱|棒|赞|笑/.test(content)
  const isNegative = /难过|伤心|不|讨厌|烦|哭|生气/.test(content)
  
  if (isPositive) {
    emotionStats.positive = Math.min(100, emotionStats.positive + Math.random() * 5)
    emotionStats.negative = Math.max(0, emotionStats.negative - Math.random() * 3)
  } else if (isNegative) {
    emotionStats.negative = Math.min(100, emotionStats.negative + Math.random() * 5)
    emotionStats.positive = Math.max(0, emotionStats.positive - Math.random() * 3)
  } else {
    emotionStats.neutral = Math.min(100, emotionStats.neutral + Math.random() * 2)
  }
  
  // 确保总和为100%
  const total = emotionStats.positive + emotionStats.negative + emotionStats.neutral
  emotionStats.positive = Math.round((emotionStats.positive / total) * 100)
  emotionStats.negative = Math.round((emotionStats.negative / total) * 100)
  emotionStats.neutral = 100 - emotionStats.positive - emotionStats.negative
}

const showNewChatModal = () => {
  newChatModalVisible.value = true
  newChatForm.characterId = null
  newChatForm.title = ''
}

const showEmotionAnalysis = () => {
  emotionModalVisible.value = true
}

const showEmojiPicker = () => {
  message.info('表情选择器功能开发中')
}

const expandSidebar = () => {
  sidebarExpanded.value = true
}

const collapseSidebar = () => {
  sidebarExpanded.value = false
}

const createConversation = async () => {
  if (!newChatForm.characterId) {
    message.error('请选择AI角色')
    return
  }
  
  try {
    creating.value = true
    const response = await chatApi.createConversation({
      characterId: newChatForm.characterId,
      title: newChatForm.title || `与${getCharacterName(newChatForm.characterId)}的对话`
    })
    
    conversations.value.unshift(response.data)
    currentConversationId.value = response.data.id
    messages.value = []
    newChatModalVisible.value = false
    
    // 播放创建对话的声音效果
    playSound('conversation-created')
    
    message.success('对话创建成功')
  } catch (error) {
    console.error('创建对话失败:', error)
    message.error('创建对话失败')
  } finally {
    creating.value = false
  }
}

const getCharacterName = (characterId: number | null) => {
  if (!characterId) return ''
  const character = characters.value.find(c => c.id === characterId)
  return character?.name || '未知角色'
}

const showConversationSettings = () => {
  if (!currentConversationId.value) {
    message.warning('请先选择一个对话')
    return
  }
  settingsModalVisible.value = true
}

const handleSettingsUpdated = (settings: ConversationSettingsResponse) => {
  // 更新当前对话的标题
  const conversation = conversations.value.find(c => c.id === currentConversationId.value)
  if (conversation) {
    conversation.title = settings.title
  }
  message.success('对话设置已更新')
}

const scrollToBottom = () => {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

const formatDate = (date: Date) => {
  const today = new Date()
  const yesterday = new Date()
  yesterday.setDate(today.getDate() - 1)
  
  if (date.toDateString() === today.toDateString()) {
    return '今天'
  } else if (date.toDateString() === yesterday.toDateString()) {
    return '昨天'
  } else {
    return date.toLocaleDateString()
  }
}

// 监听消息变化
watch(messages, (newMessages) => {
  console.log('消息列表变化:', newMessages)
}, { deep: true })

// 监听用户信息变化
watch(() => userStore.user, (newUser) => {
  console.log('用户信息变化:', newUser)
}, { deep: true })

// 生命周期
onMounted(async () => {
  await userStore.initUser()
  loadConversations()
  loadCharacters()
})
</script>

<style scoped>
/* 全局样式变量 */
:root {
  --color-primary: #FF9FB2; /* 蜜桃粉 */
  --color-secondary: #A5D7E8; /* 薄荷绿 */
  --color-accent: #FFD89C; /* 奶油黄 */
  --color-text: #5D5D5D;
  --color-background: #FFFBF5;
  --color-card: #FFFFFF;
  --color-border: #F0F0F0;
  --border-radius: 16px;
  --shadow-soft: 0 8px 24px rgba(149, 157, 165, 0.1);
  --transition-normal: all 0.3s ease;
}

/* 主容器样式 */
.chat-room {
  display: flex;
  height: calc(100vh - 112px);
  background: var(--color-background);
  border-radius: var(--border-radius);
  overflow: hidden;
  box-shadow: var(--shadow-soft);
  font-family: 'Nunito', 'PingFang SC', sans-serif;
}

/* 侧边栏样式 */
.chat-sidebar {
  width: 280px;
  border-right: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  background: linear-gradient(to bottom, #FFF5F7, #F9FAFE);
  transition: width 0.3s ease;
  position: relative;
}

.sidebar-expanded {
  width: 280px;
}

.chat-sidebar:not(.sidebar-expanded) {
  width: 80px;
}

.chat-sidebar:not(.sidebar-expanded) .sidebar-text {
  display: none;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid var(--color-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary);
  display: flex;
  align-items: center;
}

.sidebar-icon {
  font-size: 20px;
  margin-right: 8px;
  color: var(--color-primary);
}

.create-btn {
  background: var(--color-primary);
  border: none;
  border-radius: 20px;
  color: white;
  font-weight: 600; /* 增强字重 */
  box-shadow: 0 4px 12px rgba(255, 159, 178, 0.3);
  transition: transform 0.2s;
  /* 增强白色文字阴影，提高可读性 */
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3), 0 1px 2px rgba(0, 0, 0, 0.5);
  /* 添加文字描边效果 */
  -webkit-text-stroke: 0.5px rgba(0, 0, 0, 0.2);
}

.create-btn:hover {
  transform: translateY(-2px);
  background: var(--color-primary);
  opacity: 0.9;
}

/* 对话列表样式 */
.conversation-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.conversation-list::-webkit-scrollbar {
  width: 6px;
}

.conversation-list::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: var(--transition-normal);
  border-radius: var(--border-radius);
  background: var(--color-card);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
  position: relative;
}

.conversation-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('/images/paper-texture.png');
  opacity: 0.05;
  border-radius: var(--border-radius);
  pointer-events: none;
}

.conversation-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.conversation-item.active {
  background: linear-gradient(135deg, #FFE8ED, #E8F4FF);
  border-right: 3px solid var(--color-primary);
}

.conversation-avatar {
  position: relative;
}

.unread-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: var(--color-primary);
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

.conversation-info {
  margin-left: 12px;
  flex: 1;
  min-width: 0;
}

.conversation-title {
  font-weight: 600;
  color: var(--color-text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conversation-preview {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.conversation-highlight {
  font-size: 12px;
  color: var(--color-primary);
  margin-top: 4px;
  font-style: italic;
  display: none;
}

.conversation-item:hover .conversation-highlight {
  display: block;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* 主聊天区域样式 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: var(--color-background);
}

/* 欢迎页样式 */
.chat-welcome {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FFF9FA, #F5FAFF);
}

.welcome-content {
  text-align: center;
  color: var(--color-text);
  max-width: 500px;
  padding: 40px;
}

.welcome-illustration {
  margin-bottom: 24px;
}

.welcome-illustration img {
  width: 200px;
  height: auto;
}

.welcome-content h2 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 16px;
  background: linear-gradient(45deg, var(--color-primary), #7B68EE);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.welcome-content p {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 24px;
}

.welcome-btn {
  background: var(--color-primary);
  border: none;
  border-radius: 24px;
  height: 48px;
  padding: 0 32px;
  font-size: 16px;
  font-weight: 600; /* 增强字重 */
  color: white;
  box-shadow: 0 4px 12px rgba(255, 159, 178, 0.3);
  transition: transform 0.2s;
  /* 增强白色文字阴影，提高可读性 */
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3), 0 1px 2px rgba(0, 0, 0, 0.5);
  /* 添加文字描边效果 */
  -webkit-text-stroke: 0.5px rgba(0, 0, 0, 0.2);
}

.welcome-btn:hover {
  transform: translateY(-2px);
  background: var(--color-primary);
  opacity: 0.9;
}

/* 聊天容器样式 */
.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 聊天头部样式 */
.chat-header {
  padding: 16px 24px;
  border-bottom: 1px solid var(--color-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
}

.chat-info {
  display: flex;
  align-items: center;
}

.character-avatar {
  border: 2px solid var(--color-primary);
  box-shadow: 0 2px 8px rgba(255, 159, 178, 0.2);
}

.character-info {
  margin-left: 12px;
}

.character-name {
  font-weight: 600;
  color: var(--color-text);
  font-size: 16px;
}

.character-status {
  font-size: 12px;
  color: #52c41a;
  display: flex;
  align-items: center;
}

.status-dot {
  width: 8px;
  height: 8px;
  background-color: #52c41a;
  border-radius: 50%;
  margin-right: 6px;
  display: inline-block;
  animation: blink 2s infinite;
}

@keyframes blink {
  0% { opacity: 0.5; }
  50% { opacity: 1; }
  100% { opacity: 0.5; }
}

.chat-actions {
  display: flex;
}

.action-btn {
  margin-left: 8px;
  color: var(--color-primary);
}

/* 消息列表样式 */
.chat-room .message-list {
  flex: 1 !important;
  padding: 20px 24px !important;
  overflow-y: auto !important;
  background: var(--color-background) !important;
  position: relative !important;
}

.message-list::-webkit-scrollbar {
  width: 6px;
}

.message-list::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

.message-date-divider {
  text-align: center;
  color: #999;
  font-size: 12px;
  margin: 16px 0;
  position: relative;
}

.message-date-divider::before,
.message-date-divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 30%;
  height: 1px;
  background: var(--color-border);
}

.message-date-divider::before {
  left: 0;
}

.message-date-divider::after {
  right: 0;
}

.chat-room .message-item {
  display: flex !important;
  margin-bottom: 16px !important;
  position: relative !important;
  max-width: 80% !important;
}

.chat-room .message-user {
  margin-left: auto !important;
  flex-direction: row-reverse !important;
}

.message-avatar {
  margin: 0 8px;
  align-self: flex-end;
}

.user-avatar {
  background: var(--color-primary);
  color: white;
}

.chat-room .message-content {
  display: flex !important;
  flex-direction: column !important;
}

.chat-room .message-text {
  padding: 12px 16px !important;
  border-radius: 18px !important;
  position: relative !important;
  max-width: 100% !important;
  word-break: break-word !important;
  line-height: 1.5 !important;
}

.chat-room .message-user .message-text {
  background: #FF9FB2 !important;
  color: white !important;
  border-bottom-right-radius: 4px !important;
}

.message-ai .message-text {
  background: white;
  color: var(--color-text);
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.message-tail {
  position: absolute;
  bottom: 0;
  width: 10px;
  height: 10px;
}

.message-user .message-tail {
  right: -5px;
  background: var(--color-primary);
  clip-path: polygon(0 0, 0% 100%, 100% 100%);
}

.message-ai .message-tail {
  left: -5px;
  background: white;
  clip-path: polygon(0 100%, 100% 0, 100% 100%);
}

.message-time {
  font-size: 10px;
  color: #999;
  margin-top: 4px;
  align-self: flex-end;
}

.message-user .message-time {
  margin-right: 8px;
}

.message-ai .message-time {
  margin-left: 8px;
}

/* 情感样式 */
.emotion-positive {
  background: linear-gradient(135deg, var(--color-primary), #FF7A8A) !important;
}

.emotion-negative {
  background: linear-gradient(135deg, #A5A5A5, #7D7D7D) !important;
}

/* 输入框样式 */
.message-input {
  padding: 16px 24px;
  border-top: 1px solid var(--color-border);
  background: white;
}

.input-field {
  border-radius: 24px;
  padding: 8px 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.input-icon {
  color: #999;
  cursor: pointer;
  transition: color 0.2s;
}

.input-icon:hover {
  color: var(--color-primary);
}

.send-button {
  background: var(--color-primary);
  border: none;
  box-shadow: 0 2px 8px rgba(255, 159, 178, 0.3);
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: white;
  border-radius: 18px;
  width: fit-content;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.typing-dot {
  width: 8px;
  height: 8px;
  background: var(--color-primary);
  border-radius: 50%;
  margin: 0 2px;
  animation: typingAnimation 1.5s infinite ease-in-out;
}

.typing-dot:nth-child(1) {
  animation-delay: 0s;
}

.typing-dot:nth-child(2) {
  animation-delay: 0.3s;
}

.typing-dot:nth-child(3) {
  animation-delay: 0.6s;
}

@keyframes typingAnimation {
  0% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
  100% { transform: translateY(0); }
}

/* 模态框样式 */
.custom-modal .modal-illustration {
  text-align: center;
  margin-bottom: 24px;
}

.custom-modal .modal-illustration img {
  width: 150px;
  height: auto;
}

.custom-select, .custom-input {
  border-radius: 8px;
}

.character-option {
  display: flex;
  align-items: center;
}

.character-option span {
  margin-left: 8px;
}

/* 情感分析模态框 */
.emotion-modal .emotion-visualization {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 20px 0;
}

.emotion-plant {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  width: 120px;
  height: 200px;
}

.emotion-flower {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  position: relative;
  transition: all 0.5s ease;
}

.flower-blooming {
  background: radial-gradient(circle, var(--color-primary) 0%, #FF7A8A 100%);
  box-shadow: 0 0 20px rgba(255, 159, 178, 0.5);
  transform: scale(1.2);
}

.flower-growing {
  background: radial-gradient(circle, var(--color-primary) 0%, #FFA5B5 100%);
  box-shadow: 0 0 10px rgba(255, 159, 178, 0.3);
}

.flower-wilting {
  background: radial-gradient(circle, #D3D3D3 0%, #A5A5A5 100%);
  box-shadow: none;
  transform: scale(0.8);
}

.emotion-stem {
  width: 6px;
  height: 100px;
  background: linear-gradient(to bottom, #A5D7E8, #7BBEDF);
  margin-top: -5px;
}

.emotion-stats {
  width: 200px;
}

.emotion-stat {
  margin-bottom: 16px;
}

.stat-label {
  font-size: 14px;
  margin-bottom: 4px;
}

.stat-bar {
  height: 8px;
  background: #F0F0F0;
  border-radius: 4px;
  overflow: hidden;
}

.stat-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.5s ease;
}

.stat-fill.positive {
  background: linear-gradient(to right, var(--color-primary), #FF7A8A);
}

.stat-fill.negative {
  background: linear-gradient(to right, #A5A5A5, #7D7D7D);
}

.stat-fill.neutral {
  background: linear-gradient(to right, var(--color-secondary), #7BBEDF);
}

.stat-value {
  font-size: 12px;
  text-align: right;
  margin-top: 2px;
}
</style>
