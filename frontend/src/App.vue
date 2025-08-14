<template>
  <div id="app" :class="{ 'dark-theme': isDarkTheme }">
    <LoveParticles ref="particlesRef" />
    <router-view v-slot="{ Component }">
      <transition name="page-transition">
        <component :is="Component" />
      </transition>
    </router-view>
    <div class="theme-toggle" @click="toggleTheme">
      <div class="toggle-icon">
        {{ isDarkTheme ? '🌙' : '☀️' }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import LoveParticles from './components/LoveParticles.vue'
import soundEffects from './utils/soundEffects'

// 粒子效果引用
const particlesRef = ref<InstanceType<typeof LoveParticles> | null>(null)

// 当前路由
const route = useRoute()

// 深色模式状态
const isDarkTheme = ref(false)

// 切换主题
const toggleTheme = () => {
  isDarkTheme.value = !isDarkTheme.value
  
  // 播放切换音效
  soundEffects.playSound(soundEffects.SoundType.BUTTON_CLICK)
  
  // 保存主题设置
  localStorage.setItem('theme', isDarkTheme.value ? 'dark' : 'light')
  
  // 更新文档类
  if (isDarkTheme.value) {
    document.body.classList.add('dark-theme')
  } else {
    document.body.classList.remove('dark-theme')
  }
}

// 监听路由变化，创建粒子流效果
watch(() => route.path, (newPath, oldPath) => {
  if (oldPath) {
    // 延迟执行，确保组件已加载
    setTimeout(() => {
      if (particlesRef.value) {
        // 从页面中心创建粒子爆发
        const centerX = window.innerWidth / 2
        const centerY = window.innerHeight / 2
        particlesRef.value.createHeartBurst(centerX, centerY, 50)
      }
      
      // 播放微风声效果（可选，如果不需要可以注释掉）
      try {
        // soundEffects.playBreeze(1500) // 暂时禁用音频以减少控制台警告
      } catch (error) {
        console.error('播放声音效果失败:', error)
      }
    }, 100)
  }
}, { immediate: false })

// 组件挂载时
onMounted(() => {
  // 从本地存储加载主题设置
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme === 'dark') {
    isDarkTheme.value = true
    document.body.classList.add('dark-theme')
  } else if (savedTheme === 'light') {
    isDarkTheme.value = false
    document.body.classList.remove('dark-theme')
  } else {
    // 如果没有保存的设置，使用系统偏好
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
    isDarkTheme.value = prefersDark
    if (prefersDark) {
      document.body.classList.add('dark-theme')
    }
  }
})
</script>

<style>
#app {
  height: 100vh;
  width: 100vw;
  font-family: var(--font-family);
  background-color: var(--background-color);
  color: var(--text-color);
  transition: background-color var(--transition-normal), color var(--transition-normal);
  overflow: hidden;
}

/* 主题切换按钮 */
.theme-toggle {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--primary-color);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: var(--shadow-sm);
  z-index: 1000;
  transition: all var(--transition-normal);
}

.theme-toggle:hover {
  transform: scale(1.1);
  box-shadow: var(--shadow-md);
}

.toggle-icon {
  font-size: 20px;
  line-height: 1;
}

/* 页面转场动画 */
.page-transition-enter-active,
.page-transition-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.page-transition-enter-from,
.page-transition-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
</style>
