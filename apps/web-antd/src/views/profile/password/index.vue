<template>
  <main class="password-page">
    <a-card title="修改密码" class="password-card">
      <a-form ref="formRef" :model="form" :rules="rules" layout="vertical" @finish="handleSubmit">
        <a-form-item label="当前密码" name="oldPassword">
          <a-input-password
            v-model:value="form.oldPassword"
            autocomplete="current-password"
            aria-label="当前密码"
          />
        </a-form-item>
        <a-form-item label="新密码" name="newPassword">
          <a-input-password
            v-model:value="form.newPassword"
            autocomplete="new-password"
            aria-label="新密码"
          />
        </a-form-item>
        <a-form-item label="确认新密码" name="confirmPassword">
          <a-input-password
            v-model:value="form.confirmPassword"
            autocomplete="new-password"
            aria-label="确认新密码"
          />
        </a-form-item>
        <div class="actions">
          <a-button @click="router.back()">取消</a-button>
          <a-button type="primary" html-type="submit" :loading="isSubmitting">确认修改</a-button>
        </div>
      </a-form>
    </a-card>
  </main>
</template>

<script setup lang="ts">
import type { FormInstance } from 'ant-design-vue';

import { message } from 'ant-design-vue';
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

import { updatePasswordApi } from '#/api/core/auth';
import { useAuthStore } from '#/store/auth';

const router = useRouter();
const authStore = useAuthStore();
const formRef = ref<FormInstance>();
const isSubmitting = ref(false);
const form = reactive({ confirmPassword: '', newPassword: '', oldPassword: '' });
const rules = {
  confirmPassword: [
    { required: true, message: '请确认新密码' },
    {
      validator: async (_rule: unknown, value: string) => {
        if (value !== form.newPassword) throw new Error('两次输入的新密码不一致');
      },
    },
  ],
  newPassword: [{ required: true, message: '请输入新密码' }],
  oldPassword: [{ required: true, message: '请输入当前密码' }],
};

async function handleSubmit() {
  await formRef.value?.validate();
  isSubmitting.value = true;
  try {
    await updatePasswordApi({ newPassword: form.newPassword, oldPassword: form.oldPassword });
    authStore.clearSession();
    message.success('密码修改成功，请重新登录');
    await router.replace('/auth/login');
  } finally {
    isSubmitting.value = false;
  }
}
</script>

<style scoped>
.password-page {
  padding: 24px;
}

.password-card {
  max-width: 480px;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
