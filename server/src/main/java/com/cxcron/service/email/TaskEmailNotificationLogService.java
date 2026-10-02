package com.cxcron.service.email;

import com.cxcron.common.pojo.PageResult;
import com.cxcron.controller.system.dto.EmailNotificationLogPageReq;
import com.cxcron.controller.system.vo.EmailNotificationLogResp;

public interface TaskEmailNotificationLogService {
    /**
     * 分页查询邮件通知日志。
     *
     * @param request 分页及筛选条件
     * @return 邮件通知日志分页结果
     */
    PageResult<EmailNotificationLogResp> getPage(EmailNotificationLogPageReq request);
}
