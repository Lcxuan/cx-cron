package com.cxcron.service.task;

import com.cxcron.common.pojo.PageResult;
import com.cxcron.controller.task.dto.TaskCreateReq;
import com.cxcron.controller.task.dto.TaskPageReq;
import com.cxcron.controller.task.dto.TaskUpdateReq;
import com.cxcron.controller.task.dto.TaskUpdateStatusReq;
import com.cxcron.controller.task.vo.ExecutionRecordResp;
import com.cxcron.controller.task.vo.ScheduledTaskResp;

public interface ScheduledTaskService {

    /**
     * 创建定时任务。
     *
     * @param request 任务创建参数
     * @return 任务 ID
     */
    Long create(TaskCreateReq request);

    /**
     * 更新定时任务配置。
     *
     * @param taskId 任务 ID
     * @param request 任务更新参数
     */
    void update(Long taskId, TaskUpdateReq request);

    /**
     * 更新任务启用状态。
     *
     * @param taskId 任务 ID
     * @param request 状态更新参数
     */
    void updateStatus(Long taskId, TaskUpdateStatusReq request);

    /**
     * 删除任务。
     *
     * @param taskId 任务 ID
     */
    void delete(Long taskId);

    /**
     * 手动触发任务执行。
     *
     * @param taskId 任务 ID
     */
    void trigger(Long taskId);

    /**
     * 分页查询任务。
     *
     * @param request 分页参数
     * @return 任务分页结果
     */
    PageResult<ScheduledTaskResp> getPage(TaskPageReq request);

    /**
     * 分页查询任务执行记录。
     *
     * @param taskId 任务 ID
     * @param request 分页参数
     * @return 执行记录分页结果
     */
    PageResult<ExecutionRecordResp> getExecutionPage(Long taskId, TaskPageReq request);
}
