<template>
  <div class="user-profile">
    <div class="page-header">
      <h2>个人资料</h2>
      <p>管理您的个人信息和偏好设置</p>
    </div>

    <a-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      layout="vertical"
      @finish="handleSubmit"
      class="profile-form"
    >
      <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item label="用户名" name="username">
            <a-input 
              v-model:value="formData.username" 
              disabled
              placeholder="用户名不可修改"
            />
          </a-form-item>
        </a-col>
        
        <a-col :span="12">
          <a-form-item label="昵称" name="nickname">
            <a-input 
              v-model:value="formData.nickname" 
              placeholder="请输入昵称"
              :maxlength="50"
              show-count
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item label="邮箱" name="email">
            <a-input 
              v-model:value="formData.email" 
              placeholder="请输入邮箱地址"
              type="email"
            />
            <div v-if="!userStore.user?.emailVerified" class="email-verify-tip">
              <ExclamationCircleOutlined />
              邮箱未验证
              <a-button type="link" size="small" @click="sendVerifyEmail">
                发送验证邮件
              </a-button>
            </div>
          </a-form-item>
        </a-col>
        
        <a-col :span="12">
          <a-form-item label="手机号" name="phone">
            <a-input 
              v-model:value="formData.phone" 
              placeholder="请输入手机号"
              :maxlength="11"
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item label="性别" name="gender">
            <a-select 
              v-model:value="formData.gender" 
              placeholder="请选择性别"
              allow-clear
            >
              <a-select-option value="MALE">男</a-select-option>
              <a-select-option value="FEMALE">女</a-select-option>
              <a-select-option value="OTHER">其他</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        
        <a-col :span="12">
          <a-form-item label="生日" name="birthDate">
            <a-date-picker 
              v-model:value="formData.birthDate" 
              placeholder="请选择生日"
              style="width: 100%"
              :disabled-date="disabledDate"
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-form-item label="个人简介" name="bio">
        <a-textarea 
          v-model:value="formData.bio" 
          placeholder="介绍一下自己吧..."
          :rows="4"
          :maxlength="500"
          show-count
        />
      </a-form-item>

      <a-form-item>
        <a-space>
          <a-button 
            type="primary" 
            html-type="submit" 
            :loading="loading"
            size="large"
          >
            保存修改
          </a-button>
          <a-button 
            @click="resetForm" 
            size="large"
          >
            重置
          </a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { ExclamationCircleOutlined } from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api/auth'
import type { FormInstance } from 'ant-design-vue'
import type { Dayjs } from 'dayjs'
import dayjs from 'dayjs'

const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const formData = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  gender: undefined as 'MALE' | 'FEMALE' | 'OTHER' | undefined,
  birthDate: undefined as Dayjs | undefined,
  bio: ''
})

const rules = {
  email: [
    { type: 'email', message: '请输入正确的邮箱格式' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式' }
  ],
  nickname: [
    { max: 50, message: '昵称长度不能超过50个字符' }
  ],
  bio: [
    { max: 500, message: '个人简介不能超过500个字符' }
  ]
}

onMounted(() => {
  loadUserData()
})

const loadUserData = () => {
  const user = userStore.user
  if (user) {
    formData.username = user.username
    formData.nickname = user.nickname || ''
    formData.email = user.email
    formData.phone = user.phone || ''
    formData.gender = user.gender
    formData.birthDate = user.birthDate ? dayjs(user.birthDate) : undefined
    formData.bio = user.bio || ''
  }
}

const disabledDate = (current: Dayjs) => {
  // 禁用未来日期
  return current && current > dayjs().endOf('day')
}

const handleSubmit = async () => {
  try {
    loading.value = true
    
    const updateData = {
      nickname: formData.nickname,
      email: formData.email,
      phone: formData.phone,
      gender: formData.gender,
      birthDate: formData.birthDate?.format('YYYY-MM-DD'),
      bio: formData.bio
    }

    const response = await authApi.updateProfile(updateData)
    if (response.success) {
      await userStore.fetchUserInfo()
      message.success('个人资料更新成功')
    }
  } catch (error) {
    console.error('更新个人资料失败:', error)
    message.error('更新失败，请重试')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  loadUserData()
  formRef.value?.clearValidate()
}

const sendVerifyEmail = async () => {
  try {
    // TODO: 实现发送验证邮件的API
    message.info('验证邮件发送功能待实现')
  } catch (error) {
    message.error('发送验证邮件失败')
  }
}
</script>

<style scoped>
.user-profile {
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

.profile-form {
  background: #fafafa;
  padding: 24px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
}

.email-verify-tip {
  margin-top: 8px;
  color: #faad14;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

:deep(.ant-form-item-label) {
  font-weight: 500;
}

:deep(.ant-input:disabled) {
  background-color: #f5f5f5;
  border-color: #d9d9d9;
  color: #8c8c8c;
}
</style>
