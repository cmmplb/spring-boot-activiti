package io.github.cmmplb.activiti.factory;


import java.util.HashMap;
import java.util.Map;

/**
 * @author plb
 * @date 2022-04-27 下午 07:10
 * 申请工厂
 */

public class ApplyFactory {

    /**
     * 申请类型与处理类的映射
     */
    private static final Map<Byte, ApplyAbstractHandler<?, ?>> STRATEGY_MAP = new HashMap<>();

    public static ApplyAbstractHandler<?, ?> getInstance(Byte type) {
        return STRATEGY_MAP.get(type);
    }

    public static void register(Byte type, ApplyAbstractHandler<?, ?> abstractHandler) {
        if (null == type || null == abstractHandler) {
            return;
        }
        STRATEGY_MAP.put(type, abstractHandler);
    }
}
