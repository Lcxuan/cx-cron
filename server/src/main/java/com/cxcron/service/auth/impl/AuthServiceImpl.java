package com.cxcron.service.auth.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.cxcron.convert.auth.AuthConvert;
import com.cxcron.entity.AdminUserDO;
import com.cxcron.enums.exception.BusinessErrorCodeConstants;
import com.cxcron.enums.exception.BusinessException;
import com.cxcron.mapper.AdminUserMapper;
import com.cxcron.controller.auth.dto.LoginReq;
import com.cxcron.controller.auth.vo.CurrentUserResp;
import com.cxcron.controller.auth.vo.TokenResp;
import com.cxcron.service.auth.AuthService;
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
    private final TokenService tokenService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResp login(LoginReq request) {
        AdminUserDO user = adminUserMapper.selectByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(BusinessErrorCodeConstants.LOGIN_FAILED);
        }
        user.setLastLoginTime(LocalDateTime.now());
        adminUserMapper.updateById(user);
        return tokenService.createTokens(user.getId());
    }

    @Override
    public CurrentUserResp getCurrentUser() {
        AdminUserDO user = adminUserMapper.selectById(StpUtil.getLoginIdAsLong());
        if (user == null) {
            throw new BusinessException(BusinessErrorCodeConstants.TOKEN_INVALID);
        }
        return AuthConvert.INSTANCE.toCurrentUserResp(user);
    }
}
