package com.cxcron.service.email;

import com.cxcron.entity.ExecutionRecordDO;
import com.cxcron.entity.ScheduledTaskDO;

public interface TaskEmailNotificationService {
    /**
     * 按任务通知策略，在任务执行结束后安排邮件通知。
     *
     * @param task 已执行的定时任务
     * @param record 本次执行记录
     */
    void notifyAfterExecution(ScheduledTaskDO task, ExecutionRecordDO record);

    /**
     * 向全局配置的收件人发送测试邮件。
     */
    void sendTest();
}
