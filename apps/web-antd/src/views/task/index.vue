<template>
  <section class="task-page" aria-labelledby="task-title">
    <div class="task-page__header">
      <div>
        <h1 id="task-title">任务管理</h1>
        <p>管理定时任务并查看执行结果。</p>
      </div>
      <a-button type="primary" @click="handleCreate">新建任务</a-button>
    </div>

    <a-table
      :columns="taskColumns"
      :data-source="tasks"
      :loading="isLoading"
      :pagination="false"
      :row-key="(task: TaskApi.Task) => task.id"
      :scroll="{ x: 960 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'enabled'">
          <TaskStatusTag :enabled="record.enabled" />
        </template>
        <template v-else-if="column.key === 'lastScheduleStatus'">
          <a-tag v-if="record.lastScheduleStatus === 'SUCCESS'" color="success">成功</a-tag>
          <a-tag v-else-if="record.lastScheduleStatus === 'FAILED'" color="error">失败</a-tag>
          <span v-else>-</span>
        </template>
        <template v-else-if="column.key === 'lastScheduleTime'">
          {{ record.lastScheduleTime || '-' }}
        </template>
        <template v-else-if="column.key === 'actions'">
          <a-space :size="0" wrap>
            <a-button type="link" size="small" :loading="actionLoadingId === record.id" @click="handleEdit(record)">
              编辑任务
            </a-button>
            <a-button
              type="link"
              size="small"
              :loading="actionLoadingId === record.id"
              @click="handleTaskAction(record, record.enabled === 1 ? 'disable' : 'enable')"
            >
              {{ record.enabled === 1 ? '关闭' : '启用' }}
            </a-button>
            <a-button
              type="link"
              size="small"
              :loading="actionLoadingId === record.id"
              @click="handleTaskAction(record, 'trigger')"
            >
              手动执行
            </a-button>
            <a-button type="link" size="small" @click="handleShowRecords(record)">
              执行记录
            </a-button>
            <a-button danger type="link" size="small" :loading="actionLoadingId === record.id" @click="handleDelete(record)">
              删除
            </a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-pagination
      v-if="total > 0"
      class="task-page__pagination"
      :current="pageNo"
      :page-size="pageSize"
      :total="total"
      :show-size-changer="true"
      :page-size-options="['10', '20', '50']"
      show-less-items
      @change="handlePageChange"
    />

    <a-modal
      :open="isFormOpen"
      :title="formMode === 'create' ? '新建任务' : '编辑任务'"
      :confirm-loading="isSubmitting"
      ok-text="保存"
      cancel-text="取消"
      @cancel="isFormOpen = false"
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
          :rules="formMode === 'create' ? [{ required: true, message: '请上传脚本' }] : []"
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

    <ExecutionRecords :open="isRecordsOpen" :task="recordTask" @close="isRecordsOpen = false" />
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { message, Modal } from 'ant-design-vue';
import type { FormInstance, UploadProps } from 'ant-design-vue';
import {
  createTaskApi,
  deleteTaskApi,
  getTaskPageApi,
  triggerTaskApi,
  updateTaskApi,
  updateTaskStatusApi,
  type TaskApi,
} from '#/api/task';
import { taskColumns } from './data';
import TaskStatusTag from './components/TaskStatusTag.vue';
import ExecutionRecords from './modules/ExecutionRecords.vue';

const tasks = ref<TaskApi.Task[]>([]);
const total = ref(0);
const pageNo = ref(1);
const pageSize = ref(10);
const isLoading = ref(false);
const isFormOpen = ref(false);
const isSubmitting = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const selectedTask = ref<TaskApi.Task>();
const recordTask = ref<TaskApi.Task>();
const isRecordsOpen = ref(false);
const actionLoadingId = ref<number>();
const formRef = ref<FormInstance>();
const form = reactive<TaskApi.TaskParams>({
  name: '',
  cronExpression: '',
  runCommand: '',
  enabled: 1,
});

async function loadTasks() {
  isLoading.value = true;
  try {
    const page = await getTaskPageApi({ pageNo: pageNo.value, pageSize: pageSize.value });
    tasks.value = page.list;
    total.value = page.total;
  } catch (error) {
    message.error(error instanceof Error ? error.message : '加载任务失败');
  } finally {
    isLoading.value = false;
  }
}

function handlePageChange(page: number, size: number) {
  pageNo.value = size !== pageSize.value ? 1 : page;
  pageSize.value = size;
  void loadTasks();
}

function resetForm(task?: TaskApi.Task) {
  form.name = task?.name ?? '';
  form.cronExpression = task?.cronExpression ?? '';
  form.runCommand = task?.runCommand ?? '';
  form.enabled = task?.enabled ?? 1;
  form.script = undefined;
  formRef.value?.clearValidate();
}

function handleCreate() {
  formMode.value = 'create';
  selectedTask.value = undefined;
  resetForm();
  isFormOpen.value = true;
}

function handleEdit(task: TaskApi.Task) {
  formMode.value = 'edit';
  selectedTask.value = task;
  resetForm(task);
  isFormOpen.value = true;
}

const selectScript: UploadProps['beforeUpload'] = (file) => {
  form.script = file;
  return false;
};

async function handleSubmit() {
  await formRef.value?.validate();
  await handleFormSubmit({ ...form });
}

async function handleFormSubmit(data: TaskApi.TaskParams) {
  isSubmitting.value = true;
  try {
    if (formMode.value === 'create') {
      await createTaskApi(data);
      message.success('任务已创建');
    } else if (selectedTask.value) {
      await updateTaskApi(selectedTask.value.id, data);
      message.success('任务已更新');
    }
    isFormOpen.value = false;
    await loadTasks();
  } catch (error) {
    message.error(error instanceof Error ? error.message : '保存任务失败');
  } finally {
    isSubmitting.value = false;
  }
}

function handleTaskAction(task: TaskApi.Task, action: 'disable' | 'enable' | 'trigger') {
  const actionText =
    action === 'trigger' ? '手动执行' : action === 'enable' ? '启用' : '关闭';

  Modal.confirm({
    title: `确认${actionText}任务？`,
    content: `任务“${task.name}”将被${actionText}。`,
    okText: '确认',
    cancelText: '取消',
    async onOk() {
      actionLoadingId.value = task.id;
      try {
        if (action === 'enable') await updateTaskStatusApi(task.id, 1);
        if (action === 'disable') await updateTaskStatusApi(task.id, 0);
        if (action === 'trigger') await triggerTaskApi(task.id);
        message.success(
          action === 'trigger'
            ? '已手动执行任务'
            : action === 'enable'
              ? '任务已启用'
              : '任务已关闭',
        );
        await loadTasks();
      } catch (error) {
        message.error(error instanceof Error ? error.message : '操作失败');
        throw error;
      } finally {
        actionLoadingId.value = undefined;
      }
    },
  });
}

function handleDelete(task: TaskApi.Task) {
  Modal.confirm({
    title: '确认删除任务？',
    content: `将删除任务“${task.cronExpression}”，此操作不可恢复。`,
    okText: '删除',
    okType: 'danger',
    cancelText: '取消',
    async onOk() {
      actionLoadingId.value = task.id;
      try {
        await deleteTaskApi(task.id);
        message.success('任务已删除');
        if (tasks.value.length === 1 && pageNo.value > 1) pageNo.value -= 1;
        await loadTasks();
      } catch (error) {
        message.error(error instanceof Error ? error.message : '删除任务失败');
        throw error;
      } finally {
        actionLoadingId.value = undefined;
      }
    },
  });
}

function handleShowRecords(task: TaskApi.Task) {
  recordTask.value = task;
  isRecordsOpen.value = true;
}

void loadTasks();
</script>

<style scoped>
.task-page {
  padding: 24px;
  background: #fff;
  border-radius: 8px;
}

.task-page__header {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 20px;
}

h1 {
  margin: 0;
  font-size: 20px;
}

p {
  margin: 8px 0 0;
  color: #64748b;
}

.task-page__pagination {
  margin-top: 16px;
  text-align: right;
}

@media (max-width: 576px) {
  .task-page {
    padding: 16px;
  }

  .task-page__header {
    flex-direction: column;
    align-items: stretch;
  }

  .task-page__header :deep(.ant-btn) {
    width: 100%;
  }

  .task-page__pagination {
    text-align: left;
  }
}
</style>
