import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    host: '127.0.0.1', // 使用 IPv4 地址，避免 IPv6 权限问题
    port: 5173,
    strictPort: true, // 如果端口被占用，自动尝试下一个可用端口
  },
})
