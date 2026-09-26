import { loadEnv } from 'vite';
import { defineConfig } from '@vben/vite-config';

export default defineConfig(async (config) => {
  const env = loadEnv(config?.mode ?? 'development', process.cwd(), '');

  return {
    application: {},
    vite: {
      resolve: {
        alias: {
          '#': new URL('./src', import.meta.url).pathname,
        },
      },
      server: {
        proxy: {
          '/client': {
            changeOrigin: true,
            target: env.VITE_API_PROXY_TARGET || 'http://localhost:8080',
          },
        },
      },
    },
  };
});
