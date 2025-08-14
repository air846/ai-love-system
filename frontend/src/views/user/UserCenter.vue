<template>
  <div class="user-center">
    <div class="user-center-header">
      <div class="user-info">
        <div class="avatar-section">
          <a-avatar 
            :size="80" 
            :src="avatarUrl" 
            class="user-avatar"
          >
            <template #icon>
              <UserOutlined />
            </template>
          </a-avatar>
          <a-button 
            type="link" 
            size="small" 
            @click="showAvatarUpload = true"
            class="change-avatar-btn"
          >
            更换头像
          </a-button>
        </div>
        
        <div class="user-details">
          <h2 class="username">{{ userStore.user?.nickname || userStore.user?.username }}</h2>
          <p class="user-email">{{ userStore.user?.email }}</p>
          <div class="user-stats">
            <a-statistic 
              title="登录次数" 
              :value="userStore.user?.loginCount || 0" 
              class="stat-item"
            />
            <a-statistic 
              title="最后登录" 
              :value="formatLastLogin(userStore.user?.lastLoginAt)" 
              class="stat-item"
            />
          </div>
        </div>
      </div>
    </div>

    <div class="user-center-content">
      <a-row :gutter="24">
        <a-col :span="6">
          <div class="sidebar">
            <a-menu 
              v-model:selectedKeys="selectedKeys" 
              mode="vertical"
              @click="handleMenuClick"
              class="user-menu"
            >
              <a-menu-item key="profile">
                <template #icon>
                  <UserOutlined />
                </template>
                个人资料
              </a-menu-item>
              <a-menu-item key="account">
                <template #icon>
                  <SettingOutlined />
                </template>
                账户设置
              </a-menu-item>
              <a-menu-item key="preferences">
                <template #icon>
                  <ControlOutlined />
                </template>
                偏好设置
              </a-menu-item>
              <a-menu-item key="security">
                <template #icon>
                  <SafetyOutlined />
                </template>
                安全中心
              </a-menu-item>
            </a-menu>
          </div>
        </a-col>
        
        <a-col :span="18">
          <div class="content-area">
            <router-view />
          </div>
        </a-col>
      </a-row>
    </div>

    <!-- 头像上传模态框 -->
    <a-modal
      v-model:open="showAvatarUpload"
      title="更换头像"
      @ok="handleAvatarUpload"
      @cancel="showAvatarUpload = false"
      :confirm-loading="uploading"
    >
      <a-upload
        v-model:file-list="fileList"
        :before-upload="beforeUpload"
        list-type="picture-card"
        :max-count="1"
        accept="image/*"
      >
        <div v-if="fileList.length < 1">
          <plus-outlined />
          <div style="margin-top: 8px">选择图片</div>
        </div>
      </a-upload>
      <p class="upload-tips">
        支持 JPG、PNG、GIF 格式，文件大小不超过 5MB
      </p>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  UserOutlined, 
  SettingOutlined, 
  ControlOutlined, 
  SafetyOutlined,
  PlusOutlined 
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api/auth'
import type { UploadFile } from 'ant-design-vue'

const router = useRouter()
const userStore = useUserStore()

const selectedKeys = ref(['profile'])
const showAvatarUpload = ref(false)
const uploading = ref(false)
const fileList = ref<UploadFile[]>([])

 // 计算头像URL，添加时间戳避免缓存（开发环境统一走 /api，避免 context-path 丢失）
const avatarUrl = computed(() => {
  const url = userStore.user?.avatarUrl
  if (url) {
    let full = url
    if (!url.startsWith('http')) {
      const apiBase = import.meta.env.DEV ? '/api' : (import.meta.env.VITE_API_BASE_URL || '')
      full = `${apiBase}${url}`
    }
    const timestamp = Date.now()
    return `${full}?t=${timestamp}`
  }
  return ''
})

onMounted(() => {
  // 根据当前路由设置选中的菜单项
  const currentPath = router.currentRoute.value.name as string
  if (currentPath) {
    selectedKeys.value = [currentPath.replace('user-', '')]
  }
})

const handleMenuClick = ({ key }: { key: string }) => {
  router.push({ name: `user-${key}` })
}

const formatLastLogin = (lastLoginAt?: string) => {
  if (!lastLoginAt) return '从未登录'
  return new Date(lastLoginAt).toLocaleString('zh-CN')
}

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
    return false
  }
  
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    message.error('图片大小不能超过 5MB!')
    return false
  }
  
  return false // 阻止自动上传
}

const handleAvatarUpload = async () => {
  if (fileList.value.length === 0) {
    message.warning('请选择要上传的图片')
    return
  }

  const file = fileList.value[0].originFileObj as File
  if (!file) return

  uploading.value = true
  try {
    const response = await authApi.uploadAvatar(file)
    if (response.success) {
      // 更新用户头像
      const updateResult = await userStore.updateProfile({ avatarUrl: response.data })
      if (updateResult) {
        message.success('头像更新成功')
        showAvatarUpload.value = false
        fileList.value = []
        // 强制刷新用户信息以确保头像更新
        await userStore.fetchUserInfo()
      }
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    message.error('头像上传失败')
  } finally {
    uploading.value = false
  }
}
</script>

<style scoped>
.user-center {
  min-height: 100vh;
  background: #f5f5f5;
}

.user-center-header {
  background: white;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 24px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  border: 3px solid #f0f0f0;
}

.change-avatar-btn {
  font-size: 12px;
  padding: 0;
}

.user-details {
  flex: 1;
}

.username {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.user-email {
  margin: 0 0 16px 0;
  color: #8c8c8c;
  font-size: 14px;
}

.user-stats {
  display: flex;
  gap: 48px;
}

.stat-item {
  text-align: center;
}

.user-center-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.sidebar {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-menu {
  border: none;
}

.content-area {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-height: 600px;
}

.upload-tips {
  margin-top: 16px;
  color: #8c8c8c;
  font-size: 12px;
  text-align: center;
}
</style>
