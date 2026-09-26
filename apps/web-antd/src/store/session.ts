export interface Session {
  accessToken: string;
  refreshToken: string;
}

const sessionKey = 'cx-cron-session';

export function createAuthSession(session: Session): Session {
  return {
    accessToken: session.accessToken,
    refreshToken: session.refreshToken,
  };
}

export function getSession(): Session | null {
  const value = localStorage.getItem(sessionKey);
  return value ? (JSON.parse(value) as Session) : null;
}

export function saveSession(session: Session) {
  localStorage.setItem(sessionKey, JSON.stringify(createAuthSession(session)));
}

export function updateSession(session: Session) {
  saveSession(session);
}

export function clearSession() {
  localStorage.removeItem(sessionKey);
}
