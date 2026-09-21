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

    ScheduledTaskDO toTask(TaskCreateReq request);

    void updateTask(TaskUpdateReq request, @MappingTarget ScheduledTaskDO task);
}
