import type { MenuRecordRaw } from '@vben/types';
import type { Router } from 'vue-router';
import { useAccessStore } from '@vben/stores';
import { resetStaticRoutes } from '@vben/utils';

import { useAuthStore } from '#/store/auth';
import { generateAccess } from './access';
import { routes } from './index';

export function installRouterGuard(router: Router) {
  router.beforeEach(async (to) => {
    const authStore = useAuthStore();
    if (!authStore.isRestored) await authStore.restoreSession();

    if (to.meta.requiresAuth && !authStore.isLoggedIn) {
      resetAccessRoutes(router);
      return { path: '/auth/login', query: { redirect: to.fullPath } };
    }
    const accessStore = useAccessStore();
    if (authStore.isLoggedIn && !accessStore.isAccessChecked) {
      const redirectPath = to.fullPath;
      const { accessibleRoutes, accessibleMenus } = await generateAccess();
      accessStore.setAccessRoutes(accessibleRoutes);
      accessStore.setAccessMenus(accessibleMenus as unknown as MenuRecordRaw[]);
      accessStore.setIsAccessChecked(true);
      const defaultRoute = accessibleRoutes.find((route) => route.path === '/task')
        ?? accessibleRoutes[0];
      const target = accessibleRoutes.length === 0
        ? '/empty-menu'
        : to.path === '/auth/login' || to.path === '/empty-menu'
          ? defaultRoute?.path ?? '/empty-menu'
          : redirectPath;
      const resolved = router.resolve(target);
      if (resolved.name === 'NotFound' || to.path === '/auth/login') {
        const fallback = accessibleRoutes[0]?.path ?? '/empty-menu';
        return { path: resolved.name === 'NotFound' ? fallback : target, replace: true };
      }
      return { ...resolved, replace: true };
    }
    return true;
  });

  router.afterEach((to) => {
    document.title = `${to.meta.title ?? 'cx-cron'} | cx-cron`;
  });
}

export function resetAccessRoutes(router: Router) {
  const accessStore = useAccessStore();
  resetStaticRoutes(router, routes);
  accessStore.setAccessRoutes([]);
  accessStore.setAccessMenus([]);
  accessStore.setIsAccessChecked(false);
}
