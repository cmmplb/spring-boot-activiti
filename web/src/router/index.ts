import {createRouter, createWebHistory} from 'vue-router';
import {App} from 'vue';
import Layout from '@/layout/index.vue';

// 公共静态路由
const constantRoutes = [
  {
    path: '/',
    component: Layout,
    redirect: '/',
    children: [
      {
        path: '/',
        component: () => import ('@/views/home/index.vue'),
        name: '首页'
      }
    ]
  },
  {
    path: '/login',
    component: () => import ('@/views/login/index.vue')
  },
  {
    path: '/activiti-modeler',
    component: () => import ('@/views/process/model/activiti-modeler.vue'),
    name: 'activiti-modeler 模型设计'
  },
  {
    path: '/bpmn-js',
    component: () => import ('@/views/process/model/bpmn-js.vue'),
    name: 'bpmn-js 模型设计'
  },
  {
    path: '/process',
    component: Layout,
    // 重定向到 /process, 效果是访问 /process 重定向到 /process/model
    redirect: '/process/model',
    children: [
      {
        path: '/process/model',
        component: () => import ('@/views/process/model/index.vue'),
        name: '模型管理'
      },
      {
        path: '/process/deployment',
        component: () => import ('@/views/process/deployment/index.vue'),
        name: '部署管理'
      },
      {
        path: '/process/definition',
        component: () => import ('@/views/process/definition/index.vue'),
        name: '定义管理'
      }
    ]
  }
];

export const router = createRouter({
  // 相同的 url, history 会触发添加到浏览器历史记录栈中, hash 不会触发, WebHistory 需要后端配合, 如果后端不配合刷新新页面会出现404, hash 不需要
  history: createWebHistory(import.meta.env.VITE_APP_BASE_PATH),
  // Hash 模式会在根目录后面拼接 /#/, 优点是刷新页面不会丢失, 缺点是URL会多一个 /#/
  // history: createWebHashHistory(import.meta.env.VITE_APP_BASE_PATH),
  // history: createMemoryHistory(import.meta.env.VITE_APP_BASE_PATH),
  routes: constantRoutes,
  strict: true,
  scrollBehavior: () => ({left: 0, top: 0})
});

export function setupRouter(app: App<Element>) {
  app.use(router);
}