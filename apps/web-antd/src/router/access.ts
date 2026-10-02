import type { Component } from 'vue';
import type { ComponentRecordType, GenerateMenuAndRoutesOptions } from '@vben/types';
import { generateAccessible as vbenGenerateAccessible } from '@vben/access';
import { preferences } from '@vben/preferences';
import { useAccessStore } from '@vben/stores';
import { BasicLayout } from '@vben/layouts';

import { router } from '#/router';
import { convertServerMenuToRouteRecordStringComponent } from './menu-converter';
import type { ServerMenu } from './menu-converter';

const pageMap = import.meta.glob<Component>('../views/**/*.vue');
const layoutMap: ComponentRecordType = { BasicLayout: async () => BasicLayout };

export function generateAccess() {
  const accessStore = useAccessStore();
  const options: GenerateMenuAndRoutesOptions = {
    router,
    routes: [],
    fetchMenuListAsync: async () =>
      convertServerMenuToRouteRecordStringComponent(
        accessStore.accessMenus as unknown as ServerMenu[],
      ),
    layoutMap,
    pageMap,
  };
  return vbenGenerateAccessible(preferences.app.accessMode, options);
}
