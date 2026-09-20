package com.cxcron.service.token;

import com.cxcron.controller.auth.vo.TokenResp;

public interface TokenService {

    /**
     * 为管理员创建认证令牌。
     *
     * @param userId 管理员 ID
     * @return 认证令牌
     */
    TokenResp createTokens(Long userId);

    /**
     * 使用刷新令牌换取新的认证令牌。
     *
     * @param refreshToken 刷新令牌
     * @return 新认证令牌
     */
    TokenResp refreshTokens(String refreshToken);

    /**
     * 注销当前访问令牌。
     */
    void logout();
}
