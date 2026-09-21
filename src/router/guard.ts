import type { Router } from 'vue-router';
import { useAuthStore } from '#/store/auth';

export function installRouterGuard(router: Router) {
  router.beforeEach(async (to) => {
    const authStore = useAuthStore();
    if (!authStore.isRestored) await authStore.restoreSession();

    if (to.meta.requiresAuth && !authStore.isLoggedIn) {
      return { path: '/login', query: { redirect: to.fullPath } };
    }
    if (to.path === '/login' && authStore.isLoggedIn) return '/tasks';
    return true;
  });

  router.afterEach((to) => {
    document.title = `${to.meta.title ?? 'cx-cron'} | cx-cron`;
  });
}
