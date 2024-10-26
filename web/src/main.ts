import {createApp} from 'vue';
import App from './App.vue';
import {setupRouter} from './router';
import {setupConfig} from './config';

async function bootstrap() {
  const app = createApp(App);

  // 路由配置
  setupRouter(app);

  // 系统配置
  setupConfig(app);

  app.mount('#app');
}

bootstrap().catch((e) => {
  console.log(e);
});