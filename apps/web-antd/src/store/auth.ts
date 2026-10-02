import type { MenuRecordRaw } from '@vben/types';
import { defineStore } from 'pinia';
import { useAccessStore } from '@vben/stores';

import { getCurrentUserApi, loginApi, logoutApi, type AuthApi } from '#/api/core/auth';
import { clearSession, getSession, saveSession } from './session';
import { resetAccessRoutes } from '#/router/guard';
import { router } from '#/router';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    currentUser: null as AuthApi.CurrentUserResp | null,
    isLoading: false,
    isRestored: false,
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.currentUser),
  },
  actions: {
    async login(params: AuthApi.LoginParams) {
      this.isLoading = true;
      try {
        const tokens = await loginApi(params);
        saveSession(tokens);
        const accessStore = useAccessStore();
        resetAccessRoutes(router);
        accessStore.setAccessToken(tokens.accessToken);
        accessStore.setRefreshToken(tokens.refreshToken);
        accessStore.setIsAccessChecked(false);
        accessStore.setAccessRoutes([]);
        accessStore.setAccessMenus([]);
        this.isRestored = false;
        await this.restoreSession();
      } finally {
        this.isLoading = false;
      }
    },
    async restoreSession() {
      if (this.isRestored) return;
      const session = getSession();
      if (!session) {
        this.clearSession();
        return;
      }
      const accessStore = useAccessStore();
      accessStore.setAccessToken(session.accessToken);
      accessStore.setRefreshToken(session.refreshToken);
      try {
        const user = await getCurrentUserApi();
        this.currentUser = user;
        accessStore.setAccessMenus(user.menus as unknown as MenuRecordRaw[]);
      } catch {
        this.clearSession();
      } finally {
        this.isRestored = true;
      }
    },
    async logout() {
      try {
        await logoutApi();
      } finally {
        this.clearSession();
      }
    },
    clearSession() {
      clearSession();
      const accessStore = useAccessStore();
      accessStore.setAccessToken(null);
      accessStore.setRefreshToken(null);
      resetAccessRoutes(router);
      this.currentUser = null;
      this.isRestored = true;
    },
  },
});
