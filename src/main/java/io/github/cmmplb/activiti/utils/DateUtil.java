package io.github.cmmplb.activiti.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author penglibo
 * @date 2024-11-01 10:26:43
 * @since jdk 1.8
 */

@Slf4j
public class DateUtil {

    public final static String FORMAT_DATE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 锁对象
     */
    private static final Object LOCK_OBJ = new Object();
    private static final Map<String, ThreadLocal<DateFormat>> SDF_MAP = new HashMap<>();

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     * @param pattern 日期格式
     */
    private static DateFormat getSdf(final String pattern) {
        ThreadLocal<DateFormat> tl = SDF_MAP.get(pattern);
        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (LOCK_OBJ) {
                tl = SDF_MAP.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = ThreadLocal.withInitial(() -> new SimpleDateFormat(pattern, Locale.CHINA));
                    SDF_MAP.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }

    /**
     * 是用ThreadLocal<SimpleDateFormat>来获取SimpleDateFormat,这样每个线程只会有一个SimpleDateFormat
     * @param date    日期
     * @param pattern 日期格式
     * @return 字符串日期
     */
    public static String formatDate(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    public static Date parseToDate(String dateStr, String pattern) {
        try {
            return getSdf(pattern).parse(dateStr);
        } catch (ParseException e) {
            log.error("日期转换失败", e);
        }
        return null;
    }

    /**
     * 获取当前日期
     * @return 当前日期
     */
    public static Date getCurrentDateTime() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 获取当前日期字符串
     * @return 当前日期字符串
     */
    public static String getCurrentDateTimeString() {
        return formatDate(getCurrentDateTime(), FORMAT_DATE_YYYY_MM_DD_HH_MM_SS);
    }
}
