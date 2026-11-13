import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// 개발 서버에서 /api 요청을 Spring Boot(8080)로 전달한다.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
