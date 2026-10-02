import { requestClient } from '../request';
import type { PageParams, PageResult } from '../common/types';

export namespace EmailNotificationApi {
  /**
   * 邮件通知配置表单的数据结构。
   */
  export interface Config {
    enabled: boolean;
    recipient: string;
    fromAddress: string;
    smtpHost: string;
    smtpPort: number;
    smtpUsername: string;
    smtpProtocol: string;
    smtpPassword: string;
    passwordConfigured: boolean;
  }

  export type ConfigResponse = Omit<Config, 'smtpPassword'>;
  export type ConfigRequest = Omit<Config, 'passwordConfigured'>;

  /**
   * 邮件通知日志。
   */
  export interface Log {
    /** 日志 ID。 */
    id: number;
    /** 任务名称快照。 */
    taskName: string | null;
    /** Cron 表达式快照。 */
    cronExpression: string | null;
    /** 收件人邮箱。 */
    recipient: string | null;
    /** 通知状态。 */
    status: 'PENDING' | 'SENT' | 'FAILED' | 'SKIPPED';
    /** 日志创建时间。 */
    createTime: string;
    /** 邮件发送成功时间。 */
    sentTime: string | null;
    /** 发送失败或跳过原因。 */
    errorMessage: string | null;
    /** 邮件主题快照。 */
    subject: string | null;
    /** 邮件 HTML 正文快照。 */
    htmlBody: string | null;
  }

  /**
   * 邮件日志分页查询参数。
   */
  export interface LogQuery extends PageParams {
    /** 通知状态筛选。 */
    status?: string;
    /** 任务名称模糊筛选。 */
    taskName?: string;
    /** 收件人邮箱模糊筛选。 */
    recipient?: string;
    /** 创建时间范围起始值。 */
    createTimeStart?: string;
    /** 创建时间范围结束值。 */
    createTimeEnd?: string;
  }
}

/**
 * 分页查询邮件通知日志。
 */
export function getEmailNotificationLogPageApi(params: EmailNotificationApi.LogQuery) {
  return requestClient.get<PageResult<EmailNotificationApi.Log>>('/client/system/email-notification/logs', { params });
}

/**
 * 获取全局邮件通知配置。
 */
export function getEmailNotificationConfigApi() {
  return requestClient.get<EmailNotificationApi.ConfigResponse>('/client/system/email-notification/config');
}

/**
 * 保存全局邮件通知配置。
 */
export function updateEmailNotificationConfigApi(data: EmailNotificationApi.ConfigRequest) {
  return requestClient.put<void>('/client/system/email-notification/config', data);
}

/**
 * 向全局收件人发送测试邮件。
 */
export function testEmailNotificationApi() {
  return requestClient.post<void>('/client/system/email-notification/test');
}

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
    emailNotificationPolicy: 'OFF' | 'FAILURE' | 'ALL';
  }

  /**
   * 创建或编辑任务的表单参数。
   */
  export interface TaskParams {
    name: string;
    cronExpression: string;
    runCommand: string;
    enabled: 0 | 1;
    emailNotificationPolicy: 'OFF' | 'FAILURE' | 'ALL';
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
  formData.append('emailNotificationPolicy', data.emailNotificationPolicy);
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
  return requestClient.post<number>('/client/tasks', taskFormData(data), {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

/**
 * 更新定时任务配置。
 */
export function updateTaskApi(taskId: number, data: TaskApi.TaskParams) {
  return requestClient.put<void>(`/client/tasks/${taskId}`, taskFormData(data), {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
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
