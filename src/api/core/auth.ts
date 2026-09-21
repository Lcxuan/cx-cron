import { baseRequestClient, requestClient } from '#/api/request';

export namespace AuthApi {
  /**
   * 登录请求参数。
   */
  export interface LoginParams {
    username: string;
    password: string;
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

  /**
   * 当前登录用户信息。
   */
  export interface CurrentUserResp {
    id: number;
    username: string;
    nickname: string;
  }
}

/**
 * 使用账号密码登录。
 */
export function loginApi(data: AuthApi.LoginParams) {
  return baseRequestClient.post<AuthApi.TokenResp>('/client/auth/login', data);
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
 * 获取当前登录用户。
 */
export function getCurrentUserApi() {
  return requestClient.get<AuthApi.CurrentUserResp>('/client/auth/me');
}
