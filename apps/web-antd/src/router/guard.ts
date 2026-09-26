import type { Router } from 'vue-router';

import { useAccessStore } from '@vben/stores';

import { useAuthStore } from '#/store/auth';
import { getSession } from '#/store/session';

export function installRouterGuard(router: Router) {
  router.beforeEach(async (to) => {
    const authStore = useAuthStore();
    if (!authStore.isRestored) await authStore.restoreSession();

    if (to.meta.requiresAuth && !authStore.isLoggedIn) {
      return { path: '/auth/login', query: { redirect: to.fullPath } };
    }
    if (to.path === '/auth/login' && authStore.isLoggedIn && getSession()) return '/task';

    const accessStore = useAccessStore();
    accessStore.setAccessMenus([
      {
        icon: 'lucide:list-todo',
        name: '任务管理',
        path: '/task',
      },
    ]);
    return true;
  });

  router.afterEach((to) => {
    document.title = `${to.meta.title ?? 'cx-cron'} | cx-cron`;
  });
}
