package com.cxcron.common.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cxcron.common.pojo.PageParam;
import com.cxcron.common.pojo.PageResult;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.interfaces.MPJBaseJoin;

/**
 * 数据库访问基础 Mapper，提供 MyBatis-Plus 与 MyBatis-Plus-Join 查询能力。
 *
 * @param <T> 实体类型
 */
public interface BaseMapperPlus<T> extends MPJBaseMapper<T> {

    /**
     * 根据查询条件分页查询。
     *
     * @param pageParam 分页参数
     * @param queryWrapper 查询条件
     * @return 分页结果
     */
    default PageResult<T> selectPage(PageParam pageParam, Wrapper<T> queryWrapper) {
        Page<T> page = selectPage(new Page<>(pageParam.getPageNo(), pageParam.getPageSize()), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 根据关联查询条件分页查询。
     *
     * @param pageParam 分页参数
     * @param joinWrapper 关联查询条件
     * @return 分页结果
     */
    default PageResult<T> selectJoinPage(PageParam pageParam, MPJBaseJoin<T> joinWrapper) {
        Page<T> page = selectJoinPage(new Page<>(pageParam.getPageNo(), pageParam.getPageSize()), joinWrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }
}
