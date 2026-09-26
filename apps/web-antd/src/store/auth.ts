import { defineStore } from 'pinia';
import { useAccessStore } from '@vben/stores';

import { getCurrentUserApi, loginApi, logoutApi, type AuthApi } from '#/api/core/auth';
import { clearSession, getSession, saveSession } from './session';

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
        accessStore.setAccessToken(tokens.accessToken);
        accessStore.setRefreshToken(tokens.refreshToken);
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
        this.isRestored = true;
        return;
      }
      const accessStore = useAccessStore();
      accessStore.setAccessToken(session.accessToken);
      accessStore.setRefreshToken(session.refreshToken);
      try {
        this.currentUser = await getCurrentUserApi();
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
      this.currentUser = null;
      this.isRestored = true;
    },
  },
});
