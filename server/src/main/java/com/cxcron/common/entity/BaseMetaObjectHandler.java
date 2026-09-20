package com.cxcron.common.entity;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据库公共字段自动填充处理器。
 */
@Component
public class BaseMetaObjectHandler implements MetaObjectHandler {

    /**
     * 新增记录时填充创建和更新时间。
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        if (StpUtil.isLogin()) {
            strictInsertFill(metaObject, "createBy", Long.class, StpUtil.getLoginIdAsLong());
            strictInsertFill(metaObject, "updateBy", Long.class, StpUtil.getLoginIdAsLong());
        }
    }

    /**
     * 更新记录时填充更新时间。
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
