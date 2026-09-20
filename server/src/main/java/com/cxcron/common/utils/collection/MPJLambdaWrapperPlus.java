package com.cxcron.common.utils.collection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import java.util.Collection;
import org.springframework.util.StringUtils;

/**
 * MyBatis-Plus-Join Lambda 查询条件构造器扩展。
 *
 * @param <T> 主表实体类型
 */
public class MPJLambdaWrapperPlus<T> extends MPJLambdaWrapper<T> {

    /**
     * like 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param value 值
     * @return 当前查询构造器
     */
    public MPJLambdaWrapperPlus<T> likeIfPresent(SFunction<T, ?> column, String value) {
        if (StringUtils.hasText(value)) {
            super.like(column, value);
        }
        return this;
    }

    /**
     * in 条件查询拼接，集合为空时不进行条件拼接。
     *
     * @param column 字段
     * @param values 值集合
     * @return 当前查询构造器
     */
    public MPJLambdaWrapperPlus<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
        if (CollUtil.isNotEmpty(values)) {
            super.in(column, values);
        }
        return this;
    }

    /**
     * in 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param values 值
     * @return 当前查询构造器
     */
    public MPJLambdaWrapperPlus<T> inIfPresent(SFunction<T, ?> column, Object... values) {
        if (ArrayUtil.isNotEmpty(values)) {
            super.in(column, values);
        }
        return this;
    }

    /**
     * ne 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param value 值
     * @return 当前查询构造器
     */
    public MPJLambdaWrapperPlus<T> neIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null) {
            super.ne(column, value);
        }
        return this;
    }

    /**
     * gt 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param value 值
     * @return 当前查询构造器
     */
    public MPJLambdaWrapperPlus<T> gtIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null) {
            super.gt(column, value);
        }
        return this;
    }

    /**
     * ge 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param value 值
     * @return 当前查询构造器
     */
    public MPJLambdaWrapperPlus<T> geIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null) {
            super.ge(column, value);
        }
        return this;
    }

    /**
     * lt 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param value 值
     * @return 当前查询构造器
     */
    public MPJLambdaWrapperPlus<T> ltIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null) {
            super.lt(column, value);
        }
        return this;
    }

    /**
     * le 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param value 值
     * @return 当前查询构造器
     */
    public MPJLambdaWrapperPlus<T> leIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null) {
            super.le(column, value);
        }
        return this;
    }

    /**
     * eq 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param value 值
     * @return 当前查询构造器
     */
    public MPJLambdaWrapperPlus<T> eqIfPresent(SFunction<T, ?> column, Object value) {
        if (value != null) {
            super.eq(column, value);
        }
        return this;
    }

    /**
     * between 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param start 开始值
     * @param end 结束值
     * @return 当前查询构造器
     */
    public MPJLambdaWrapperPlus<T> betweenIfPresent(SFunction<T, ?> column, Object start, Object end) {
        if (start != null && end != null) {
            super.between(column, start, end);
        } else if (start != null) {
            super.ge(column, start);
        } else if (end != null) {
            super.le(column, end);
        }
        return this;
    }
}
