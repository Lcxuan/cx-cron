package com.cxcron.common.utils;

import cn.hutool.core.bean.BeanUtil;
import com.cxcron.common.utils.collection.CollectionUtil;
import java.util.List;
import java.util.function.Consumer;

/**
 * Bean 转换工具。
 */
public final class BeanUtils {

    private BeanUtils() {
    }

    public static <T> T toBean(Object source, Class<T> targetClass) {
        return BeanUtil.toBean(source, targetClass);
    }

    public static <T> T toBean(Object source, Class<T> targetClass, Consumer<T> peek) {
        T target = toBean(source, targetClass);
        if (target != null) {
            peek.accept(target);
        }
        return target;
    }

    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        return CollectionUtil.convertList(source, item -> toBean(item, targetType));
    }

    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType, Consumer<T> peek) {
        List<T> list = toBean(source, targetType);
        list.forEach(peek);
        return list;
    }
}
