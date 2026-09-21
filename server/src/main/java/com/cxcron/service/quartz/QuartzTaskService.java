package com.cxcron.service.quartz;

import com.cxcron.entity.ScheduledTaskDO;

public interface QuartzTaskService {

    /**
     * 创建或恢复任务调度。
     *
     * @param task 定时任务
     */
    void schedule(ScheduledTaskDO task);

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
