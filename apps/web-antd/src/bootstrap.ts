import { createApp } from 'vue';

import Antd from 'ant-design-vue';
import { setupI18n } from '@vben/locales';
import { initStores } from '@vben/stores';
import '@vben/styles';
import '@vben/styles/antd';

import App from './app.vue';
import { router } from './router';

export async function bootstrap(namespace: string) {
  const app = createApp(App);

  app.use(Antd);
  await setupI18n(app);
  await initStores(app, { namespace });
  app.use(router);
  await router.isReady();
  app.mount('#app');
}
