import { baseRequestClient, requestClient } from '#/api/request';
import { encryptRsa } from '#/utils/rsa';

export namespace AuthApi {
  /**
   * 登录请求参数。
   */
  export interface LoginParams {
    username: string;
    password: string;
  }

  /**
   * 修改密码请求参数。
   */
  export interface UpdatePasswordParams {
    newPassword: string;
    oldPassword: string;
  }

  /**
   * 登录或刷新 Token 的响应。
   */
  export interface TokenResp {
    accessToken: string;
    refreshToken: string;
    accessTokenExpiresIn: number;
    refreshTokenExpiresIn: number;
  }

  export interface MenuRouteResp {
    id: string;
    parentId: string;
    name: string;
    path: string;
    component: string;
    componentName: null | string;
    icon: null | string;
    visible: boolean;
    keepAlive: boolean;
    alwaysShow: boolean;
    children: MenuRouteResp[];
  }

  /**
   * 当前登录用户信息。
   */
  export interface CurrentUserResp {
    id: number;
    username: string;
    nickname: string;
    menus: MenuRouteResp[];
  }
}

/**
 * 使用账号密码登录。
 */
export async function loginApi(data: AuthApi.LoginParams) {
  return baseRequestClient.post<AuthApi.TokenResp>('/client/auth/login', {
    ...data,
    password: await encryptRsa(data.password),
  });
}

/**
 * 使用刷新 Token 获取新的双 Token。
 */
export function refreshApi(refreshToken: string) {
  return baseRequestClient.post<AuthApi.TokenResp>('/client/auth/refresh', { refreshToken });
}

/**
 * 退出当前登录会话。
 */
export function logoutApi() {
  return requestClient.post<void>('/client/auth/logout');
}

/**
 * 修改当前登录用户密码。
 */
export async function updatePasswordApi(data: AuthApi.UpdatePasswordParams) {
  const [newPassword, oldPassword] = await Promise.all([
    encryptRsa(data.newPassword),
    encryptRsa(data.oldPassword),
  ]);
  return requestClient.post<void>('/client/auth/password', { newPassword, oldPassword });
}

/**
 * 获取当前登录用户。
 */
export function getCurrentUserApi() {
  return requestClient.get<AuthApi.CurrentUserResp>('/client/auth/me');
}
