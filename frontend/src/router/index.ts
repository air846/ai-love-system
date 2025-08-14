import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/debug/api-test',
    name: 'ApiTest',
    component: () => import('@/views/debug/ApiTest.vue'),
    meta: {
      title: 'API测试',
      requiresAuth: false
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: {
      title: '注册',
      requiresAuth: false
    }
  },
  {
    path: '/dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: {
      title: '控制台',
      requiresAuth: true
    },
    children: [
      {
        path: '',
        name: 'Dashboard',
        redirect: '/dashboard/chat'
      },
      {
        path: 'chat',
        name: 'Chat',
        component: () => import('@/views/chat/ChatRoom.vue'),
        meta: {
          title: '聊天室',
          requiresAuth: true
        }
      },
      {
        path: 'characters',
        name: 'Characters',
        component: () => import('@/views/character/CharacterList.vue'),
        meta: {
          title: 'AI角色',
          requiresAuth: true
        }
      },
      {
        path: 'characters/create',
        name: 'CreateCharacter',
        component: () => import('@/views/character/CreateCharacter.vue'),
        meta: {
          title: '创建角色',
          requiresAuth: true
        }
      },
      {
        path: 'characters/:characterId/edit',
        name: 'EditCharacter',
        component: () => import('@/views/character/EditCharacter.vue'),
        meta: {
          title: '编辑角色',
          requiresAuth: true
        }
      },
      {
        path: 'emotions',
        name: 'Emotions',
        component: () => import('@/views/emotion/EmotionAnalysis.vue'),
        meta: {
          title: '情感分析',
          requiresAuth: true
        }
      },
      {
        path: 'history',
        name: 'History',
        component: () => import('@/views/history/ConversationHistory.vue'),
        meta: {
          title: '对话历史',
          requiresAuth: true
        }
      },
      {
        path: 'user',
        component: () => import('@/views/user/UserCenter.vue'),
        meta: {
          title: '用户中心',
          requiresAuth: true
        },
        children: [
          {
            path: '',
            redirect: '/dashboard/user/profile'
          },
          {
            path: 'profile',
            name: 'user-profile',
            component: () => import('@/views/user/UserProfile.vue'),
            meta: {
              title: '个人资料',
              requiresAuth: true
            }
          },
          {
            path: 'account',
            name: 'user-account',
            component: () => import('@/views/user/UserAccount.vue'),
            meta: {
              title: '账户设置',
              requiresAuth: true
            }
          },
          {
            path: 'preferences',
            name: 'user-preferences',
            component: () => import('@/views/user/UserPreferences.vue'),
            meta: {
              title: '偏好设置',
              requiresAuth: true
            }
          },
          {
            path: 'security',
            name: 'user-security',
            component: () => import('@/views/user/UserAccount.vue'), // 暂时复用账户设置页面
            meta: {
              title: '安全中心',
              requiresAuth: true
            }
          }
        ]
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/NotFound.vue'),
    meta: {
      title: '页面未找到'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  if (to.meta?.title) {
    document.title = `${to.meta.title} - AI恋爱系统`
  }
  
  // 如果有token但没有用户信息，先尝试获取用户信息
  const token = localStorage.getItem('token')
  if (token && !userStore.isAuthenticated) {
    try {
      await userStore.initUser()
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 如果获取用户信息失败，清除token
      localStorage.removeItem('token')
    }
  }
  
  // 检查是否需要认证
  if (to.meta?.requiresAuth && !userStore.isAuthenticated) {
    next('/login')
  } else if ((to.name === 'Login' || to.name === 'Register') && userStore.isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
