package io.github.cmmplb.activiti.utils;

import io.github.cmmplb.activiti.convert.Converter;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author penglibo
 * @date 2022-08-04 11:35:14
 * @since jdk 1.8
 * 转换工具类
 */
public class ConverterUtil {

    /**
     * 对象映射
     * @param clz    映射类
     * @param target 实体
     * @param <E>    实体泛型
     * @param <V>    VO泛型
     * @param <C>    映射类泛型
     * @return e
     */
    public static <E, V, C extends Converter<E, V>> V convert(Class<C> clz, E target) {
        // 初始化指定类的映射实例
        final C converter = Mappers.getMapper(clz);
        // 对象映射
        return converter.convert(target);
    }

    /**
     * 集合映射
     * @param clazz 映射类
     * @param list  实体
     * @param <E>   实体泛型
     * @param <V>   VO泛型
     * @param <C>   映射类泛型
     * @return list
     */
    public static <E, V, C extends Converter<E, V>> List<V> convert(Class<C> clazz, List<E> list) {
        // 初始化指定类的映射实例
        final C converter = Mappers.getMapper(clazz);
        // 集合映射
        return converter.convertList(list);
    }
}
