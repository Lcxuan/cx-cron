import type { RouteRecordRaw } from 'vue-router';

export interface ServerMenu {
  alwaysShow: boolean;
  children: ServerMenu[];
  component: string;
  componentName: null | string;
  icon: null | string;
  id: string;
  keepAlive: boolean;
  name: string;
  parentId: string;
  path: string;
  visible: boolean;
}

type RouteRecordStringComponent = Omit<RouteRecordRaw, 'children' | 'component'> & {
  children?: RouteRecordStringComponent[];
  component: string;
};

const PAGE_COMPONENT = /^[A-Za-z0-9_-]+(?:\/[A-Za-z0-9_-]+)*$/;

export function convertServerMenuToRouteRecordStringComponent(
  menus: ServerMenu[],
): RouteRecordStringComponent[] {
  return menus.map((menu) => {
    const isLayout = menu.component === 'BasicLayout';
    if (
      !menu.path.startsWith('/') ||
      menu.path.includes('..') ||
      (!isLayout && !PAGE_COMPONENT.test(menu.component)) ||
      (menu.componentName !== null && !/^[A-Z][A-Za-z0-9]*$/.test(menu.componentName)) ||
      (!isLayout && !menu.componentName)
    ) {
      throw new TypeError(`Invalid menu route: ${menu.path}`);
    }

    return {
      name: menu.componentName ?? `Menu_${menu.id}`,
      path: menu.path,
      component: menu.component,
      meta: {
        title: menu.name,
        ...(menu.icon ? { icon: menu.icon } : {}),
        hideInMenu: menu.visible,
        keepAlive: menu.keepAlive,
        alwaysShow: menu.alwaysShow,
      },
      ...(menu.children.length > 0
        ? { children: convertServerMenuToRouteRecordStringComponent(menu.children) }
        : {}),
    };
  });
}
