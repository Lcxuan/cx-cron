import { baseRequestClient } from '#/api/request';

export function getRsaPublicKeyApi() {
  return baseRequestClient.get<string>('/client/auth/public-key');
}
