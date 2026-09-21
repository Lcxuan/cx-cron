import { loadEnv } from 'vite';
import { defineConfig } from 'vitest/config';
import vue from '@vitejs/plugin-vue';
import { fileURLToPath, URL } from 'node:url';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '#': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    test: {
      environment: 'node',
    },
    server: env.VITE_API_PROXY_TARGET
      ? {
          proxy: {
            '/client': {
              target: env.VITE_API_PROXY_TARGET,
              changeOrigin: true,
            },
          },
        }
      : undefined,
  };
});
