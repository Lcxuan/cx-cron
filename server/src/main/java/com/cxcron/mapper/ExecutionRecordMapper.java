package com.cxcron.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cxcron.common.mapper.BaseMapperPlus;
import com.cxcron.common.pojo.PageParam;
import com.cxcron.common.pojo.PageResult;
import com.cxcron.entity.ExecutionRecordDO;

public interface ExecutionRecordMapper extends BaseMapperPlus<ExecutionRecordDO> {

    default PageResult<ExecutionRecordDO> selectPageByTaskId(Long taskId, PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapper<ExecutionRecordDO>()
                .eq(ExecutionRecordDO::getScheduledTaskId, taskId)
                .orderByDesc(ExecutionRecordDO::getCreateTime));
    }
}
