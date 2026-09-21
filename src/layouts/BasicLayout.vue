<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useAuthStore } from '#/store/auth';

const router = useRouter();
const authStore = useAuthStore();

async function handleLogout() {
  await authStore.logout();
  await router.replace('/login');
}
</script>

<template>
  <a-layout class="app-layout">
    <a-layout-header class="app-header">
      <a-typography-title :level="3" class="brand">cx-cron</a-typography-title>
      <div class="header-actions">
        <span>{{ authStore.currentUser?.username }}</span>
        <a-button @click="handleLogout">退出登录</a-button>
      </div>
    </a-layout-header>
    <a-layout-content class="app-content">
      <nav aria-label="主导航">
        <RouterLink to="/tasks">任务管理</RouterLink>
      </nav>
      <main><RouterView /></main>
    </a-layout-content>
  </a-layout>
</template>
