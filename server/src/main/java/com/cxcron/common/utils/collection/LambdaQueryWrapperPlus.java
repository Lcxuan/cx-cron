package com.cxcron.common.utils.collection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import java.util.Collection;
import org.springframework.util.StringUtils;

public class LambdaQueryWrapperPlus<T> extends LambdaQueryWrapper<T> {

    /**
     * like 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param val 值
     * @return LambdaQueryWrapperPlus
     */
    public LambdaQueryWrapperPlus<T> likeIfPresent(SFunction<T, ?> column, String val) {
        return StringUtils.hasText(val) ? like(column, val) : this;
    }

    /**
     * in 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param values 值
     * @return LambdaQueryWrapperPlus
     */
    public LambdaQueryWrapperPlus<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
        return CollUtil.isNotEmpty(values) ? in(column, values) : this;
    }

    /**
     * in 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param values 值
     * @return LambdaQueryWrapperPlus
     */
    public LambdaQueryWrapperPlus<T> inIfPresent(SFunction<T, ?> column, Object... values) {
        return ArrayUtil.isNotEmpty(values) ? in(column, values) : this;
    }

    /**
     * eq 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param val 值
     * @return LambdaQueryWrapperPlus
     */
    public LambdaQueryWrapperPlus<T> eqIfPresent(SFunction<T, ?> column, Object val) {
        return ObjectUtil.isNotEmpty(val) ? eq(column, val) : this;
    }

    /**
     * ne 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param val 值
     * @return LambdaQueryWrapperPlus
     */
    public LambdaQueryWrapperPlus<T> neIfPresent(SFunction<T, ?> column, Object val) {
        return ObjectUtil.isNotEmpty(val) ? ne(column, val) : this;
    }

    /**
     * 大于条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param val 值
     * @return LambdaQueryWrapperPlus
     */
    public LambdaQueryWrapperPlus<T> gtIfPresent(SFunction<T, ?> column, Object val) {
        return val != null ? gt(column, val) : this;
    }

    /**
     * 大于等于条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param val 值
     * @return LambdaQueryWrapperPlus
     */
    public LambdaQueryWrapperPlus<T> geIfPresent(SFunction<T, ?> column, Object val) {
        return val != null ? ge(column, val) : this;
    }

    /**
     * 小于条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param val 值
     * @return LambdaQueryWrapperPlus
     */
    public LambdaQueryWrapperPlus<T> ltIfPresent(SFunction<T, ?> column, Object val) {
        return val != null ? lt(column, val) : this;
    }

    /**
     * 小于等于条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param val 值
     * @return LambdaQueryWrapperPlus
     */
    public LambdaQueryWrapperPlus<T> leIfPresent(SFunction<T, ?> column, Object val) {
        return val != null ? le(column, val) : this;
    }

    /**
     * between 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param val1 值
     * @param val2 值
     * @return LambdaQueryWrapperPlus
     */
    public LambdaQueryWrapperPlus<T> betweenIfPresent(SFunction<T, ?> column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return between(column, val1, val2);
        }
        if (val1 != null) {
            return ge(column, val1);
        }
        return val2 != null ? le(column, val2) : this;
    }

    /**
     * between 条件查询拼接，值为空时不进行条件拼接。
     *
     * @param column 字段
     * @param values 值
     * @return LambdaQueryWrapperPlus
     */
    public LambdaQueryWrapperPlus<T> betweenIfPresent(SFunction<T, ?> column, Object[] values) {
        return betweenIfPresent(column, ArrayUtil.get(values, 0), ArrayUtil.get(values, 1));
    }

    @Override
    public LambdaQueryWrapperPlus<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        super.eq(condition, column, val);
        return this;
    }

    @Override
    public LambdaQueryWrapperPlus<T> eq(SFunction<T, ?> column, Object val) {
        super.eq(column, val);
        return this;
    }

    @Override
    public LambdaQueryWrapperPlus<T> orderByDesc(SFunction<T, ?> column) {
        super.orderByDesc(true, column);
        return this;
    }

    @Override
    public LambdaQueryWrapperPlus<T> last(String lastSql) {
        super.last(lastSql);
        return this;
    }

    @Override
    public LambdaQueryWrapperPlus<T> in(SFunction<T, ?> column, Collection<?> coll) {
        super.in(column, coll);
        return this;
    }

    @Override
    public LambdaQueryWrapperPlus<T> in(SFunction<T, ?> column, Object... values) {
        super.in(column, values);
        return this;
    }

    @Override
    public LambdaQueryWrapperPlus<T> like(SFunction<T, ?> column, Object val) {
        super.like(column, val);
        return this;
    }

    @Override
    public LambdaQueryWrapperPlus<T> ne(SFunction<T, ?> column, Object val) {
        super.ne(column, val);
        return this;
    }

    @Override
    public LambdaQueryWrapperPlus<T> gt(SFunction<T, ?> column, Object val) {
        super.gt(column, val);
        return this;
    }

    @Override
    public LambdaQueryWrapperPlus<T> ge(SFunction<T, ?> column, Object val) {
        super.ge(column, val);
        return this;
    }

    @Override
    public LambdaQueryWrapperPlus<T> lt(SFunction<T, ?> column, Object val) {
        super.lt(column, val);
        return this;
    }

    @Override
    public LambdaQueryWrapperPlus<T> le(SFunction<T, ?> column, Object val) {
        super.le(column, val);
        return this;
    }

    @Override
    public LambdaQueryWrapperPlus<T> between(SFunction<T, ?> column, Object val1, Object val2) {
        super.between(column, val1, val2);
        return this;
    }
}
