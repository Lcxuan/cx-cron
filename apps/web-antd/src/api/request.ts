import type { RequestClientOptions } from '@vben/request';

import {
  authenticateResponseInterceptor,
  defaultResponseInterceptor,
  errorMessageResponseInterceptor,
  RequestClient,
} from '@vben/request';
import { useAccessStore } from '@vben/stores';
import { message } from 'ant-design-vue';

import { getSession, updateSession } from '#/store/session';

interface TokenResp {
  accessToken: string;
  refreshToken: string;
}

const baseURL = import.meta.env.VITE_API_BASE_URL;

function formatToken(token: null | string) {
  return token ? `Bearer ${token}` : null;
}

function normalizeResponseCode(response: any) {
  const code = response.data?.code;
  if (typeof code === 'string' && /^\d+$/.test(code)) {
    response.data.code = Number(code);
  }
  return response;
}

function createRequestClient(baseURL: string, options?: RequestClientOptions) {
  const client = new RequestClient({ ...options, baseURL });

  client.addResponseInterceptor({ fulfilled: normalizeResponseCode });
  client.addResponseInterceptor(
    defaultResponseInterceptor({
      codeField: 'code',
      dataField: 'data',
      successCode: 0,
    }),
  );

  return client;
}

export const baseRequestClient = createRequestClient(baseURL, {
  responseReturn: 'data',
});

export const requestClient = createRequestClient(baseURL, {
  responseReturn: 'data',
});

requestClient.addRequestInterceptor({
  fulfilled: (config) => {
    config.headers.Authorization = formatToken(getSession()?.accessToken ?? null);
    return config;
  },
});

requestClient.addResponseInterceptor(
  authenticateResponseInterceptor({
    client: requestClient,
    async doReAuthenticate() {
      const { useAuthStore } = await import('#/store/auth');
      const { router } = await import('#/router');
      useAuthStore().clearSession();
      message.error('登录已失效，请重新登录');
      await router.replace({ path: '/auth/login', query: { expired: '1' } });
    },
    async doRefreshToken() {
      const session = getSession();
      if (!session) throw new Error('登录已失效');

      const tokens = await baseRequestClient.post<TokenResp>('/client/auth/refresh', {
        refreshToken: session.refreshToken,
      });
      updateSession(tokens);

      const accessStore = useAccessStore();
      accessStore.setAccessToken(tokens.accessToken);
      accessStore.setRefreshToken(tokens.refreshToken);
      return tokens.accessToken;
    },
    enableRefreshToken: true,
    formatToken,
  }),
);

requestClient.addResponseInterceptor(
  errorMessageResponseInterceptor((msg, error) => {
    const code = error?.data?.code ?? error?.response?.data?.code;
    if (!getSession() || Number(code) === 401) return;
    message.error(error?.data?.msg ?? error?.response?.data?.msg ?? msg);
  }),
);
