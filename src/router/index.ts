import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import { installRouterGuard } from './guard';

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    component: () => import('#/views/auth/login/index.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/tasks',
    component: () => import('#/layouts/BasicLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        component: () => import('#/views/task/index.vue'),
        meta: { requiresAuth: true, title: '任务管理' },
      },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/tasks' },
];

const router = createRouter({ history: createWebHistory(), routes });
installRouterGuard(router);

export default router;
