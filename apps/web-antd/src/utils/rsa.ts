import { getRsaPublicKeyApi } from '#/api/core/rsa';

let publicKeyPromise: Promise<CryptoKey> | undefined;

export function encryptRsa(value: string) {
  publicKeyPromise ??= getPublicKey();
  return publicKeyPromise.then(async (publicKey) => {
    const encrypted = await crypto.subtle.encrypt(
      { name: 'RSA-OAEP' },
      publicKey,
      new TextEncoder().encode(value),
    );
    return arrayBufferToBase64(encrypted);
  });
}

async function getPublicKey() {
  const pem = await getRsaPublicKeyApi();
  const publicKeyData = base64ToArrayBuffer(
    pem.replace(/-----BEGIN PUBLIC KEY-----|-----END PUBLIC KEY-----|\s/g, ''),
  );
  return crypto.subtle.importKey(
    'spki',
    publicKeyData,
    { hash: 'SHA-256', name: 'RSA-OAEP' },
    false,
    ['encrypt'],
  );
}

function arrayBufferToBase64(buffer: ArrayBuffer) {
  let binary = '';
  for (const byte of new Uint8Array(buffer)) binary += String.fromCharCode(byte);
  return btoa(binary);
}

function base64ToArrayBuffer(value: string) {
  const binary = atob(value);
  const bytes = new Uint8Array(binary.length);
  for (let index = 0; index < binary.length; index++) bytes[index] = binary.charCodeAt(index);
  return bytes.buffer;
}
