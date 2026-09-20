package com.cxcron.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cxcron.common.mapper.BaseMapperPlus;
import com.cxcron.entity.AdminUserDO;

public interface AdminUserMapper extends BaseMapperPlus<AdminUserDO> {

    /**
     * 根据用户名查询管理员。
     *
     * @param username 登录用户名
     * @return 管理员，不存在时返回 {@code null}
     */
    default AdminUserDO selectByUsername(String username) {
        return selectOne(new LambdaQueryWrapper<AdminUserDO>()
                .eq(AdminUserDO::getUsername, username));
    }
}
