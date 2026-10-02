<template>
  <section class="email-logs" aria-labelledby="email-logs-title">
    <div class="email-logs__header">
      <div>
        <h1 id="email-logs-title">邮件日志</h1>
        <p>查看任务邮件通知的发送状态及邮件内容快照。</p>
      </div>
    </div>

    <a-form class="email-logs__filters" layout="inline" @submit.prevent="handleSearch">
      <a-form-item label="状态">
        <a-select v-model:value="filters.status" allow-clear placeholder="全部状态" class="email-logs__status">
          <a-select-option value="PENDING">待发送</a-select-option>
          <a-select-option value="SENT">已发送</a-select-option>
          <a-select-option value="FAILED">发送失败</a-select-option>
          <a-select-option value="SKIPPED">已跳过</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="任务名称">
        <a-input v-model:value="filters.taskName" allow-clear placeholder="模糊匹配任务名称" @press-enter="handleSearch" />
      </a-form-item>
      <a-form-item label="收件人">
        <a-input v-model:value="filters.recipient" allow-clear placeholder="匹配收件人" @press-enter="handleSearch" />
      </a-form-item>
      <a-form-item label="创建时间">
        <a-range-picker v-model:value="dateRange" show-time />
      </a-form-item>
      <a-form-item>
        <a-space>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button @click="handleReset">重置</a-button>
        </a-space>
      </a-form-item>
    </a-form>

    <a-table
      :columns="columns"
      :data-source="logs"
      :loading="isLoading"
      :pagination="false"
      :row-key="(item: EmailNotificationApi.Log) => item.id"
      :scroll="{ x: 1200 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag v-if="record.status === 'PENDING'" color="processing">待发送</a-tag>
          <a-tag v-else-if="record.status === 'SENT'" color="success">已发送</a-tag>
          <a-tag v-else-if="record.status === 'FAILED'" color="error">发送失败</a-tag>
          <a-tag v-else color="default">已跳过</a-tag>
        </template>
        <template v-else-if="column.key === 'errorMessage'">
          <span>{{ record.errorMessage || '-' }}</span>
        </template>
        <template v-else-if="column.key === 'content'">
          <a-button type="link" size="small" :disabled="!record.subject && !record.htmlBody" @click="showContent(record)">查看邮件</a-button>
        </template>
      </template>
    </a-table>

    <a-pagination
      v-if="total > 0"
      class="email-logs__pagination"
      :current="pageNo"
      :page-size="pageSize"
      :total="total"
      :show-size-changer="true"
      :page-size-options="['10', '20', '50']"
      show-less-items
      @change="handlePageChange"
    />

    <a-modal v-model:open="isContentOpen" title="邮件内容快照" :footer="null" width="800px">
      <h3 class="email-logs__subject">{{ selectedLog?.subject || '（无主题）' }}</h3>
      <div v-if="selectedLog?.htmlBody" class="email-logs__body" v-html="selectedLog.htmlBody" />
      <a-empty v-else description="该记录没有保存邮件正文" />
    </a-modal>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { message } from 'ant-design-vue';
import { getEmailNotificationLogPageApi, type EmailNotificationApi } from '#/api/task';
import type { TableColumnsType } from 'ant-design-vue';

const columns: TableColumnsType<EmailNotificationApi.Log> = [
  { dataIndex: 'taskName', key: 'taskName', title: '任务名称', width: 160, ellipsis: true },
  { dataIndex: 'cronExpression', key: 'cronExpression', title: 'Cron 表达式', width: 150, ellipsis: true },
  { dataIndex: 'recipient', key: 'recipient', title: '收件人', width: 180, ellipsis: true },
  { key: 'status', title: '状态', width: 100 },
  { dataIndex: 'createTime', key: 'createTime', title: '创建时间', width: 180 },
  { dataIndex: 'sentTime', key: 'sentTime', title: '发送时间', width: 180 },
  { key: 'errorMessage', title: '失败/跳过原因', width: 200, ellipsis: true },
  { key: 'content', title: '邮件内容', width: 100 },
];
const logs = ref<EmailNotificationApi.Log[]>([]);
const total = ref(0);
const pageNo = ref(1);
const pageSize = ref(10);
const isLoading = ref(false);
const isContentOpen = ref(false);
const selectedLog = ref<EmailNotificationApi.Log>();
const filters = reactive({ status: undefined as string | undefined, taskName: '', recipient: '' });
const dateRange = ref<[{ format: (format: string) => string }, { format: (format: string) => string }]>();

async function loadLogs() {
  isLoading.value = true;
  try {
    const page = await getEmailNotificationLogPageApi({
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      status: filters.status,
      taskName: filters.taskName || undefined,
      recipient: filters.recipient || undefined,
      createTimeStart: dateRange.value?.[0]?.format('YYYY-MM-DDTHH:mm:ss'),
      createTimeEnd: dateRange.value?.[1]?.format('YYYY-MM-DDTHH:mm:ss'),
    });
    logs.value = page.list;
    total.value = page.total;
  } catch (error) {
    message.error(error instanceof Error ? error.message : '加载邮件日志失败');
  } finally {
    isLoading.value = false;
  }
}

function handleSearch() {
  pageNo.value = 1;
  void loadLogs();
}

function handleReset() {
  filters.status = undefined;
  filters.taskName = '';
  filters.recipient = '';
  dateRange.value = undefined;
  handleSearch();
}

function handlePageChange(page: number, size: number) {
  pageNo.value = size !== pageSize.value ? 1 : page;
  pageSize.value = size;
  void loadLogs();
}

function showContent(log: EmailNotificationApi.Log) {
  selectedLog.value = log;
  isContentOpen.value = true;
}

onMounted(loadLogs);
</script>

<style scoped>
.email-logs {
  padding: 24px;
  background: #fff;
  border-radius: 8px;
}

.email-logs__header {
  margin-bottom: 20px;
}

.email-logs__header h1 {
  margin: 0;
  font-size: 20px;
}

.email-logs__header p {
  margin: 8px 0 0;
  color: #64748b;
}

.email-logs__filters {
  margin-bottom: 20px;
  gap: 8px 0;
}

.email-logs__status {
  width: 130px;
}

.email-logs__pagination {
  margin-top: 16px;
  text-align: right;
}

.email-logs__subject {
  margin-bottom: 16px;
}

.email-logs__body {
  max-height: 65vh;
  overflow: auto;
  padding: 16px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
}

@media (max-width: 576px) {
  .email-logs {
    padding: 16px;
  }
}
</style>
