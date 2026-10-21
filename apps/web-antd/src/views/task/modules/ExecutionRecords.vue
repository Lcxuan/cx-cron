<template>
  <a-drawer :open="open" title="执行记录" width="min(760px, 100vw)" @close="emit('close')">
    <a-table
      :columns="executionColumns"
      :data-source="records"
      :loading="isLoading"
      :pagination="false"
      :row-key="(record: TaskApi.ExecutionRecord) => record.id"
      :scroll="{ x: 720 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'triggerType'">
          {{ record.triggerType === 'SCHEDULED' ? '定时' : '手动' }}
        </template>
        <template v-else-if="column.key === 'resultType'">
          <a-tag :color="record.resultType === 'SUCCESS' ? 'success' : record.resultType === 'SKIPPED_CONCURRENT' ? 'warning' : 'error'">
            {{ record.resultType === 'SUCCESS' ? '成功' : record.resultType === 'SKIPPED_CONCURRENT' ? '并发跳过' : '失败' }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'summary'">
          {{ record.summary || '-' }}
        </template>
        <template v-else-if="column.key === 'startedTime' || column.key === 'finishedTime'">
          {{ record[column.key] || '-' }}
        </template>
        <template v-else-if="column.key === 'executionLog'">
          <a-button type="link" size="small" @click="showExecutionLog(record)">查看</a-button>
        </template>
      </template>
    </a-table>
    <a-modal v-model:open="isLogOpen" title="命令执行日志" :footer="null" width="min(900px, 100vw)">
      <a-alert message="日志可能包含敏感内容，请谨慎查看和分享。" type="warning" show-icon />
      <pre v-if="selectedRecord?.executionLog" class="execution-log">{{ selectedRecord.executionLog }}</pre>
      <a-empty v-else description="该记录没有执行日志" />
    </a-modal>
    <a-pagination
      v-if="total > 0"
      class="execution-pagination"
      :current="pageNo"
      :page-size="pageSize"
      :total="total"
      :show-size-changer="true"
      :page-size-options="['10', '20', '50']"
      show-less-items
      @change="handlePageChange"
    />
  </a-drawer>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { getTaskExecutionPageApi, type TaskApi } from '#/api/task';
import { executionColumns } from '../data';

const props = defineProps<{ open: boolean; task?: TaskApi.Task }>();
const emit = defineEmits<{ close: [] }>();

const records = ref<TaskApi.ExecutionRecord[]>([]);
const total = ref(0);
const pageNo = ref(1);
const pageSize = ref(10);
const isLoading = ref(false);
const isLogOpen = ref(false);
const selectedRecord = ref<TaskApi.ExecutionRecord>();

function showExecutionLog(record: TaskApi.ExecutionRecord) {
  selectedRecord.value = record;
  isLogOpen.value = true;
}

async function loadRecords() {
  if (!props.task) return;
  isLoading.value = true;
  try {
    const page = await getTaskExecutionPageApi(props.task.id, {
      pageNo: pageNo.value,
      pageSize: pageSize.value,
    });
    records.value = page.list;
    total.value = page.total;
  } finally {
    isLoading.value = false;
  }
}

function handlePageChange(page: number, size: number) {
  pageNo.value = size !== pageSize.value ? 1 : page;
  pageSize.value = size;
  void loadRecords();
}

watch(
  () => [props.open, props.task?.id] as const,
  ([open]) => {
    if (!open) return;
    pageNo.value = 1;
    void loadRecords();
  },
);
</script>

<style scoped>
.execution-log {
  max-height: 60vh;
  overflow: auto;
  padding: 12px;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
  background: #f5f5f5;
  border-radius: 4px;
}

.execution-pagination {
  margin-top: 16px;
  text-align: right;
}
</style>
