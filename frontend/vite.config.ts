import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { AntDesignVueResolver } from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      imports: [
        'vue',
        'vue-router',
        'pinia'
      ],
      dts: true,
      eslintrc: {
        enabled: true
      }
    }),
    Components({
      resolvers: [
        AntDesignVueResolver({
          importStyle: false
        })
      ],
      dts: true
    })
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: process.env.VITE_API_BASE_URL || 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path,
        // 添加错误处理和日志
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('代理错误:', err)
            console.log('目标地址:', options.target)
          })
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('代理请求:', req.method, req.url, '-> ', options.target + req.url)
          })
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('代理响应:', proxyRes.statusCode, req.url)
          })
        }
      }
    }
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    chunkSizeWarningLimit: 1600,
    rollupOptions: {
      output: {
        manualChunks: {
          vue: ['vue', 'vue-router', 'pinia'],
          antd: ['ant-design-vue', '@ant-design/icons-vue'],
          utils: ['axios', 'dayjs', 'lodash-es']
        }
      }
    }
  },
  css: {
    preprocessorOptions: {
      less: {
        modifyVars: {
          'primary-color': '#1890ff',
          'link-color': '#1890ff',
          'border-radius-base': '6px'
        },
        javascriptEnabled: true
      }
    }
  }
})
