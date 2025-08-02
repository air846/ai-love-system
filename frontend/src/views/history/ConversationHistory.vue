<template>
  <div class="conversation-history">
    <div class="page-header">
      <h1>对话历史</h1>
      <p>查看和管理您的所有对话记录</p>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-bar">
      <a-input-search
        v-model:value="searchKeyword"
        placeholder="搜索对话标题..."
        style="width: 300px"
        @search="handleSearch"
      />
      
      <div class="filter-controls">
        <a-select
          v-model:value="statusFilter"
          placeholder="状态筛选"
          style="width: 120px"
          @change="loadConversations"
        >
          <a-select-option value="">全部</a-select-option>
          <a-select-option value="ACTIVE">进行中</a-select-option>
          <a-select-option value="ARCHIVED">已归档</a-select-option>
          <a-select-option value="COMPLETED">已完成</a-select-option>
        </a-select>
        
        <a-button @click="showBatchActions = !showBatchActions">
          批量操作
        </a-button>
      </div>
    </div>

    <!-- 批量操作栏 -->
    <div v-if="showBatchActions" class="batch-actions">
      <a-checkbox
        :indeterminate="indeterminate"
        :checked="checkAll"
        @change="onCheckAllChange"
      >
        全选
      </a-checkbox>
      
      <div class="batch-buttons">
        <a-button
          :disabled="selectedConversations.length === 0"
          @click="batchArchive"
        >
          批量归档
        </a-button>
        <a-button
          danger
          :disabled="selectedConversations.length === 0"
          @click="batchDelete"
        >
          批量删除
        </a-button>
      </div>
    </div>

    <!-- 对话列表 -->
    <div class="conversation-list">
      <div
        v-for="conversation in conversations"
        :key="conversation.id"
        class="conversation-item"
        :class="{ selected: selectedConversations.includes(conversation.id) }"
      >
        <div v-if="showBatchActions" class="conversation-checkbox">
          <a-checkbox
            :checked="selectedConversations.includes(conversation.id)"
            @change="toggleConversationSelection(conversation.id)"
          />
        </div>
        
        <div class="conversation-avatar">
          <a-avatar :src="conversation.characterAvatar">
            {{ conversation.characterName?.charAt(0) }}
          </a-avatar>
        </div>
        
        <div class="conversation-content" @click="viewConversation(conversation)">
          <div class="conversation-header">
            <h3 class="conversation-title">{{ conversation.title }}</h3>
            <div class="conversation-meta">
              <a-tag :color="getStatusColor(conversation.status)">
                {{ getStatusText(conversation.status) }}
              </a-tag>
              <span class="conversation-time">
                {{ formatTime(conversation.lastMessageAt || conversation.createdAt) }}
              </span>
            </div>
          </div>
          
          <div class="conversation-info">
            <div class="character-info">
              <span class="character-name">{{ conversation.characterName }}</span>
              <span class="character-personality">{{ conversation.characterPersonality }}</span>
            </div>
            
            <div class="conversation-stats">
              <span class="message-count">
                <MessageOutlined />
                {{ conversation.messageCount }} 条消息
              </span>
            </div>
          </div>
        </div>
        
        <div class="conversation-actions">
          <a-dropdown>
            <a-button type="text">
              <MoreOutlined />
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="viewConversation(conversation)">
                  <EyeOutlined />
                  查看详情
                </a-menu-item>
                <a-menu-item @click="continueConversation(conversation)">
                  <MessageOutlined />
                  继续对话
                </a-menu-item>
                <a-menu-item @click="editConversation(conversation)">
                  <EditOutlined />
                  编辑标题
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item
                  v-if="conversation.status === 'ACTIVE'"
                  @click="archiveConversation(conversation)"
                >
                  <InboxOutlined />
                  归档
                </a-menu-item>
                <a-menu-item
                  v-if="conversation.status === 'ARCHIVED'"
                  @click="restoreConversation(conversation)"
                >
                  <RedoOutlined />
                  恢复
                </a-menu-item>
                <a-menu-item @click="deleteConversation(conversation)" danger>
                  <DeleteOutlined />
                  删除
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <a-pagination
        v-model:current="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :show-size-changer="true"
        :show-quick-jumper="true"
        :show-total="(total: number, range: [number, number]) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`"
        @change="loadConversations"
        @show-size-change="loadConversations"
      />
    </div>

    <!-- 对话详情弹窗 -->
    <a-modal
      v-model:open="detailModalVisible"
      title="对话详情"
      width="800px"
      :footer="null"
    >
      <div v-if="selectedConversation" class="conversation-detail">
        <div class="detail-header">
          <a-avatar :size="48" :src="selectedConversation.characterAvatar">
            {{ selectedConversation.characterName?.charAt(0) }}
          </a-avatar>
          <div class="detail-info">
            <h3>{{ selectedConversation.title }}</h3>
            <p>与 {{ selectedConversation.characterName }} 的对话</p>
          </div>
        </div>
        
        <div class="detail-stats">
          <div class="stat-item">
            <span class="stat-label">消息数量:</span>
            <span class="stat-value">{{ selectedConversation.messageCount }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">创建时间:</span>
            <span class="stat-value">{{ formatTime(selectedConversation.createdAt) }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">最后活跃:</span>
            <span class="stat-value">{{ formatTime(selectedConversation.lastMessageAt) }}</span>
          </div>
        </div>
        
        <div class="detail-actions">
          <a-button type="primary" @click="continueConversation(selectedConversation)">
            继续对话
          </a-button>
          <a-button @click="viewEmotionAnalysis(selectedConversation)">
            情感分析
          </a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  MessageOutlined,
  MoreOutlined,
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
  InboxOutlined,
  RedoOutlined
} from '@ant-design/icons-vue'
import { chatApi } from '@/api/chat'
import type { ConversationListResponse } from '@/types/chat'

const router = useRouter()

// 响应式数据
const conversations = ref<ConversationListResponse[]>([])
const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 批量操作
const showBatchActions = ref(false)
const selectedConversations = ref<number[]>([])

// 弹窗
const detailModalVisible = ref(false)
const selectedConversation = ref<ConversationListResponse | null>(null)

// 计算属性
const checkAll = computed(() => {
  return conversations.value.length > 0 && selectedConversations.value.length === conversations.value.length
})

const indeterminate = computed(() => {
  return selectedConversations.value.length > 0 && selectedConversations.value.length < conversations.value.length
})

// 方法
const loadConversations = async () => {
  try {
    loading.value = true
    const response = await chatApi.getConversations(currentPage.value - 1, pageSize.value)
    conversations.value = response.data.content || []
    total.value = response.data.content?.length || 0
  } catch (error) {
    console.error('加载对话列表失败:', error)
    message.error('加载对话列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  if (searchKeyword.value.trim()) {
    try {
      const response = await chatApi.searchConversations(searchKeyword.value, currentPage.value - 1, pageSize.value)
      conversations.value = response.data.content || []
      total.value = response.data.content?.length || 0
    } catch (error) {
      console.error('搜索对话失败:', error)
      message.error('搜索对话失败')
    }
  } else {
    loadConversations()
  }
}

const viewConversation = (conversation: ConversationListResponse) => {
  selectedConversation.value = conversation
  detailModalVisible.value = true
}

const continueConversation = (conversation: ConversationListResponse) => {
  router.push(`/dashboard/chat?conversationId=${conversation.id}`)
}

const editConversation = (conversation: ConversationListResponse) => {
  // TODO: 实现编辑对话标题功能
  message.info('编辑功能开发中')
}

const archiveConversation = async (conversation: ConversationListResponse) => {
  try {
    await chatApi.archiveConversation(conversation.id)
    message.success('对话已归档')
    loadConversations()
  } catch (error) {
    console.error('归档对话失败:', error)
    message.error('归档对话失败')
  }
}

const restoreConversation = async (conversation: ConversationListResponse) => {
  try {
    await chatApi.restoreConversation(conversation.id)
    message.success('对话已恢复')
    loadConversations()
  } catch (error) {
    console.error('恢复对话失败:', error)
    message.error('恢复对话失败')
  }
}

const deleteConversation = (conversation: ConversationListResponse) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除对话"${conversation.title}"吗？此操作不可恢复。`,
    okText: '删除',
    okType: 'danger',
    cancelText: '取消',
    onOk: async () => {
      try {
        await chatApi.deleteConversation(conversation.id)
        message.success('对话删除成功')
        loadConversations()
      } catch (error) {
        console.error('删除对话失败:', error)
        message.error('删除对话失败')
      }
    }
  })
}

const viewEmotionAnalysis = (conversation: ConversationListResponse) => {
  router.push(`/dashboard/emotions?conversationId=${conversation.id}`)
}

// 批量操作
const onCheckAllChange = (e: any) => {
  if (e.target.checked) {
    selectedConversations.value = conversations.value.map(c => c.id)
  } else {
    selectedConversations.value = []
  }
}

const toggleConversationSelection = (conversationId: number) => {
  const index = selectedConversations.value.indexOf(conversationId)
  if (index > -1) {
    selectedConversations.value.splice(index, 1)
  } else {
    selectedConversations.value.push(conversationId)
  }
}

const batchArchive = async () => {
  // TODO: 实现批量归档
  message.info('批量归档功能开发中')
}

const batchDelete = () => {
  Modal.confirm({
    title: '确认批量删除',
    content: `确定要删除选中的 ${selectedConversations.value.length} 个对话吗？此操作不可恢复。`,
    okText: '删除',
    okType: 'danger',
    cancelText: '取消',
    onOk: async () => {
      try {
        await chatApi.batchDeleteConversations(selectedConversations.value)
        message.success('批量删除成功')
        selectedConversations.value = []
        loadConversations()
      } catch (error) {
        console.error('批量删除失败:', error)
        message.error('批量删除失败')
      }
    }
  })
}

// 工具方法
const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    ACTIVE: 'green',
    ARCHIVED: 'orange',
    COMPLETED: 'blue',
    DELETED: 'red'
  }
  return colorMap[status] || 'default'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ACTIVE: '进行中',
    ARCHIVED: '已归档',
    COMPLETED: '已完成',
    DELETED: '已删除'
  }
  return textMap[status] || status
}

const formatTime = (time?: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  return date.toLocaleDateString()
}

// 生命周期
onMounted(() => {
  loadConversations()
})
</script>

<style scoped>
.conversation-history {
  padding: 24px;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-controls {
  display: flex;
  gap: 16px;
  align-items: center;
}

.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 16px;
}

.batch-buttons {
  display: flex;
  gap: 8px;
}

.conversation-list {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}

.conversation-item:hover {
  background-color: #f5f5f5;
}

.conversation-item.selected {
  background-color: #e6f7ff;
}

.conversation-checkbox {
  margin-right: 16px;
}

.conversation-avatar {
  margin-right: 16px;
}

.conversation-content {
  flex: 1;
  cursor: pointer;
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.conversation-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.conversation-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.conversation-time {
  color: #999;
  font-size: 12px;
}

.conversation-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.character-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.character-name {
  color: #666;
  font-size: 14px;
}

.character-personality {
  color: #999;
  font-size: 12px;
}

.conversation-stats {
  color: #999;
  font-size: 12px;
}

.message-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 24px;
}

.conversation-detail {
  padding: 16px 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-info h3 {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 600;
}

.detail-info p {
  margin: 0;
  color: #666;
}

.detail-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 6px;
}

.stat-label {
  color: #666;
}

.stat-value {
  font-weight: 500;
  color: #333;
}

.detail-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .filter-controls {
    justify-content: space-between;
  }
  
  .conversation-item {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .conversation-header {
    flex-direction: column;
    gap: 8px;
  }
  
  .detail-stats {
    grid-template-columns: 1fr;
  }
}
</style>
