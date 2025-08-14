<template>
  <div class="user-account">
    <div class="page-header">
      <h2>账户设置</h2>
      <p>管理您的账户安全和登录设置</p>
    </div>

    <div class="settings-sections">
      <!-- 密码修改 -->
      <div class="setting-section">
        <div class="section-header">
          <h3>修改密码</h3>
          <p>定期更换密码可以提高账户安全性</p>
        </div>
        
        <a-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          layout="vertical"
          @finish="handlePasswordChange"
          class="password-form"
        >
          <a-form-item label="当前密码" name="oldPassword">
            <a-input-password 
              v-model:value="passwordForm.oldPassword" 
              placeholder="请输入当前密码"
              autocomplete="current-password"
            />
          </a-form-item>
          
          <a-form-item label="新密码" name="newPassword">
            <a-input-password 
              v-model:value="passwordForm.newPassword" 
              placeholder="请输入新密码"
              autocomplete="new-password"
            />
          </a-form-item>
          
          <a-form-item label="确认新密码" name="confirmPassword">
            <a-input-password 
              v-model:value="passwordForm.confirmPassword" 
              placeholder="请再次输入新密码"
              autocomplete="new-password"
            />
          </a-form-item>
          
          <a-form-item>
            <a-button 
              type="primary" 
              html-type="submit" 
              :loading="passwordLoading"
            >
              修改密码
            </a-button>
          </a-form-item>
        </a-form>
      </div>

      <!-- 邮箱验证 -->
      <div class="setting-section">
        <div class="section-header">
          <h3>邮箱验证</h3>
          <p>验证邮箱可以帮助您找回密码和接收重要通知</p>
        </div>
        
        <div class="email-verification">
          <div class="email-status">
            <span class="email-address">{{ userStore.user?.email }}</span>
            <a-tag 
              :color="userStore.user?.emailVerified ? 'green' : 'orange'"
              class="verify-status"
            >
              {{ userStore.user?.emailVerified ? '已验证' : '未验证' }}
            </a-tag>
          </div>
          
          <div v-if="!userStore.user?.emailVerified" class="verify-actions">
            <a-button 
              type="primary" 
              @click="sendVerificationEmail"
              :loading="emailLoading"
            >
              发送验证邮件
            </a-button>
            <p class="verify-tip">
              点击发送验证邮件，然后查看您的邮箱并点击验证链接
            </p>
          </div>
        </div>
      </div>

      <!-- 登录历史 -->
      <div class="setting-section">
        <div class="section-header">
          <h3>登录信息</h3>
          <p>查看您的登录历史和设备信息</p>
        </div>
        
        <div class="login-info">
          <a-descriptions :column="1" bordered>
            <a-descriptions-item label="登录次数">
              {{ userStore.user?.loginCount || 0 }} 次
            </a-descriptions-item>
            <a-descriptions-item label="最后登录时间">
              {{ formatLastLogin(userStore.user?.lastLoginAt) }}
            </a-descriptions-item>
            <a-descriptions-item label="最后登录IP">
              {{ userStore.user?.lastLoginIp || '未知' }}
            </a-descriptions-item>
            <a-descriptions-item label="账户状态">
              <a-tag :color="getStatusColor(userStore.user?.status)">
                {{ getStatusText(userStore.user?.status) }}
              </a-tag>
            </a-descriptions-item>
          </a-descriptions>
        </div>
      </div>

      <!-- 危险操作 -->
      <div class="setting-section danger-section">
        <div class="section-header">
          <h3>危险操作</h3>
          <p>以下操作不可逆，请谨慎操作</p>
        </div>
        
        <div class="danger-actions">
          <a-button 
            danger 
            @click="showDeleteConfirm"
            class="delete-account-btn"
          >
            注销账户
          </a-button>
          <p class="danger-tip">
            注销账户将永久删除您的所有数据，此操作不可恢复
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api/auth'
import type { FormInstance } from 'ant-design-vue'

const userStore = useUserStore()
const passwordFormRef = ref<FormInstance>()
const passwordLoading = ref(false)
const emailLoading = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 6, message: '密码长度至少6位' },
    { max: 100, message: '密码长度不能超过100位' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码' },
    {
      validator: (_: any, value: string) => {
        if (value && value !== passwordForm.newPassword) {
          return Promise.reject('两次输入的密码不一致')
        }
        return Promise.resolve()
      }
    }
  ]
}

const handlePasswordChange = async () => {
  try {
    passwordLoading.value = true
    
    const response = await authApi.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    if (response.success) {
      message.success('密码修改成功')
      // 清空表单
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      passwordFormRef.value?.resetFields()
    }
  } catch (error) {
    console.error('密码修改失败:', error)
    message.error('密码修改失败，请检查当前密码是否正确')
  } finally {
    passwordLoading.value = false
  }
}

const sendVerificationEmail = async () => {
  try {
    emailLoading.value = true
    // TODO: 实现发送验证邮件的API
    message.info('验证邮件发送功能待实现')
  } catch (error) {
    message.error('发送验证邮件失败')
  } finally {
    emailLoading.value = false
  }
}

const formatLastLogin = (lastLoginAt?: string) => {
  if (!lastLoginAt) return '从未登录'
  return new Date(lastLoginAt).toLocaleString('zh-CN')
}

const getStatusColor = (status?: string) => {
  switch (status) {
    case 'ACTIVE': return 'green'
    case 'INACTIVE': return 'orange'
    case 'SUSPENDED': return 'red'
    case 'DELETED': return 'red'
    default: return 'default'
  }
}

const getStatusText = (status?: string) => {
  switch (status) {
    case 'ACTIVE': return '正常'
    case 'INACTIVE': return '未激活'
    case 'SUSPENDED': return '已暂停'
    case 'DELETED': return '已删除'
    default: return '未知'
  }
}

const showDeleteConfirm = () => {
  Modal.confirm({
    title: '确认注销账户？',
    content: '注销账户将永久删除您的所有数据，包括AI角色、对话记录等，此操作不可恢复。',
    okText: '确认注销',
    okType: 'danger',
    cancelText: '取消',
    onOk() {
      // TODO: 实现账户注销功能
      message.info('账户注销功能待实现')
    }
  })
}
</script>

<style scoped>
.user-account {
  max-width: 800px;
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

.settings-sections {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.setting-section {
  background: #fafafa;
  padding: 24px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
}

.danger-section {
  border-color: #ff4d4f;
  background: #fff2f0;
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

.password-form {
  max-width: 400px;
}

.email-verification {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.email-status {
  display: flex;
  align-items: center;
  gap: 12px;
}

.email-address {
  font-weight: 500;
  color: #262626;
}

.verify-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.verify-tip {
  margin: 0;
  color: #8c8c8c;
  font-size: 12px;
}

.login-info {
  max-width: 500px;
}

.danger-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.danger-tip {
  margin: 0;
  color: #ff4d4f;
  font-size: 12px;
}

:deep(.ant-form-item-label) {
  font-weight: 500;
}
</style>
