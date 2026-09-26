import type { TableColumnsType } from 'ant-design-vue';
import type { TaskApi } from '#/api/task';

export const taskColumns: TableColumnsType<TaskApi.Task> = [
  { dataIndex: 'name', key: 'name', title: '任务名称', ellipsis: true },
  { dataIndex: 'cronExpression', key: 'cronExpression', title: 'Cron 表达式', ellipsis: true },
  { key: 'enabled', title: '状态', width: 100 },
  { key: 'lastScheduleStatus', title: '最近执行结果', width: 130 },
  { dataIndex: 'lastScheduleTime', key: 'lastScheduleTime', title: '最近执行时间', width: 180 },
  { key: 'actions', title: '操作', width: 360 },
];

export const executionColumns: TableColumnsType<TaskApi.ExecutionRecord> = [
  { key: 'triggerType', title: '触发方式', width: 100 },
  { key: 'resultType', title: '结果', width: 90 },
  { dataIndex: 'summary', key: 'summary', title: '摘要', ellipsis: true },
  { dataIndex: 'startedTime', key: 'startedTime', title: '开始时间', width: 170 },
  { dataIndex: 'finishedTime', key: 'finishedTime', title: '结束时间', width: 170 },
];
