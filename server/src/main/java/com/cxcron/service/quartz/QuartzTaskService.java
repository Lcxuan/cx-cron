package com.cxcron.service.quartz;

import com.cxcron.entity.ScheduledTaskDO;

import java.time.LocalDateTime;

public interface QuartzTaskService {

    /**
     * 创建或更新任务调度。
     *
     * @param task 定时任务
     */
    void schedule(ScheduledTaskDO task);

    /**
     * 恢复任务调度，并保留已有的未来触发时间。
     *
     * @param task 定时任务
     */
    void recover(ScheduledTaskDO task);

    /**
     * 安排每日随机任务的下一次执行。
     *
     * @param taskId 任务 ID
     */
    void scheduleNextRandom(Long taskId, LocalDateTime scheduledAt);

    /**
     * 暂停任务调度。
     *
     * @param task 定时任务
     */
    void pause(ScheduledTaskDO task);

    /**
     * 删除任务调度。
     *
     * @param task 定时任务
     */
    void delete(ScheduledTaskDO task);

    /**
     * 立即触发任务执行。
     *
     * @param task 定时任务
     */
    void trigger(ScheduledTaskDO task);
}
