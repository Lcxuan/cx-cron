import { Empty } from 'ant-design-vue';
import { defineComponent, h } from 'vue';
import type { RouteRecordRaw } from 'vue-router';
import { createRouter, createWebHashHistory } from 'vue-router';
import { installRouterGuard } from './guard';

export const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Root',
    component: () => import('#/layouts/BasicLayout.vue'),
    redirect: '/empty-menu',
    meta: { title: 'cx-cron' },
    children: [
      {
        path: '/empty-menu',
        name: 'EmptyMenu',
        component: defineComponent({
          setup: () => () => h(
            'div',
            { style: { display: 'flex', justifyContent: 'center', paddingTop: '15vh' } },
            [h(Empty, { description: '暂无可访问菜单。' })],
          ),
        }),
        meta: { hideInMenu: true, requiresAuth: true, title: '暂无可访问菜单' },
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
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: defineComponent({ setup: () => () => h('div', '页面不存在') }),
    meta: { requiresAuth: true, hideInMenu: true, title: '页面不存在' },
  },
];

export const router = createRouter({
  history: createWebHashHistory(import.meta.env.VITE_BASE),
  routes,
});

installRouterGuard(router);
