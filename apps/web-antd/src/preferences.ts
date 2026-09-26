import { defineOverridesPreferences } from '@vben/preferences';

export const overridesPreferences = defineOverridesPreferences({
  app: {
    defaultHomePath: '/task',
    name: 'cx-cron',
  },
  logo: {
    enable: true,
  },
  theme: {
    mode: 'light',
  },
  widget: {
    fullscreen: false,
    globalSearch: false,
    languageToggle: false,
    lockScreen: false,
    notification: false,
    refresh: false,
    timezone: false,
    themeToggle: false,
  },
});
