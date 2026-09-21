<template>
  <a-modal
    :open="open"
    :title="mode === 'create' ? '新建任务' : '编辑任务'"
    :confirm-loading="submitting"
    ok-text="保存"
    cancel-text="取消"
    @cancel="emit('cancel')"
    @ok="handleSubmit"
  >
    <a-form ref="formRef" :model="form" layout="vertical">
      <a-form-item
        label="任务名称"
        name="name"
        :rules="[{ required: true, message: '请输入任务名称' }]"
      >
        <a-input v-model:value="form.name" />
      </a-form-item>
      <a-form-item
        label="Cron 表达式"
        name="cronExpression"
        :rules="[{ required: true, message: '请输入 Cron 表达式' }]"
      >
        <a-input v-model:value="form.cronExpression" placeholder="例如：0 0 * * * ?" />
      </a-form-item>
      <a-form-item
        label="运行命令"
        name="runCommand"
        :rules="[{ required: true, message: '请输入运行命令' }]"
        extra="{script} 占位符代表上传脚本的路径，可重复使用，例如：py {script} checkin"
      >
        <a-input v-model:value="form.runCommand" placeholder="例如：py {script} checkin" />
      </a-form-item>
      <a-form-item
        label="脚本"
        name="script"
        :rules="mode === 'create' ? [{ required: true, message: '请上传脚本' }] : []"
      >
        <a-upload :before-upload="selectScript" :max-count="1" @remove="form.script = undefined">
          <a-button>选择脚本</a-button>
        </a-upload>
      </a-form-item>
      <a-form-item
        label="启用状态"
        name="enabled"
        :rules="[{ required: true, message: '请选择启用状态' }]"
      >
        <a-radio-group v-model:value="form.enabled">
          <a-radio :value="1">启用</a-radio>
          <a-radio :value="0">关闭</a-radio>
        </a-radio-group>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import type { FormInstance, UploadProps } from 'ant-design-vue';
import type { TaskApi } from '#/api/task';

const props = defineProps<{
  mode: 'create' | 'edit';
  open: boolean;
  submitting: boolean;
  task?: TaskApi.Task;
}>();
const emit = defineEmits<{ cancel: []; submit: [data: TaskApi.TaskParams] }>();
const formRef = ref<FormInstance>();
const form = reactive<TaskApi.TaskParams>({
  name: '',
  cronExpression: '',
  runCommand: '',
  enabled: 1,
});

watch(
  () => [props.open, props.task] as const,
  ([open, task]) => {
    if (!open) return;
    form.name = task?.name ?? '';
    form.cronExpression = task?.cronExpression ?? '';
    form.runCommand = task?.runCommand ?? '';
    form.enabled = task?.enabled ?? 1;
    form.script = undefined;
    formRef.value?.clearValidate();
  },
  { immediate: true },
);

const selectScript: UploadProps['beforeUpload'] = (file) => {
  form.script = file;
  return false;
};

async function handleSubmit() {
  await formRef.value?.validate();
  emit('submit', { ...form });
}
</script>
