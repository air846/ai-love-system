<template>
  <div class="character-list">
    <div class="page-header">
      <div class="header-content">
        <h1>AI角色管理</h1>
        <p>创建和管理您的AI角色，每个角色都有独特的个性和对话风格</p>
      </div>
      <div class="header-actions">
        <a-button type="primary" @click="goToCreate">
          <PlusOutlined />
          创建角色
        </a-button>
      </div>
    </div>

    <div class="character-grid">
      <div
        v-for="character in characters"
        :key="character.id"
        class="character-card"
        @click="selectCharacter(character)"
      >
        <div class="character-avatar">
          <a-avatar :size="64" :src="character.avatarUrl">
            {{ character.name.charAt(0) }}
          </a-avatar>
        </div>
        
        <div class="character-info">
          <h3 class="character-name">{{ character.name }}</h3>
          <p class="character-description">{{ character.description || '暂无描述' }}</p>
          
          <div class="character-tags">
            <a-tag color="blue">{{ character.personalityDescription }}</a-tag>
            <a-tag color="green">{{ character.genderDescription }}</a-tag>
            <a-tag v-if="character.age" color="orange">{{ character.age }}岁</a-tag>
          </div>
          
          <div class="character-stats">
            <span class="stat-item">
              <MessageOutlined />
              {{ character.usageCount }} 次对话
            </span>
          </div>
        </div>
        
        <div class="character-actions">
          <a-dropdown>
            <a-button type="text" @click.stop>
              <MoreOutlined />
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="editCharacter(character)">
                  <EditOutlined />
                  编辑
                </a-menu-item>
                <a-menu-item @click="testCharacter(character)">
                  <PlayCircleOutlined />
                  测试
                </a-menu-item>
                <a-menu-item @click="cloneCharacter(character)">
                  <CopyOutlined />
                  复制
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item @click="deleteCharacter(character)" danger>
                  <DeleteOutlined />
                  删除
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="characters.length === 0" class="empty-state">
        <RobotOutlined class="empty-icon" />
        <h3>还没有AI角色</h3>
        <p>创建您的第一个AI角色，开始智能对话之旅</p>
        <a-button type="primary" @click="goToCreate">
          <PlusOutlined />
          创建角色
        </a-button>
      </div>
    </div>

    <!-- 测试角色弹窗 -->
    <a-modal
      v-model:open="testModalVisible"
      title="测试角色"
      @ok="handleTest"
      :confirm-loading="testing"
      width="600px"
    >
      <div v-if="selectedCharacter" class="test-modal">
        <div class="test-character-info">
          <a-avatar :size="48" :src="selectedCharacter.avatarUrl">
            {{ selectedCharacter.name.charAt(0) }}
          </a-avatar>
          <div class="character-details">
            <h4>{{ selectedCharacter.name }}</h4>
            <p>{{ selectedCharacter.personalityDescription }} • {{ selectedCharacter.genderDescription }}</p>
          </div>
        </div>
        
        <a-form layout="vertical">
          <a-form-item label="测试消息">
            <a-textarea
              v-model:value="testMessage"
              placeholder="输入一条消息来测试角色的回复..."
              :rows="3"
              :maxlength="500"
              show-count
            />
          </a-form-item>
        </a-form>
        
        <div v-if="testResponse" class="test-response">
          <h5>AI回复：</h5>
          <div class="response-content">{{ testResponse }}</div>
        </div>
      </div>
    </a-modal>

    <!-- 复制角色弹窗 -->
    <a-modal
      v-model:open="cloneModalVisible"
      title="复制角色"
      @ok="handleClone"
      :confirm-loading="cloning"
    >
      <a-form layout="vertical">
        <a-form-item label="新角色名称" required>
          <a-input
            v-model:value="cloneName"
            placeholder="输入新角色的名称"
            :maxlength="50"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  PlusOutlined,
  MessageOutlined,
  MoreOutlined,
  EditOutlined,
  PlayCircleOutlined,
  CopyOutlined,
  DeleteOutlined,
  RobotOutlined
} from '@ant-design/icons-vue'
import { characterApi } from '@/api/character'
import type { CharacterResponse } from '@/types/character'

const router = useRouter()

// 响应式数据
const characters = ref<CharacterResponse[]>([])
const loading = ref(false)
const testModalVisible = ref(false)
const cloneModalVisible = ref(false)
const selectedCharacter = ref<CharacterResponse | null>(null)
const testMessage = ref('')
const testResponse = ref('')
const testing = ref(false)
const cloneName = ref('')
const cloning = ref(false)

// 方法
const loadCharacters = async () => {
  try {
    loading.value = true
    const response = await characterApi.getCharacters()
    characters.value = response.data || []
  } catch (error) {
    console.error('加载角色列表失败:', error)
    message.error('加载角色列表失败')
  } finally {
    loading.value = false
  }
}

const goToCreate = () => {
  router.push('/dashboard/characters/create')
}

const selectCharacter = (character: CharacterResponse) => {
  // 可以跳转到角色详情页或开始对话
  router.push(`/dashboard/chat?characterId=${character.id}`)
}

const editCharacter = (character: CharacterResponse) => {
  router.push(`/dashboard/characters/${character.id}/edit`)
}

const testCharacter = (character: CharacterResponse) => {
  selectedCharacter.value = character
  testMessage.value = ''
  testResponse.value = ''
  testModalVisible.value = true
}

const handleTest = async () => {
  if (!testMessage.value.trim() || !selectedCharacter.value) {
    message.error('请输入测试消息')
    return
  }

  try {
    testing.value = true
    const response = await characterApi.testCharacter(selectedCharacter.value.id, {
      message: testMessage.value
    })
    testResponse.value = response.data
  } catch (error) {
    console.error('测试角色失败:', error)
    message.error('测试角色失败')
  } finally {
    testing.value = false
  }
}

const cloneCharacter = (character: CharacterResponse) => {
  selectedCharacter.value = character
  cloneName.value = `${character.name}_副本`
  cloneModalVisible.value = true
}

const handleClone = async () => {
  if (!cloneName.value.trim() || !selectedCharacter.value) {
    message.error('请输入新角色名称')
    return
  }

  try {
    cloning.value = true
    await characterApi.cloneCharacter(selectedCharacter.value.id, {
      newName: cloneName.value
    })
    message.success('角色复制成功')
    cloneModalVisible.value = false
    loadCharacters()
  } catch (error) {
    console.error('复制角色失败:', error)
    message.error('复制角色失败')
  } finally {
    cloning.value = false
  }
}

const deleteCharacter = (character: CharacterResponse) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除角色"${character.name}"吗？此操作不可恢复。`,
    okText: '删除',
    okType: 'danger',
    cancelText: '取消',
    onOk: async () => {
      try {
        await characterApi.deleteCharacter(character.id)
        message.success('角色删除成功')
        loadCharacters()
      } catch (error) {
        console.error('删除角色失败:', error)
        message.error('删除角色失败')
      }
    }
  })
}

// 生命周期
onMounted(() => {
  loadCharacters()
})
</script>

<style scoped>
.character-list {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.header-content h1 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.header-content p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.character-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.character-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.character-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.character-avatar {
  text-align: center;
  margin-bottom: 16px;
}

.character-info {
  text-align: center;
}

.character-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.character-description {
  color: #666;
  font-size: 14px;
  margin-bottom: 16px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.character-tags {
  margin-bottom: 16px;
}

.character-tags .ant-tag {
  margin-bottom: 4px;
}

.character-stats {
  display: flex;
  justify-content: center;
  color: #999;
  font-size: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.character-actions {
  position: absolute;
  top: 16px;
  right: 16px;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 80px 20px;
  color: #666;
}

.empty-icon {
  font-size: 64px;
  color: #d9d9d9;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  margin-bottom: 8px;
  color: #333;
}

.empty-state p {
  margin-bottom: 24px;
}

.test-modal {
  padding: 16px 0;
}

.test-character-info {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
}

.character-details {
  margin-left: 16px;
}

.character-details h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
}

.character-details p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.test-response {
  margin-top: 16px;
  padding: 16px;
  background: #f0f9ff;
  border-radius: 8px;
  border-left: 4px solid #1890ff;
}

.test-response h5 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 14px;
  font-weight: 600;
}

.response-content {
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
}

@media (max-width: 768px) {
  .character-grid {
    grid-template-columns: 1fr;
  }
  
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
}
</style>
