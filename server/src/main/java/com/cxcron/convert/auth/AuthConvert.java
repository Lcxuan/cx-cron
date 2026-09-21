package com.cxcron.convert.auth;

import com.cxcron.entity.AdminUserDO;
import com.cxcron.controller.auth.vo.CurrentUserResp;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 管理员认证对象转换器。
 */
@Mapper
public interface AuthConvert {

    AuthConvert INSTANCE = Mappers.getMapper(AuthConvert.class);

    /**
     * 管理员实体转换为当前用户响应。
     *
     * @param user 管理员实体
     * @return 当前用户响应
     */
    CurrentUserResp toCurrentUserResp(AdminUserDO user);
}
