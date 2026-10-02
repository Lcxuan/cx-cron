package com.cxcron.service.email;

import com.cxcron.controller.system.dto.EmailConfigReq;
import com.cxcron.controller.system.vo.EmailConfigResp;
import com.cxcron.entity.SystemEmailConfigDO;

public interface SystemEmailConfigService {
    /**
     * 新增或更新全局邮件配置。
     *
     * @param request 邮件配置请求，密码为空时保留已保存的密码
     */
    void save(EmailConfigReq request);

    /**
     * 查询全局邮件配置，不返回邮件密码或其密文。
     *
     * @return 邮件配置响应
     */
    EmailConfigResp get();

    /**
     * 查询已启用的全局邮件配置。
     *
     * @return 已启用的邮件配置
     */
    SystemEmailConfigDO requireEnabled();
}
