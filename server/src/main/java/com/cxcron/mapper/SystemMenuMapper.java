package com.cxcron.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cxcron.common.mapper.BaseMapperPlus;
import com.cxcron.entity.SystemMenuDO;

import java.util.List;

public interface SystemMenuMapper extends BaseMapperPlus<SystemMenuDO> {

    default List<SystemMenuDO> selectAllMenus() {
        return selectList(new LambdaQueryWrapper<SystemMenuDO>().orderByAsc(SystemMenuDO::getParentId)
                .orderByAsc(SystemMenuDO::getSort).orderByAsc(SystemMenuDO::getId));
    }
}
