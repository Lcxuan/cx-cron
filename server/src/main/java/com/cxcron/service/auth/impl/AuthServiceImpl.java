package com.cxcron.service.auth.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.cxcron.common.enums.CommonStatusEnum;
import com.cxcron.controller.auth.dto.LoginReq;
import com.cxcron.controller.auth.dto.UpdatePasswordReq;
import com.cxcron.controller.auth.vo.CurrentUserResp;
import com.cxcron.controller.auth.vo.TokenResp;
import com.cxcron.convert.auth.AuthConvert;
import com.cxcron.entity.AdminUserDO;
import com.cxcron.enums.exception.BusinessErrorCodeConstants;
import com.cxcron.enums.exception.BusinessException;
import com.cxcron.enums.exception.GlobalErrorCodeConstants;
import com.cxcron.mapper.AdminUserMapper;
import com.cxcron.redis.LoginFailureRedis;
import com.cxcron.service.auth.AuthService;
import com.cxcron.service.auth.RsaCryptoService;
import com.cxcron.service.menu.SystemMenuService;
import com.cxcron.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminUserMapper adminUserMapper;

    private final PasswordEncoder passwordEncoder;
    
    private final RsaCryptoService rsaCryptoService;
    
    private final TokenService tokenService;
    
    private final SystemMenuService systemMenuService;
    
    private final LoginFailureRedis loginFailureRedis;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResp login(LoginReq request, String clientIp) {
        String username = request.getUsername().trim();
        // 失败次数过多时直接拒绝，避免继续执行 RSA 解密和密码校验
        if (loginFailureRedis.isLocked(username, clientIp)) {
            throw new BusinessException(BusinessErrorCodeConstants.LOGIN_LOCKED);
        }
        // 解密前端 RSA 加密后的密码，并查询账号
        String password = rsaCryptoService.decrypt(request.getPassword());
        AdminUserDO user = adminUserMapper.selectByUsername(username);
        // 账号不存在或密码不匹配时累计失败次数，统一返回模糊的登录失败提示
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            loginFailureRedis.recordFailure(username, clientIp);
            throw new BusinessException(BusinessErrorCodeConstants.LOGIN_FAILED);
        }
        // 密码校验通过后再检查账号状态，避免向未通过密码校验的请求暴露账号是否被禁用
        if (!CommonStatusEnum.isEnabled(user.getStatus())) {
            throw new BusinessException(BusinessErrorCodeConstants.ACCOUNT_DISABLED);
        }
        // 登录成功后清空该用户名和 IP 的失败计数
        loginFailureRedis.clear(username, clientIp);
        // 更新最后登录时间并签发访问令牌、刷新令牌
        user.setLastLoginTime(LocalDateTime.now());
        adminUserMapper.updateById(user);
        return tokenService.createTokens(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UpdatePasswordReq request) {
        AdminUserDO user = adminUserMapper.selectById(StpUtil.getLoginIdAsLong());
        if (user == null) {
            throw new BusinessException(GlobalErrorCodeConstants.UNAUTHORIZED);
        }
        if (!passwordEncoder.matches(rsaCryptoService.decrypt(request.getOldPassword()), user.getPassword())) {
            throw new BusinessException(BusinessErrorCodeConstants.PASSWORD_INCORRECT);
        }
        user.setPassword(passwordEncoder.encode(rsaCryptoService.decrypt(request.getNewPassword())));
        adminUserMapper.updateById(user);
        tokenService.logout();
    }

    @Override
    public CurrentUserResp getCurrentUser() {
        AdminUserDO user = adminUserMapper.selectById(StpUtil.getLoginIdAsLong());
        if (user == null) {
            throw new BusinessException(GlobalErrorCodeConstants.UNAUTHORIZED);
        }
        CurrentUserResp response = AuthConvert.INSTANCE.toCurrentUserResp(user);
        response.setMenus(systemMenuService.getEnabledMenuRoutes());
        return response;
    }
}
