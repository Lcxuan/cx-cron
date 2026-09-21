<template>
  <main class="login-page">
    <a-card title="登录 cx-cron" class="login-card">
      <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        layout="vertical"
        @finish="handleLogin"
      >
        <a-form-item label="用户名" name="username">
          <a-input v-model:value="form.username" autocomplete="username" aria-label="用户名" />
        </a-form-item>
        <a-form-item label="密码" name="password">
          <a-input-password
            v-model:value="form.password"
            autocomplete="current-password"
            aria-label="密码"
          />
        </a-form-item>
        <a-button type="primary" html-type="submit" block :loading="authStore.isLoading">
          登录
        </a-button>
      </a-form>
    </a-card>
  </main>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { useAuthStore } from '#/store/auth';

const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();
const form = reactive({ username: '', password: '' });
const formRef = ref();
const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
};

async function handleLogin() {
  await formRef.value.validate();
  try {
    await authStore.login({ username: form.username, password: form.password });
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/tasks';
    await router.replace(redirect);
  } catch (error) {
    message.error(error instanceof Error ? error.message : '登录失败');
  }
}
</script>
