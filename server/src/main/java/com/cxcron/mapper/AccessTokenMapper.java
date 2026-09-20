package com.cxcron.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cxcron.common.mapper.BaseMapperPlus;
import com.cxcron.entity.AccessTokenDO;

/**
 * 访问令牌数据访问接口。
 */
public interface AccessTokenMapper extends BaseMapperPlus<AccessTokenDO> {

    /**
     * 根据刷新令牌记录 ID 删除关联的访问令牌。
     *
     * @param refreshTokenId 刷新令牌记录 ID
     */
    default void deleteByRefreshTokenId(Long refreshTokenId) {
        delete(new LambdaQueryWrapper<AccessTokenDO>()
                .eq(AccessTokenDO::getRefreshTokenId, refreshTokenId));
    }

    /**
     * 根据访问令牌删除令牌记录。
     *
     * @param accessToken 访问令牌
     */
    default void deleteByAccessToken(String accessToken) {
        delete(new LambdaQueryWrapper<AccessTokenDO>()
                .eq(AccessTokenDO::getAccessToken, accessToken));
    }
}
