package com.cxcron.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cxcron.common.mapper.BaseMapperPlus;
import com.cxcron.entity.RefreshTokenDO;

/**
 * 刷新令牌数据访问接口。
 */
public interface RefreshTokenMapper extends BaseMapperPlus<RefreshTokenDO> {

    /**
     * 根据刷新令牌查询令牌记录。
     *
     * @param refreshToken 刷新令牌
     * @return 刷新令牌记录，不存在时返回 {@code null}
     */
    default RefreshTokenDO selectByRefreshToken(String refreshToken) {
        return selectOne(new LambdaQueryWrapper<RefreshTokenDO>()
                .eq(RefreshTokenDO::getRefreshToken, refreshToken));
    }
}
