import { initPreferences } from '@vben/preferences';
import { unmountGlobalLoading } from '@vben/utils';
import { overridesPreferences } from './preferences';

async function initApplication() {
  const env = import.meta.env.PROD ? 'prod' : 'dev';
  const appNamespace = import.meta.env.VITE_APP_NAMESPACE || 'cx-cron';
  const appVersion = import.meta.env.VITE_APP_VERSION || '0.0.0';
  const namespace = `${appNamespace}-${appVersion}-${env}`;

  await initPreferences({
    namespace,
    overrides: overridesPreferences,
  });

  const { bootstrap } = await import('./bootstrap');
  await bootstrap(namespace);
  unmountGlobalLoading();
}

void initApplication();
