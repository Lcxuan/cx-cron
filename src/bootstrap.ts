import { createApp } from 'vue';
import Antd from 'ant-design-vue';
import { createPinia } from 'pinia';
import App from './app.vue';
import router from './router';
import 'ant-design-vue/dist/reset.css';
import './style.css';

export async function bootstrap() {
  const app = createApp(App);
  app.use(Antd);
  app.use(createPinia());
  app.use(router);
  await router.isReady();
  app.mount('#app');
}
