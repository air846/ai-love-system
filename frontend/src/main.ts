import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import { useUserStore } from './stores/user'

// Ant Design Vue
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'

// 全局样式
import './styles/global.css'

// 引入声音效果工具
import soundEffects from './utils/soundEffects'

const app = createApp(App)

// 全局属性
app.config.globalProperties.$sound = soundEffects

// 初始化主题
const initTheme = () => {
  // 检查是否为夜间模式
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  if (prefersDark) {
    document.body.classList.add('dark-theme')
  }
  
  // 监听系统主题变化
  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
    if (e.matches) {
      document.body.classList.add('dark-theme')
    } else {
      document.body.classList.remove('dark-theme')
    }
  })
}

// 页面转场动画
router.beforeEach((to, from, next) => {
  // 播放页面切换音效
  if (from.name) {
    soundEffects.playSound(soundEffects.SoundType.BUTTON_CLICK)
  }
  next()
})

const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(Antd)

// 初始化主题
initTheme()

app.mount('#app')

// 初始化用户信息
const userStore = useUserStore(pinia)
userStore.initUser()
