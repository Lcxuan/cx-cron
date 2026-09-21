package com.cxcron.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cxcron.common.mapper.BaseMapperPlus;
import com.cxcron.common.pojo.PageParam;
import com.cxcron.common.pojo.PageResult;
import com.cxcron.entity.ScheduledTaskDO;

public interface ScheduledTaskMapper extends BaseMapperPlus<ScheduledTaskDO> {

    default PageResult<ScheduledTaskDO> selectPage(PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapper<ScheduledTaskDO>()
                .orderByDesc(ScheduledTaskDO::getCreateTime));
    }
}
