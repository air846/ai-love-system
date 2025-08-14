<template>
  <a-layout class="dashboard-layout">
    <!-- 侧边栏 -->
    <a-layout-sider
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      class="dashboard-sider"
    >
      <div class="logo">
        <img src="/images/love-illustration.svg" class="logo-icon" alt="爱心图标" />
        <h2 v-if="!collapsed">甜蜜对话</h2>
        <h2 v-else>❤️</h2>
      </div>
      
      <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="light"
        mode="inline"
        class="custom-menu"
        @click="handleMenuClick"
      >
        <a-menu-item key="chat" class="menu-item">
          <div class="menu-icon cloud-icon">
            <CloudOutlined />
          </div>
          <span>甜蜜聊天</span>
        </a-menu-item>
        
        <a-menu-item key="characters" class="menu-item">
          <div class="menu-icon heart-icon">
            <HeartOutlined />
          </div>
          <span>AI伙伴</span>
        </a-menu-item>
        
        <a-menu-item key="emotions" class="menu-item">
          <div class="menu-icon flower-icon">
            <SmileOutlined />
          </div>
          <span>情感花园</span>
        </a-menu-item>
        
        <a-menu-item key="history" class="menu-item">
          <div class="menu-icon letter-icon">
            <MailOutlined />
          </div>
          <span>记忆信箱</span>
        </a-menu-item>

        <a-menu-item key="user" class="menu-item">
          <div class="menu-icon user-icon">
            <UserOutlined />
          </div>
          <span>个人中心</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    
    <!-- 主内容区 -->
    <a-layout>
      <!-- 顶部导航 -->
      <a-layout-header class="dashboard-header">
        <div class="header-left">
          <MenuUnfoldOutlined
            v-if="collapsed"
            class="trigger"
            @click="() => (collapsed = !collapsed)"
          />
          <MenuFoldOutlined
            v-else
            class="trigger"
            @click="() => (collapsed = !collapsed)"
          />
        </div>
        
        <div class="header-right">
          <a-dropdown>
            <a class="user-dropdown" @click.prevent>
               <a-avatar :src="userStore.user?.avatarUrl">
                {{ (userStore.user?.nickname || userStore.user?.username)?.charAt(0).toUpperCase() }}
              </a-avatar>
              <span class="username">{{ userStore.user?.nickname || userStore.user?.username }}</span>
              <DownOutlined />
            </a>
            
            <template #overlay>
              <a-menu>
                <a-menu-item @click="handleProfile">
                  <UserOutlined />
                  个人信息
                </a-menu-item>
                <a-menu-item @click="handleSettings">
                  <SettingOutlined />
                  设置
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item @click="handleLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>
      
      <!-- 内容区域 -->
      <a-layout-content class="dashboard-content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  MessageOutlined,
  RobotOutlined,
  HeartOutlined,
  HistoryOutlined,
  UserOutlined,
  SettingOutlined,
  LogoutOutlined,
  DownOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const collapsed = ref(false)
const selectedKeys = ref<string[]>([])

// 根据当前路由设置选中的菜单项
const updateSelectedKeys = () => {
  const path = route.path
  if (path.includes('/chat')) {
    selectedKeys.value = ['chat']
  } else if (path.includes('/characters')) {
    selectedKeys.value = ['characters']
  } else if (path.includes('/emotions')) {
    selectedKeys.value = ['emotions']
  } else if (path.includes('/history')) {
    selectedKeys.value = ['history']
  } else if (path.includes('/user')) {
    selectedKeys.value = ['user']
  }
}

const handleMenuClick = ({ key }: { key: string }) => {
  router.push(`/dashboard/${key}`)
}

const handleProfile = () => {
  router.push('/dashboard/user/profile')
}

const handleSettings = () => {
  router.push('/dashboard/user/preferences')
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  updateSelectedKeys()
  // 初始化用户信息
  userStore.initUser()
})

// 监听路由变化
watch(() => route.path, () => {
  updateSelectedKeys()
})
</script>

<style scoped>
.dashboard-layout {
  min-height: 100vh;
  background-color: var(--background-color);
}

.dashboard-sider {
  position: fixed;
  height: 100vh;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
  background-color: var(--sidebar-bg);
  box-shadow: var(--shadow-md);
  border-right: 1px solid var(--border-color);
}

.logo {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-light);
  margin: 16px;
  border-radius: 16px;
  box-shadow: var(--shadow-sm);
  position: relative;
  overflow: hidden;
}

.logo::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('/images/paper-texture.png') repeat;
  opacity: 0.1;
  pointer-events: none;
}

.logo-icon {
  width: 32px;
  height: 32px;
  margin-right: 8px;
}

.logo h2 {
  color: var(--primary-dark);
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  font-family: var(--font-family-fancy);
}

.custom-menu {
  background-color: transparent;
  border-right: none;
  padding: 16px 8px;
}

.menu-item {
  margin: 8px 0;
  border-radius: 12px;
  overflow: hidden;
  transition: all var(--transition-normal);
}

.menu-item:hover {
  background-color: var(--hover-color);
  transform: translateY(-2px);
}

.menu-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 8px;
}

.cloud-icon {
  background-color: var(--mint-green-light);
  color: var(--mint-green);
}

.heart-icon {
  background-color: var(--peach-pink-light);
  color: var(--peach-pink);
}

.flower-icon {
  background-color: var(--cream-yellow-light);
  color: var(--cream-yellow);
}

.letter-icon {
  background-color: var(--lavender-light);
  color: var(--lavender);
}

.dashboard-header {
  background: var(--card-bg);
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: var(--shadow-sm);
  position: fixed;
  top: 0;
  right: 0;
  left: 200px;
  z-index: 99;
  transition: left 0.2s;
  height: 70px;
  border-bottom: 1px solid var(--border-color);
  border-radius: 0 0 16px 16px;
}

.dashboard-header.collapsed {
  left: 80px;
}

.header-left {
  display: flex;
  align-items: center;
}

.trigger {
  font-size: 20px;
  line-height: 70px;
  padding: 0 24px;
  cursor: pointer;
  transition: all var(--transition-normal);
  color: var(--text-color-light);
}

.trigger:hover {
  color: var(--primary-color);
  transform: scale(1.1);
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  padding: 6px 12px;
  cursor: pointer;
  transition: all var(--transition-normal);
  border-radius: 20px;
  background-color: var(--card-bg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
}

.user-dropdown:hover {
  background-color: var(--hover-color);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.username {
  margin: 0 8px;
  color: var(--text-color);
  font-weight: 500;
}

.dashboard-content {
  margin-left: 200px;
  margin-top: 70px;
  padding: 24px;
  background: var(--background-color);
  min-height: calc(100vh - 70px);
  transition: all var(--transition-normal);
  position: relative;
}

.dashboard-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('/images/paper-texture.png') repeat;
  opacity: 0.05;
  pointer-events: none;
  z-index: -1;
}

.dashboard-content.collapsed {
  margin-left: 80px;
}

@media (max-width: 768px) {
  .dashboard-sider {
    transform: translateX(-100%);
    transition: transform 0.3s;
  }
  
  .dashboard-sider.mobile-open {
    transform: translateX(0);
  }
  
  .dashboard-header {
    left: 0;
  }
  
  .dashboard-content {
    margin-left: 0;
  }
}
</style>
