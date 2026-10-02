package com.cxcron.service.email.impl;

import com.cxcron.common.pojo.PageResult;
import com.cxcron.controller.system.dto.EmailNotificationLogPageReq;
import com.cxcron.controller.system.vo.EmailNotificationLogResp;
import com.cxcron.convert.email.EmailNotificationLogConvert;
import com.cxcron.mapper.TaskEmailNotificationMapper;
import com.cxcron.service.email.TaskEmailNotificationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskEmailNotificationLogServiceImpl implements TaskEmailNotificationLogService {
    
    private final TaskEmailNotificationMapper mapper;

    @Override
    public PageResult<EmailNotificationLogResp> getPage(EmailNotificationLogPageReq request) {
        return EmailNotificationLogConvert.INSTANCE.toPage(mapper.selectLogPage(request));
    }
}
