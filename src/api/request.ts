import axios, { type AxiosError, type AxiosInstance, type AxiosRequestConfig } from 'axios';
import { clearSession, getSession, updateSession } from '#/store/session';

interface ApiResponse<T> {
  code: string;
  data: T;
  msg: string;
}

interface RequestClient {
  get<T>(url: string, config?: AxiosRequestConfig): Promise<T>;
  post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>;
  put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>;
  delete<T>(url: string, config?: AxiosRequestConfig): Promise<T>;
}

interface TokenResp {
  accessToken: string;
  refreshToken: string;
}

declare module 'axios' {
  export interface AxiosRequestConfig {
    _retry?: boolean;
  }
}

const baseURL = import.meta.env.VITE_API_BASE_URL;
let refreshPromise: Promise<void> | null = null;

function formatToken(token?: string) {
  return token ? `Bearer ${token}` : undefined;
}

function unwrapResponse<T>(body: ApiResponse<T>) {
  if (body.code !== '0') throw new Error(body.msg);
  return body.data;
}

function createRequestClient(instance: AxiosInstance): RequestClient {
  instance.interceptors.response.use((response) => unwrapResponse(response.data as ApiResponse<unknown>));

  return {
    get: <T>(url: string, config?: AxiosRequestConfig) => instance.get(url, config) as Promise<T>,
    post: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
      instance.post(url, data, config) as Promise<T>,
    put: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
      instance.put(url, data, config) as Promise<T>,
    delete: <T>(url: string, config?: AxiosRequestConfig) => instance.delete(url, config) as Promise<T>,
  };
}

const baseInstance = axios.create({ baseURL });
export const baseRequestClient = createRequestClient(baseInstance);

const requestInstance = axios.create({ baseURL });

requestInstance.interceptors.request.use((config) => {
  config.headers.Authorization = formatToken(getSession()?.accessToken);
  return config;
});

requestInstance.interceptors.response.use(undefined, async (error: AxiosError) => {
  const config = error.config;
  if (error.response?.status !== 401 || !config || config._retry) return Promise.reject(error);

  config._retry = true;
  try {
    refreshPromise ??= refreshAccessToken().finally(() => {
      refreshPromise = null;
    });
    await refreshPromise;
    return requestInstance.request(config);
  } catch (refreshError) {
    clearSession();
    return Promise.reject(refreshError);
  }
});

async function refreshAccessToken() {
  const session = getSession();
  if (!session) throw new Error('登录已失效');

  const tokens = await baseRequestClient.post<TokenResp>('/client/auth/refresh', {
    refreshToken: session.refreshToken,
  });
  updateSession(tokens);
}

export const requestClient = createRequestClient(requestInstance);
