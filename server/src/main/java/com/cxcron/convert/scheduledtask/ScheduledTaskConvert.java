package com.cxcron.convert.scheduledtask;

import com.cxcron.entity.AdminUserDO;
import com.cxcron.entity.ScheduledTaskDO;
import com.cxcron.common.pojo.PageResult;
import com.cxcron.controller.auth.vo.CurrentUserResp;
import com.cxcron.controller.task.dto.TaskCreateReq;
import com.cxcron.controller.task.dto.TaskUpdateReq;
import com.cxcron.controller.task.vo.ExecutionRecordResp;
import com.cxcron.controller.task.vo.ScheduledTaskResp;
import com.cxcron.entity.ExecutionRecordDO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * 管理员认证对象转换器。
 */
@Mapper
public interface ScheduledTaskConvert {

    ScheduledTaskConvert INSTANCE = Mappers.getMapper(ScheduledTaskConvert.class);

    PageResult<ScheduledTaskResp> toPage(PageResult<ScheduledTaskDO> pageResult);

    PageResult<ExecutionRecordResp> toExecutionPage(PageResult<ExecutionRecordDO> pageResult);

    @Mapping(target = "notification", ignore = true)
    ExecutionRecordResp toExecutionRecordResp(ExecutionRecordDO record);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "scriptFilename", ignore = true)
    @Mapping(target = "scriptPath", ignore = true)
    @Mapping(target = "quartzJobName", ignore = true)
    @Mapping(target = "lastScheduleStatus", ignore = true)
    @Mapping(target = "lastScheduleTime", ignore = true)
    ScheduledTaskDO toTask(TaskCreateReq request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "scriptFilename", ignore = true)
    @Mapping(target = "scriptPath", ignore = true)
    @Mapping(target = "quartzJobName", ignore = true)
    @Mapping(target = "lastScheduleStatus", ignore = true)
    @Mapping(target = "lastScheduleTime", ignore = true)
    void updateTask(TaskUpdateReq request, @MappingTarget ScheduledTaskDO task);
}
