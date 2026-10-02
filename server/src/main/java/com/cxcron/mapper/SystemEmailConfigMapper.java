package com.cxcron.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cxcron.common.mapper.BaseMapperPlus;
import com.cxcron.entity.SystemEmailConfigDO;

public interface SystemEmailConfigMapper extends BaseMapperPlus<SystemEmailConfigDO> {
    /**
     * 查询一条系统邮件配置记录。
     *
     * @return 邮件配置记录；不存在时返回 null
     */
    default SystemEmailConfigDO selectConfig() {
        return selectOne(new LambdaQueryWrapper<SystemEmailConfigDO>().last("LIMIT 1"));
    }
}
