package com.cxcron.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cxcron.common.mapper.BaseMapperPlus;
import com.cxcron.common.pojo.PageResult;
import com.cxcron.controller.system.dto.EmailNotificationLogPageReq;
import com.cxcron.entity.TaskEmailNotificationDO;

import java.time.LocalDateTime;

import java.util.Collection;
import java.util.List;

public interface TaskEmailNotificationMapper extends BaseMapperPlus<TaskEmailNotificationDO> {
    /**
     * 根据筛选条件分页查询邮件通知日志。
     *
     * @param request 分页参数及状态、任务名称、收件人和创建时间范围筛选条件
     * @return 邮件通知日志分页结果
     */
    default PageResult<TaskEmailNotificationDO> selectLogPage(EmailNotificationLogPageReq request) {
        LambdaQueryWrapper<TaskEmailNotificationDO> wrapper = new LambdaQueryWrapper<TaskEmailNotificationDO>()
                .eq(StringUtils.isNotBlank(request.getStatus()), TaskEmailNotificationDO::getStatus, request.getStatus())
                .like(StringUtils.isNotBlank(request.getTaskName()), TaskEmailNotificationDO::getTaskName, request.getTaskName())
                .like(StringUtils.isNotBlank(request.getRecipient()), TaskEmailNotificationDO::getRecipient, request.getRecipient())
                .ge(request.getCreateTimeStart() != null, TaskEmailNotificationDO::getCreateTime, request.getCreateTimeStart())
                .le(request.getCreateTimeEnd() != null, TaskEmailNotificationDO::getCreateTime, request.getCreateTimeEnd())
                .orderByDesc(TaskEmailNotificationDO::getCreateTime);
        return selectPage(request, wrapper);
    }

    /**
     * 批量查询指定执行记录对应的邮件通知记录。
     *
     * @param ids 执行记录 ID 集合
     * @return 与指定执行记录关联的邮件通知记录列表
     */
    default List<TaskEmailNotificationDO> selectByExecutionRecordIds(Collection<Long> ids) {
        return selectList(new LambdaQueryWrapper<TaskEmailNotificationDO>().in(TaskEmailNotificationDO::getExecutionRecordId, ids));
    }
}
