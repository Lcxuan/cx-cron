<template>
  <section class="email-config" aria-labelledby="email-config-title">
    <div class="email-config__header">
      <div>
        <h1 id="email-config-title">邮件通知配置</h1>
        <p>配置全局 SMTP 发件信息和任务通知收件人。</p>
      </div>
      <a-space>
        <a-button :loading="isTesting" @click="handleTest">发送测试邮件</a-button>
        <a-button type="primary" :loading="isSaving" @click="handleSave">保存配置</a-button>
      </a-space>
    </div>

    <a-form ref="formRef" :model="form" layout="vertical" class="email-config__form">
      <a-form-item label="启用邮件通知" name="enabled">
        <a-switch v-model:checked="form.enabled" />
      </a-form-item>
      <a-form-item label="全局收件人" name="recipient" :rules="[{ required: true, message: '请输入收件人邮箱' }, { type: 'email', message: '请输入有效邮箱' }]">
        <a-input v-model:value="form.recipient" placeholder="name@example.com" />
      </a-form-item>
      <a-form-item label="发件人地址" name="fromAddress" :rules="form.fromAddress ? [{ type: 'email', message: '请输入有效邮箱' }] : []">
        <a-input v-model:value="form.fromAddress" />
      </a-form-item>
      <a-form-item label="SMTP 主机" name="smtpHost" :rules="[{ required: true, message: '请输入 SMTP 主机' }]">
        <a-input v-model:value="form.smtpHost" />
      </a-form-item>
      <a-form-item label="SMTP 端口" name="smtpPort" :rules="[{ required: true, message: '请输入 SMTP 端口' }]">
        <a-input-number v-model:value="form.smtpPort" :min="1" :max="65535" class="email-config__port" />
      </a-form-item>
      <a-form-item label="SMTP 用户名" name="smtpUsername" :rules="[{ required: true, message: '请输入 SMTP 用户名' }]">
        <a-input v-model:value="form.smtpUsername" />
      </a-form-item>
      <a-form-item label="SMTP 协议" name="smtpProtocol" :rules="[{ required: true, message: '请选择 SMTP 协议' }]">
        <a-select v-model:value="form.smtpProtocol">
          <a-select-option value="smtp">SMTP / STARTTLS</a-select-option>
          <a-select-option value="smtps">SMTPS</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="SMTP 密码" name="smtpPassword" :rules="[{ required: true, message: '请输入 SMTP 密码' }]">
        <a-input-password v-model:value="form.smtpPassword" autocomplete="new-password" placeholder="请输入 SMTP 密码" />
      </a-form-item>
    </a-form>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { message } from 'ant-design-vue';
import type { FormInstance } from 'ant-design-vue';
import {
  getEmailNotificationConfigApi,
  testEmailNotificationApi,
  updateEmailNotificationConfigApi,
  type EmailNotificationApi,
} from '#/api/task';

const formRef = ref<FormInstance>();
const isSaving = ref(false);
const isTesting = ref(false);
const form = reactive<EmailNotificationApi.Config>({
  enabled: false,
  recipient: '',
  fromAddress: '',
  smtpHost: '',
  smtpPort: 587,
  smtpUsername: '',
  smtpProtocol: '',
  smtpPassword: '',
  passwordConfigured: false,
});

async function loadConfig() {
  try {
    Object.assign(form, await getEmailNotificationConfigApi(), { smtpPassword: '' });
  } catch (error) {
    message.error(error instanceof Error ? error.message : '加载邮件配置失败');
  }
}

async function handleSave() {
  await formRef.value?.validate();
  isSaving.value = true;
  try {
    await updateEmailNotificationConfigApi({
      ...form,
    });
    message.success('邮件配置已保存');
    await loadConfig();
  } catch (error) {
    message.error(error instanceof Error ? error.message : '保存邮件配置失败');
  } finally {
    isSaving.value = false;
  }
}

async function handleTest() {
  isTesting.value = true;
  try {
    await testEmailNotificationApi();
    message.success('测试邮件已发送至全局收件人');
  } catch (error) {
    message.error(error instanceof Error ? error.message : '发送测试邮件失败');
  } finally {
    isTesting.value = false;
  }
}

onMounted(loadConfig);
</script>

<style scoped>
.email-config {
  padding: 24px;
  background: #fff;
  border-radius: 8px;
}

.email-config__header {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 20px;
}

.email-config__header h1 {
  margin: 0;
  font-size: 20px;
}

.email-config__header p {
  margin: 8px 0 0;
  color: #64748b;
}

.email-config__form {
  max-width: 640px;
}

.email-config__port {
  width: 100%;
}

@media (max-width: 576px) {
  .email-config {
    padding: 16px;
  }

  .email-config__header {
    flex-direction: column;
    align-items: stretch;
  }

  .email-config__header :deep(.ant-space) {
    width: 100%;
  }

  .email-config__header :deep(.ant-space-item),
  .email-config__header :deep(.ant-btn) {
    flex: 1;
  }
}
</style>
