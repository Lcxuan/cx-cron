package com.cxcron.service.auth;

import com.cxcron.controller.auth.dto.LoginReq;
import com.cxcron.controller.auth.vo.CurrentUserResp;
import com.cxcron.controller.auth.vo.TokenResp;

public interface AuthService {

    /**
     * 管理员登录。
     *
     * @param request 登录请求
     * @return 认证令牌
     */
    TokenResp login(LoginReq request);

    /**
     * 获取当前管理员信息。
     *
     * @return 当前管理员信息
     */
    CurrentUserResp getCurrentUser();
}
