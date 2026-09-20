package com.cxcron.service.token.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.cxcron.controller.auth.vo.TokenResp;
import com.cxcron.entity.AccessTokenDO;
import com.cxcron.entity.RefreshTokenDO;
import com.cxcron.enums.UserTypeEnum;
import com.cxcron.enums.exception.BusinessErrorCodeConstants;
import com.cxcron.enums.exception.BusinessException;
import com.cxcron.mapper.AccessTokenMapper;
import com.cxcron.mapper.RefreshTokenMapper;
import com.cxcron.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RefreshTokenMapper refreshTokenMapper;
    private final AccessTokenMapper accessTokenMapper;

    @Value("${auth.token.access-timeout}")
    private long accessTimeout;

    @Value("${auth.token.refresh-timeout}")
    private long refreshTimeout;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResp createTokens(Long userId) {
        StpUtil.login(userId, accessTimeout);
        String accessToken = StpUtil.getTokenValue();
        LocalDateTime now = LocalDateTime.now();

        RefreshTokenDO refreshToken = new RefreshTokenDO();
        refreshToken.setUserId(userId);
        refreshToken.setUserType(UserTypeEnum.ADMIN.getValue());
        refreshToken.setRefreshToken(UUID.randomUUID().toString().replace("-", ""));
        refreshToken.setExpiresTime(now.plusSeconds(refreshTimeout));
        refreshTokenMapper.insert(refreshToken);

        AccessTokenDO accessTokenDO = new AccessTokenDO();
        accessTokenDO.setUserId(userId);
        accessTokenDO.setUserType(UserTypeEnum.ADMIN.getValue());
        accessTokenDO.setRefreshTokenId(refreshToken.getId());
        accessTokenDO.setAccessToken(accessToken);
        accessTokenDO.setExpiresTime(now.plusSeconds(accessTimeout));
        accessTokenMapper.insert(accessTokenDO);

        return new TokenResp(accessToken, refreshToken.getRefreshToken(), accessTimeout, refreshTimeout);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResp refreshTokens(String refreshToken) {
        RefreshTokenDO token = refreshTokenMapper.selectByRefreshToken(refreshToken);
        if (token == null || token.getExpiresTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(BusinessErrorCodeConstants.TOKEN_INVALID);
        }
        refreshTokenMapper.deleteById(token.getId());
        accessTokenMapper.deleteByRefreshTokenId(token.getId());
        return createTokens(token.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout() {
        accessTokenMapper.deleteByAccessToken(StpUtil.getTokenValue());
        StpUtil.logout();
    }
}
