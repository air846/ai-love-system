<template>
  <canvas ref="particleCanvas" class="love-particles"></canvas>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

// 画布引用
const particleCanvas = ref<HTMLCanvasElement | null>(null)

// 声明WebAudio类型
declare global {
  interface Window {
    webkitAudioContext: typeof AudioContext
  }
}

// 粒子类型
interface Particle {
  x: number
  y: number
  size: number
  speedX: number
  speedY: number
  color: string
  opacity: number
  rotation: number
  rotationSpeed: number
  type: 'heart' | 'star' | 'circle'
}

// 粒子系统
let particles: Particle[] = []
let animationId: number | null = null
let ctx: CanvasRenderingContext2D | null = null
let width = 0
let height = 0

// 颜色数组
const colors = ['#ffb7b2', '#a8e6cf', '#ffdac1', '#e2d1f9', '#b5ead7']

// 初始化画布
const initCanvas = () => {
  if (!particleCanvas.value) return
  
  // 设置画布大小为窗口大小
  width = window.innerWidth
  height = window.innerHeight
  particleCanvas.value.width = width
  particleCanvas.value.height = height
  
  // 获取绘图上下文
  ctx = particleCanvas.value.getContext('2d')
}

// 创建粒子
const createParticle = (x: number, y: number): Particle => {
  const size = Math.random() * 15 + 5
  const speedX = (Math.random() - 0.5) * 3
  const speedY = (Math.random() - 0.5) * 3 - 1 // 向上飘的趋势
  const color = colors[Math.floor(Math.random() * colors.length)]
  const opacity = Math.random() * 0.5 + 0.5
  const rotation = Math.random() * Math.PI * 2
  const rotationSpeed = (Math.random() - 0.5) * 0.05
  
  // 随机选择粒子类型
  const types: ('heart' | 'star' | 'circle')[] = ['heart', 'star', 'circle']
  const type = types[Math.floor(Math.random() * types.length)]
  
  return {
    x,
    y,
    size,
    speedX,
    speedY,
    color,
    opacity,
    rotation,
    rotationSpeed,
    type
  }
}

// 绘制爱心
const drawHeart = (x: number, y: number, size: number, color: string, opacity: number, rotation: number) => {
  if (!ctx) return
  
  ctx.save()
  ctx.translate(x, y)
  ctx.rotate(rotation)
  ctx.scale(size / 30, size / 30)
  ctx.beginPath()
  ctx.moveTo(0, 0)
  ctx.bezierCurveTo(-10, -10, -15, 0, 0, 10)
  ctx.bezierCurveTo(15, 0, 10, -10, 0, 0)
  ctx.fillStyle = color
  ctx.globalAlpha = opacity
  ctx.fill()
  ctx.restore()
}

// 绘制星星
const drawStar = (x: number, y: number, size: number, color: string, opacity: number, rotation: number) => {
  if (!ctx) return
  
  ctx.save()
  ctx.translate(x, y)
  ctx.rotate(rotation)
  ctx.beginPath()
  
  const spikes = 5
  const outerRadius = size
  const innerRadius = size / 2
  
  for (let i = 0; i < spikes * 2; i++) {
    const radius = i % 2 === 0 ? outerRadius : innerRadius
    const angle = (Math.PI * 2 * i) / (spikes * 2)
    const pointX = Math.cos(angle) * radius
    const pointY = Math.sin(angle) * radius
    
    if (i === 0) {
      ctx.moveTo(pointX, pointY)
    } else {
      ctx.lineTo(pointX, pointY)
    }
  }
  
  ctx.closePath()
  ctx.fillStyle = color
  ctx.globalAlpha = opacity
  ctx.fill()
  ctx.restore()
}

// 绘制圆形
const drawCircle = (x: number, y: number, size: number, color: string, opacity: number) => {
  if (!ctx) return
  
  ctx.beginPath()
  ctx.arc(x, y, size / 2, 0, Math.PI * 2)
  ctx.fillStyle = color
  ctx.globalAlpha = opacity
  ctx.fill()
}

// 更新粒子
const updateParticles = () => {
  particles = particles.filter(p => {
    // 更新位置
    p.x += p.speedX
    p.y += p.speedY
    p.rotation += p.rotationSpeed
    
    // 减小不透明度
    p.opacity -= 0.005
    
    // 如果粒子不透明度大于0，保留它
    return p.opacity > 0
  })
}

// 绘制粒子
const drawParticles = () => {
  if (!ctx) return
  
  // 清除画布
  ctx.clearRect(0, 0, width, height)
  
  // 绘制每个粒子
  particles.forEach(p => {
    switch (p.type) {
      case 'heart':
        drawHeart(p.x, p.y, p.size, p.color, p.opacity, p.rotation)
        break
      case 'star':
        drawStar(p.x, p.y, p.size, p.color, p.opacity, p.rotation)
        break
      case 'circle':
        drawCircle(p.x, p.y, p.size, p.color, p.opacity)
        break
    }
  })
}

// 动画循环
const animate = () => {
  updateParticles()
  drawParticles()
  animationId = requestAnimationFrame(animate)
}

// 创建爱心粒子爆发
const createHeartBurst = (x: number, y: number, count: number = 20) => {
  for (let i = 0; i < count; i++) {
    particles.push(createParticle(x, y))
  }
}

// 创建从一点到另一点的粒子流
const createParticleStream = (startX: number, startY: number, endX: number, endY: number, count: number = 30) => {
  // 计算方向向量
  const dx = endX - startX
  const dy = endY - startY
  const distance = Math.sqrt(dx * dx + dy * dy)
  
  // 创建粒子
  for (let i = 0; i < count; i++) {
    // 在路径上随机选择一个点
    const t = Math.random()
    const x = startX + dx * t
    const y = startY + dy * t
    
    // 添加一些随机偏移
    const offsetX = (Math.random() - 0.5) * distance * 0.2
    const offsetY = (Math.random() - 0.5) * distance * 0.2
    
    particles.push(createParticle(x + offsetX, y + offsetY))
  }
}

// 监听鼠标移动
const handleMouseMove = (e: MouseEvent) => {
  // 每10次鼠标移动创建一个粒子
  if (Math.random() > 0.9) {
    particles.push(createParticle(e.clientX, e.clientY))
  }
}

// 监听点击事件
const handleClick = (e: MouseEvent) => {
  createHeartBurst(e.clientX, e.clientY, 30)
}

// 监听窗口大小变化
const handleResize = () => {
  initCanvas()
}

// 组件挂载时
onMounted(() => {
  initCanvas()
  
  // 添加事件监听器
  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('click', handleClick)
  window.addEventListener('resize', handleResize)
  
  // 开始动画
  animate()
  
  // 初始粒子
  for (let i = 0; i < 20; i++) {
    const x = Math.random() * width
    const y = Math.random() * height
    particles.push(createParticle(x, y))
  }
})

// 组件卸载时
onUnmounted(() => {
  // 移除事件监听器
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('click', handleClick)
  window.removeEventListener('resize', handleResize)
  
  // 停止动画
  if (animationId !== null) {
    cancelAnimationFrame(animationId)
  }
})

// 导出方法供外部使用
defineExpose({
  createHeartBurst,
  createParticleStream
})
</script>

<style scoped>
.love-particles {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 100;
}
</style>