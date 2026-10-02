package com.cxcron.service.task.impl;

import com.cxcron.common.enums.CommonStatusEnum;
import com.cxcron.common.pojo.PageResult;
import com.cxcron.controller.task.dto.TaskCreateReq;
import com.cxcron.controller.task.dto.TaskPageReq;
import com.cxcron.controller.task.dto.TaskUpdateReq;
import com.cxcron.controller.task.dto.TaskUpdateStatusReq;
import com.cxcron.controller.task.vo.ExecutionRecordResp;
import com.cxcron.controller.task.vo.ScheduledTaskResp;
import com.cxcron.convert.scheduledtask.ScheduledTaskConvert;
import com.cxcron.entity.ExecutionRecordDO;
import com.cxcron.entity.ScheduledTaskDO;
import com.cxcron.enums.exception.BusinessErrorCodeConstants;
import com.cxcron.enums.exception.BusinessException;
import com.cxcron.mapper.ExecutionRecordMapper;
import com.cxcron.mapper.ScheduledTaskMapper;
import com.cxcron.quartz.TaskQuartzConstants;
import com.cxcron.service.quartz.QuartzTaskService;
import com.cxcron.service.task.ScheduledTaskService;
import com.cxcron.service.task.ScriptStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduledTaskServiceImpl implements ScheduledTaskService {

    private final ScheduledTaskMapper scheduledTaskMapper;

    private final ExecutionRecordMapper executionRecordMapper;

    private final QuartzTaskService quartzTaskService;
    
    private final ScriptStorage scriptStorage;

    @Override
    @Transactional
    public Long create(TaskCreateReq request) {
        validateCron(request.getCronExpression());
        ScheduledTaskDO task = ScheduledTaskConvert.INSTANCE.toTask(request);
        if (task.getEmailNotificationPolicy() == null) task.setEmailNotificationPolicy("ALL");
        saveScript(task, request.getScript());
        scheduledTaskMapper.insert(task);
        task.setQuartzJobName(TaskQuartzConstants.jobName(task.getId()));
        scheduledTaskMapper.updateById(task);
        if (CommonStatusEnum.isEnabled(task.getEnabled())) quartzTaskService.schedule(task);
        return task.getId();
    }

    @Override
    @Transactional
    public void update(Long taskId, TaskUpdateReq request) {
        validateCron(request.getCronExpression());
        ScheduledTaskDO task = validateTask(taskId);
        quartzTaskService.pause(task);
        ScheduledTaskConvert.INSTANCE.updateTask(request, task);
        if (task.getEmailNotificationPolicy() == null) task.setEmailNotificationPolicy("ALL");
        saveScript(task, request.getScript());
        scheduledTaskMapper.updateById(task);
        if (CommonStatusEnum.isEnabled(task.getEnabled())) quartzTaskService.schedule(task);
    }

    @Override
    public void updateStatus(Long taskId, TaskUpdateStatusReq request) {
        ScheduledTaskDO task = validateTask(taskId);
        task.setEnabled(request.getEnabled());
        scheduledTaskMapper.updateById(task);
        if (CommonStatusEnum.isEnabled(task.getEnabled())) {
            quartzTaskService.schedule(task);
        } else {
            quartzTaskService.pause(task);
        }
    }

    @Override
    public void delete(Long taskId) {
        ScheduledTaskDO task = validateTask(taskId);
        quartzTaskService.delete(task);
        scheduledTaskMapper.deleteById(taskId);
        scriptStorage.delete(task.getScriptPath());
    }

    @Override
    public void trigger(Long taskId) {
        ScheduledTaskDO task = validateTask(taskId);
        if (!CommonStatusEnum.isEnabled(task.getEnabled())) throw new BusinessException(BusinessErrorCodeConstants.TASK_DISABLED);
        quartzTaskService.trigger(task);
    }

    @Override
    public PageResult<ScheduledTaskResp> getPage(TaskPageReq request) {
        PageResult<ScheduledTaskDO> page = scheduledTaskMapper.selectPage(request);
        return ScheduledTaskConvert.INSTANCE.toPage(page);
    }

    @Override
    public PageResult<ExecutionRecordResp> getExecutionPage(Long taskId, TaskPageReq request) {
        validateTask(taskId);
        PageResult<ExecutionRecordDO> page = executionRecordMapper.selectPageByTaskId(taskId, request);
        return ScheduledTaskConvert.INSTANCE.toExecutionPage(page);
    }

    /**
     * 保存新脚本并删除旧脚本。
     */
    private void saveScript(ScheduledTaskDO task, org.springframework.web.multipart.MultipartFile script) {
        if (script == null || script.isEmpty()) return;
        String previous = task.getScriptPath();
        task.setScriptPath(scriptStorage.save(script));
        scriptStorage.delete(previous);
    }

    /**
     * 校验任务是否存在。
     */
    private ScheduledTaskDO validateTask(Long taskId) {
        ScheduledTaskDO task = scheduledTaskMapper.selectById(taskId);
        if (task == null) throw new BusinessException(BusinessErrorCodeConstants.TASK_NOT_FOUND);
        return task;
    }

    /**
     * 校验 Cron 表达式格式。
     */
    private void validateCron(String cronExpression) {
        if (!TaskQuartzConstants.isValidCron(cronExpression)) throw new BusinessException(BusinessErrorCodeConstants.TASK_CRON_INVALID);
    }

}
