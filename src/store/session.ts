export interface Session {
  accessToken: string;
  refreshToken: string;
}

const sessionKey = 'cx-cron-session';

export function getSession(): Session | null {
  const value = localStorage.getItem(sessionKey);
  return value ? (JSON.parse(value) as Session) : null;
}

export function saveSession(session: Session) {
  localStorage.setItem(sessionKey, JSON.stringify(session));
}

export function updateSession(session: Session) {
  localStorage.setItem(sessionKey, JSON.stringify(session));
}

export function clearSession() {
  localStorage.removeItem(sessionKey);
}
