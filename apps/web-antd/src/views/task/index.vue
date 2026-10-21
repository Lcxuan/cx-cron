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
        <template v-else-if="column.key === 'schedule'">
          <span v-if="record.scheduleType === 'RANDOM_DAILY'">
            每日随机 {{ record.randomStartTime }}–{{ record.randomEndTime }}
          </span>
          <span v-else>{{ record.cronExpression }}</span>
        </template>
        <template v-else-if="column.key === 'lastScheduleStatus'">
          <a-tag v-if="record.lastScheduleStatus === 'SUCCESS'" color="success">成功</a-tag>
          <a-tag v-else-if="record.lastScheduleStatus === 'FAILED'" color="error">失败</a-tag>
          <a-tag v-else-if="record.lastScheduleStatus === 'SKIPPED_CONCURRENT'" color="warning">并发跳过</a-tag>
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
        <a-form-item label="调度方式" name="scheduleType">
          <a-select v-model:value="form.scheduleType">
            <a-select-option value="CRON">Cron 定时</a-select-option>
            <a-select-option value="RANDOM_DAILY">每日随机一次</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          v-if="form.scheduleType === 'CRON'"
          label="Cron 表达式"
          name="cronExpression"
          :rules="[{ required: true, message: '请输入 Cron 表达式' }]"
        >
          <a-input v-model:value="form.cronExpression" placeholder="例如：0 0 * * * ?" />
        </a-form-item>
        <a-form-item
          v-else
          label="每日随机时间范围"
          name="randomTimeRange"
          :rules="[{ validator: validateRandomRange }]"
        >
          <a-space>
            <a-input v-model:value="form.randomStartTime" type="time" step="60" />
            <span>至</span>
            <a-input v-model:value="form.randomEndTime" type="time" step="60" />
          </a-space>
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
          <a-space direction="vertical" size="small" class="task-script-field">
            <div
              v-if="formMode === 'edit' && selectedTask?.scriptFilename && !form.script"
              class="task-script-file"
            >
              <div class="task-script-file__info">
                <span class="task-script-file__name">{{ selectedTask.scriptFilename }}</span>
                <span class="task-script-file__hint">当前已上传的脚本</span>
              </div>
              <a-button
                class="task-script-file__download"
                :loading="isScriptDownloading"
                @click="handleDownloadTaskScript"
              >
                <template #icon>
                  <svg viewBox="0 0 24 24" aria-hidden="true" class="task-script-file__icon">
                    <path d="M12 3v12m0 0 4-4m-4 4-4-4M5 17v3h14v-3" />
                  </svg>
                </template>
                下载
              </a-button>
            </div>
            <a-upload
              :before-upload="selectScript"
              :file-list="scriptUploadFiles"
              :max-count="1"
              @remove="removeSelectedScript"
            >
              <a-button>选择脚本</a-button>
            </a-upload>
          </a-space>
        </a-form-item>
        <a-form-item label="邮件通知" name="emailNotificationPolicy">
          <a-radio-group v-model:value="form.emailNotificationPolicy">
            <a-radio value="OFF">关闭</a-radio>
            <a-radio value="FAILURE">仅失败时通知</a-radio>
            <a-radio value="ALL">每次执行都通知</a-radio>
          </a-radio-group>
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
import { computed, reactive, ref } from 'vue';
import { message, Modal } from 'ant-design-vue';
import type { FormInstance, UploadProps } from 'ant-design-vue';
import {
  createTaskApi,
  deleteTaskApi,
  downloadTaskScriptApi,
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
const isScriptDownloading = ref(false);
const formRef = ref<FormInstance>();
const form = reactive<TaskApi.TaskParams>({
  name: '',
  scheduleType: 'CRON',
  cronExpression: '',
  runCommand: '',
  enabled: 1,
  emailNotificationPolicy: 'ALL',
});
const scriptUploadFiles = computed(() =>
  form.script ? [{ uid: 'selected-script', name: form.script.name, status: 'done' as const }] : [],
);

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

function validateRandomRange() {
  if (!form.randomStartTime || !form.randomEndTime) {
    return Promise.reject(new Error('请选择随机执行的开始和结束时间'));
  }
  if (form.randomStartTime >= form.randomEndTime) {
    return Promise.reject(new Error('结束时间必须晚于开始时间'));
  }
  return Promise.resolve();
}

function resetForm(task?: TaskApi.Task) {
  form.name = task?.name ?? '';
  form.scheduleType = task?.scheduleType ?? 'CRON';
  form.cronExpression = task?.cronExpression ?? '';
  form.randomStartTime = task?.randomStartTime ?? undefined;
  form.randomEndTime = task?.randomEndTime ?? undefined;
  form.runCommand = task?.runCommand ?? '';
  form.enabled = task?.enabled ?? 1;
  form.emailNotificationPolicy = task?.emailNotificationPolicy ?? 'ALL';
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

function removeSelectedScript() {
  form.script = undefined;
}

async function handleDownloadTaskScript() {
  if (!selectedTask.value) return;
  isScriptDownloading.value = true;
  try {
    const blob = await downloadTaskScriptApi(selectedTask.value.id);
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = selectedTask.value.scriptFilename || 'script';
    link.click();
    URL.revokeObjectURL(url);
  } catch (error) {
    message.error(error instanceof Error ? error.message : '下载脚本失败');
  } finally {
    isScriptDownloading.value = false;
  }
}

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
.task-script-field {
  width: 100%;
}

.task-script-file {
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #f8fafc;
}

.task-script-file__info {
  display: flex;
  flex-direction: column;
  min-width: 0;
  gap: 2px;
}

.task-script-file__name {
  overflow: hidden;
  color: #1e293b;
  font-weight: 500;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-script-file__hint {
  color: #64748b;
  font-size: 12px;
}

.task-script-file__download {
  flex: none;
  color: #2563eb;
  border-color: #bfdbfe;
  background: #fff;
}

.task-script-file__download:hover {
  color: #1d4ed8;
  border-color: #93c5fd;
  background: #eff6ff;
}

.task-script-file__icon {
  width: 14px;
  height: 14px;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 1.8;
}

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
