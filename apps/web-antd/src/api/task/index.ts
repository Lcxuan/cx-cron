import { requestClient } from '../request';
import type { PageParams, PageResult } from '../common/types';

export namespace TaskApi {
  /**
   * 定时任务信息。
   */
  export interface Task {
    id: number;
    name: string;
    cronExpression: string;
    scriptPath: string;
    runCommand: string;
    enabled: 0 | 1;
    quartzJobName: string;
    lastScheduleStatus: string | null;
    lastScheduleTime: string | null;
    createTime: string;
  }

  /**
   * 创建或编辑任务的表单参数。
   */
  export interface TaskParams {
    name: string;
    cronExpression: string;
    runCommand: string;
    enabled: 0 | 1;
    script?: File;
  }

  /**
   * 任务执行记录。
   */
  export interface ExecutionRecord {
    id: number;
    scheduledTaskId: number;
    triggerType: 'SCHEDULED' | 'MANUAL';
    idempotencyKey: string;
    resultType: 'SUCCESS' | 'FAILED';
    summary: string | null;
    quartzFireInstanceId: string | null;
    startedTime: string | null;
    finishedTime: string | null;
  }
}

/**
 * 将任务表单参数转换为 multipart/form-data。
 */
function taskFormData(data: TaskApi.TaskParams) {
  const formData = new FormData();
  formData.append('name', data.name);
  formData.append('cronExpression', data.cronExpression);
  formData.append('runCommand', data.runCommand);
  formData.append('enabled', String(data.enabled));
  if (data.script) formData.append('script', data.script);
  return formData;
}

/**
 * 分页获取任务列表。
 */
export function getTaskPageApi(params: PageParams) {
  return requestClient.get<PageResult<TaskApi.Task>>('/client/tasks', { params });
}

/**
 * 创建定时任务。
 */
export function createTaskApi(data: TaskApi.TaskParams) {
  return requestClient.post<number>('/client/tasks', taskFormData(data));
}

/**
 * 更新定时任务配置。
 */
export function updateTaskApi(taskId: number, data: TaskApi.TaskParams) {
  return requestClient.put<void>(`/client/tasks/${taskId}`, taskFormData(data));
}

/**
 * 更新任务启用状态。
 */
export function updateTaskStatusApi(taskId: number, enabled: 0 | 1) {
  return requestClient.put<void>(`/client/tasks/${taskId}/status`, { enabled });
}

/**
 * 删除任务。
 */
export function deleteTaskApi(taskId: number) {
  return requestClient.delete<void>(`/client/tasks/${taskId}`);
}

/**
 * 手动触发任务执行。
 */
export function triggerTaskApi(taskId: number) {
  return requestClient.post<void>(`/client/tasks/${taskId}/trigger`);
}

/**
 * 分页获取指定任务的执行记录。
 */
export function getTaskExecutionPageApi(taskId: number, params: PageParams) {
  return requestClient.get<PageResult<TaskApi.ExecutionRecord>>(
    `/client/tasks/${taskId}/executions`,
    { params },
  );
}
