import {App} from 'vue';

// element-plus
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

// 加载公共样式
import '../assets/styles/common.scss';

export function setupConfig(app: App<Element>) {

  // element-plus
  app.use(ElementPlus);

  // 注册element-plus所有图标
  for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
  }
}