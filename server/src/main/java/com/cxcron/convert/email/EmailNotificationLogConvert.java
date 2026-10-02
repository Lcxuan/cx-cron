package com.cxcron.convert.email;

import com.cxcron.common.pojo.PageResult;
import com.cxcron.controller.system.vo.EmailNotificationLogResp;
import com.cxcron.entity.TaskEmailNotificationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmailNotificationLogConvert {
    EmailNotificationLogConvert INSTANCE = Mappers.getMapper(EmailNotificationLogConvert.class);

    PageResult<EmailNotificationLogResp> toPage(PageResult<TaskEmailNotificationDO> page);

    EmailNotificationLogResp toResp(TaskEmailNotificationDO notification);
}
