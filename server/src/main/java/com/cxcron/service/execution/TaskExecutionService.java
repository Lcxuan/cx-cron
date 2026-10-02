package com.cxcron.service.execution;

public interface TaskExecutionService {

    /**
     * 执行指定任务，并记录触发方式及 Quartz 触发实例 ID。
     *
     * @param taskId 定时任务 ID
     * @param triggerType 触发方式
     * @param quartzFireInstanceId Quartz 触发实例 ID；手动触发时可为空
     */
    void execute(Long taskId, String triggerType, String quartzFireInstanceId);
}
