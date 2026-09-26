<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { BasicLayout, UserDropdown } from '@vben/layouts';
import { useAccessStore } from '@vben/stores';

import { useAuthStore } from '#/store/auth';

const router = useRouter();
const authStore = useAuthStore();
const accessStore = useAccessStore();
const nickname = computed(() => authStore.currentUser?.nickname ?? '');
const userMenus = [
  {
    handler: () => router.push('/profile/password'),
    icon: 'lucide:key-round',
    text: '修改密码',
  },
];

async function handleLogout() {
  await authStore.logout();
  accessStore.setAccessToken(null);
  accessStore.setRefreshToken(null);
  await router.replace('/auth/login');
}
</script>

<template>
  <BasicLayout :text="nickname" logo-text="cx-cron" @logout="handleLogout">
    <template #user-dropdown>
      <UserDropdown
        :text="nickname"
        :description="authStore.currentUser?.username"
        :menus="userMenus"
        @logout="handleLogout"
      />
    </template>
  </BasicLayout>
</template>
