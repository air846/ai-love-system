import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
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
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  if (to.meta?.title) {
    document.title = `${to.meta.title} - AI恋爱系统`
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
