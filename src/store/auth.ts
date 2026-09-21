import { defineStore } from 'pinia';
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
        this.isRestored = false;
        await this.restoreSession();
      } finally {
        this.isLoading = false;
      }
    },
    async restoreSession() {
      if (this.isRestored) return;
      if (!getSession()) {
        this.isRestored = true;
        return;
      }
      try {
        this.currentUser = await getCurrentUserApi();
      } catch (error) {
        this.clearSession();
        throw error;
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
      this.currentUser = null;
      this.isRestored = true;
    },
  },
});
