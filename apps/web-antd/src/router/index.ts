import type { RouteRecordRaw } from 'vue-router';
import { createRouter, createWebHashHistory } from 'vue-router';
import { installRouterGuard } from './guard';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('#/layouts/BasicLayout.vue'),
    redirect: '/task',
    meta: { title: '任务管理' },
    children: [
      {
        path: '/task',
        name: 'TaskManagement',
        component: () => import('#/views/task/index.vue'),
        meta: { icon: 'lucide:list-todo', requiresAuth: true, title: '任务管理' },
      },
      {
        path: '/profile/password',
        name: 'UpdatePassword',
        component: () => import('#/views/profile/password/index.vue'),
        meta: { hideInMenu: true, requiresAuth: true, title: '修改密码' },
      },
    ],
  },
  {
    path: '/auth/login',
    name: 'Login',
    component: () => import('#/views/auth/login/index.vue'),
    meta: { hideInMenu: true, hideInTab: true, title: '登录' },
  },
  { path: '/:pathMatch(.*)*', redirect: '/task' },
];

export const router = createRouter({
  history: createWebHashHistory(import.meta.env.VITE_BASE),
  routes,
});

installRouterGuard(router);
