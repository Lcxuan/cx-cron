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
          <a-tag :color="record.resultType === 'SUCCESS' ? 'success' : 'error'">
            {{ record.resultType === 'SUCCESS' ? '成功' : '失败' }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'summary'">
          {{ record.summary || '-' }}
        </template>
        <template v-else-if="column.key === 'startedTime' || column.key === 'finishedTime'">
          {{ record[column.key] || '-' }}
        </template>
      </template>
    </a-table>
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
.execution-pagination {
  margin-top: 16px;
  text-align: right;
}
</style>
