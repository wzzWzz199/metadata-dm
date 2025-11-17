import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'node:path';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const backend = env.VITE_BACKEND_URL || '';

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
      },
    },
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: '',
        },
      },
    },
    server: {
      port: 5173,
      host: '0.0.0.0',
      proxy: {
        '/metadata': {
          target: backend || 'http://localhost:8080',
          changeOrigin: true,
        },
        '/login': {
          target: backend || 'http://localhost:8080',
          changeOrigin: true,
        },
        '/logout': {
          target: backend || 'http://localhost:8080',
          changeOrigin: true,
        },
      },
    },
    build: {
      outDir: 'dist',
      emptyOutDir: true,
    },
  };
});
