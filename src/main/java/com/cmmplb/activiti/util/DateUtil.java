package com.cmmplb.activiti.util;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 日期工具类
 * mysql 日期
 * DATE_FORMAT(date,format)
 * 根据format字符串格式化date值。下列修饰符可以被用在format字符串中：
 * %M 月名字(January……December)
 * %W 星期名字(Sunday……Saturday)
 * %D 有英语前缀的月份的日期(1st, 2nd, 3rd, 等等。）
 * %Y 年, 数字, 4 位
 * %y 年, 数字, 2 位
 * %a 缩写的星期名字(Sun……Sat)
 * %d 月份中的天数, 数字(00……31)
 * %e 月份中的天数, 数字(0……31)
 * %m 月, 数字(01……12)
 * %c 月, 数字(1……12)
 * %b 缩写的月份名字(Jan……Dec)
 * %j 一年中的天数(001……366)
 * %H 小时(00……23)
 * %k 小时(0……23)
 * %h 小时(01……12)
 * %I 小时(01……12)
 * %l 小时(1……12)
 * %i 分钟, 数字(00……59)
 * %r 时间,12 小时(hh:mm:ss [AP]M)
 * %T 时间,24 小时(hh:mm:ss)
 * %S 秒(00……59)
 * %s 秒(00……59)
 * %p AM或PM
 * %w 一个星期中的天数(0=Sunday ……6=Saturday ）
 * %U 星期(0……52), 这里星期天是星期的第一天
 * %u 星期(0……52), 这里星期一是星期的第一天
 */
public class DateUtil {

    final static private Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);
    private static final List<String> FORMAT_LIST = new ArrayList<>(5);
    public static final String FORMAT_DATE_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String DATE_TIME_PATTERN = FORMAT_DATE_YYYY_MM_DD + " " + TIME_PATTERN;
    public static final String DATE_PATTERN_YYYY_MM = "yyyy-MM";
    public final static String FORMAT_DATE_YYYYMMDD = "yyyyMMdd";
    public final static String FORMAT_DATE_PATTERN_1 = "yyyy/MM/dd";
    public final static String FORMAT_DATE_PATTERN_2 = "yyyy/M/dd";
    public final static String FORMAT_DATE_PATTERN_3 = "yyyy/MM/d";
    public final static String FORMAT_DATE_PATTERN_4 = "yyyy/M/d";
    public final static String FORMAT_DATE_YYYY_MM_DD_HHMMSS = "yyyyMMddHHmmss";
    public final static String FORMAT_DATE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_DATE_YYYY_MM_DD_HHMM = "yyyy-MM-dd HHmm";
    public static final String FORMAT_DATE_YYYY_MM_DD_HH_MM_SS_CH = "yyyy年MM月dd日HH时mm分ss秒";
    public static final String FORMAT_DATE_YYYY_MM_DD_EEE_HH_MM_SS_CH = "yyyy年MM月dd日 星期EEE HH时mm分ss秒";
    public static final String FORMAT_DATE_M_D_EEE_HH_MM = "M月d日 EEE HH:mm";

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
            LOGGER.error("日期转换失败", e);
        }
        return null;
    }

    /**
     * 补充时分秒
     * @param date 时期
     * @param time 时分秒:"23:59:59"
     * @return date
     */
    public static Date parse(Date date, String time) {
        return DateUtil.parse(DateUtil.formatDate(date, DateUtil.FORMAT_DATE_YYYY_MM_DD) + " " + time, DateUtil.FORMAT_DATE_YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 根据开始时间和结束时间返回时间段内的时间集合
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 天集合
     */
    public static List<Date> getBetweenDayList(Date startDate, Date endDate) {
        List<Date> result = new ArrayList<>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(startDate);
        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(endDate);
        tempEnd.add(Calendar.DAY_OF_YEAR, 1);
        while (tempStart.before(tempEnd)) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

    /**
     * 根据开始时间和结束时间返回时间段内的时间集合
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 天集合
     */
    public static List<Date> getDateList(Date startDate, Date endDate) {
        List<Date> days = new ArrayList<Date>();
        days.add(startDate);// 把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(startDate);
        while (true) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                days.add(cal.getTime());
            } else {
                break;
            }
        }
        days.add(endDate);// 把结束时间加入集合
        return days;
    }

    /**
     * 获取指定日期区间的周一集合
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 周集合-yyyy-MM-dd
     */
    public static List<String> getWeekList(Date startDate, Date endDate) {
        Assert.notNull(startDate, "startDate not be null");
        Assert.notNull(endDate, "endDate not be null");
        List<String> weekList = new ArrayList<>();
        //转换成joda-time的对象
        DateTime firstDay = new DateTime(startDate).dayOfWeek().withMinimumValue();
        DateTime lastDay = new DateTime(endDate).dayOfWeek().withMaximumValue();
        //计算两日期间的区间天数
        Period p = new Period(firstDay, lastDay, PeriodType.days());
        int days = p.getDays();
        if (days > 0) {
            int weekLength = 7;
            for (int i = 0; i < days; i = i + weekLength) {
                String monDay = firstDay.plusDays(i).toString(FORMAT_DATE_YYYY_MM_DD);
                String sunDay = firstDay.plusDays(i + 6).toString(FORMAT_DATE_YYYY_MM_DD);
                weekList.add(monDay);
            }
            weekList.add(lastDay.plusDays(1).toString(FORMAT_DATE_YYYY_MM_DD));
        }
        return weekList;
    }

    /**
     * 获取指定时间的月份1号集合
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 月份集合-yyyy-MM-dd
     */
    public static List<String> getMonthList(Date startDate, Date endDate) {
        Assert.notNull(startDate, "startDate not be null");
        Assert.notNull(endDate, "endDate not be null");
        List<String> result = new ArrayList<String>();
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.setTime(startDate);
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
        max.setTime(endDate);
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
        while (min.before(max)) {
            result.add(getSdf(FORMAT_DATE_YYYY_MM_DD).format(min.getTime()));
            min.add(Calendar.MONTH, 1);
        }
        return result;
    }

    /**
     * 获取指定时间的年份集合
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 年份集合-yyyy-MM-dd
     */
    public static List<String> getYearList(Date startDate, Date endDate) {
        Assert.notNull(startDate, "startDate not be null");
        Assert.notNull(endDate, "endDate not be null");
        List<String> result = new ArrayList<String>();
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.setTime(startDate);
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
        max.setTime(endDate);
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
        while (min.before(max)) {
            result.add(getSdf(FORMAT_DATE_YYYY_MM_DD).format(min.getTime()));
            min.add(Calendar.YEAR, 1);
        }
        return result;
    }

    /**
     * 是否同一天
     * @param day      日期1
     * @param otherDay 日期2
     * @return boolean
     */
    public static boolean isSameDate(Date day, Date otherDay) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(day);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(otherDay);
        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        return isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 判断两个日期是否是同一天
     * @param day      日期1
     * @param otherDay 日期2
     * @return boolean
     */
    public static boolean isSameDay(Date day, Date otherDay) {
        return (getBetweenDays(day, otherDay) == 0);
    }

    /**
     * 计算两个日期相差的天数.不满24小时不算做一天
     * @param fDate 日期1
     * @param sDate 日期2
     * @return 相差的天数
     */
    public static int getBetweenDays(Date fDate, Date sDate) {
        return (int) ((fDate.getTime() - sDate.getTime()) / 86400000L);
    }

    /**
     * 转换为时间（天,时:分:秒.毫秒）
     * @param timeMillis 时间毫秒值
     * @return 时间-天,时:分:秒.毫秒
     */
    public static String formatDateTime(long timeMillis) {
        long day = timeMillis / (24 * 60 * 60 * 1000);
        long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
        long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

    /**
     * 日期相加指定年
     * @param date     日期
     * @param addYears 要添加的年数
     * @return 相加后的日期
     */
    public static Date addYears(Date date, int addYears) {
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.add(Calendar.YEAR, addYears);
        return calender.getTime();
    }

    /**
     * 加指定月
     * @param date      日期
     * @param addMonths 月数
     * @return 相加后的日期
     */
    public static Date addMonth(Date date, int addMonths) {
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.add(Calendar.MONTH, addMonths);
        return calender.getTime();
    }

    /**
     * 加指定天数
     * @param date    日期
     * @param addDays 天数
     * @return 相加后的日期
     */
    public static Date addDay(Date date, int addDays) {
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.add(Calendar.DAY_OF_YEAR, addDays);
        return calender.getTime();
    }

    /**
     * 减指定天数
     * @param time 日期
     * @param day  天数
     * @return 相减后的日期
     */
    public static Date calculateTime(Date time, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(Calendar.DATE, -day);
        return cal.getTime();
    }

    /**
     * 得到一年的第一天
     * @param year 年
     * @return 一年的第一天
     */
    public static Date getFirstDateOfYear(int year) {
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.YEAR, year);
        calender.set(Calendar.DAY_OF_YEAR, calender.getActualMinimum(Calendar.DAY_OF_YEAR));
        setStartTimeOfDay(calender);
        return calender.getTime();
    }

    /**
     * 获取当年的第一天
     * @return 一年的第一天
     */
    public static Date getCurrYearFirst() {
        Calendar currCal = Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearFirst(currentYear);
    }

    /**
     * 获取当年的最后一天
     * @return 一年的最后一天
     */
    public static Date getCurrYearLast() {
        Calendar currCal = Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearLast(currentYear);
    }

    /**
     * 获取某年第一天日期
     * @param year 年份
     * @return 某年的第一天
     */
    public static Date getYearFirst(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * 获取过去七天的时间
     * @return 七天前的日期
     */
    public static Date getWeek() {
        Calendar c = Calendar.getInstance();
        //过去七天
        c.setTime(new Date());
        c.add(Calendar.DATE, -7);
        return c.getTime();
    }

    /**
     * 获取某年最后一天日期
     * @param year 年份
     * @return Date
     */
    public static Date getYearLast(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    /**
     * 得到一年的最后一天
     * @param year 年
     * @return 一年的最后一天
     */
    public static Date getLastDateOfYear(int year) {
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.YEAR, year);
        calender.set(Calendar.DAY_OF_YEAR, calender.getActualMaximum(Calendar.DAY_OF_YEAR));
        setEndTimeOfDay(calender);
        return calender.getTime();
    }

    /**
     * 判断当前日期是否是所在月份的最后一天
     * @param date 日期
     * @return 是最后一天为 true
     */
    public static boolean isLastDayOfMonth(Date date) {
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        int day = calender.get(Calendar.DAY_OF_MONTH);
        int lastDay = calender.getActualMaximum(Calendar.DAY_OF_MONTH);
        return day == lastDay;
    }

    /**
     * 得到指定月的最后一天
     * @param year  年
     * @param month 月
     * @return 最后一天
     */
    public static Date getLastDayOfMonth(int year, int month) {
        Calendar calender = Calendar.getInstance();
        month = month - 1;// 月份需要减去一天
        calender.set(year, month, 1);
        calender.set(Calendar.DAY_OF_MONTH, calender.getActualMaximum(Calendar.DAY_OF_MONTH));
        setEndTimeOfDay(calender);
        return calender.getTime();
    }

    /**
     * 得到日期所在月的最后一天
     * @param date 日期
     * @return 所在月的最后一天
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.set(Calendar.DAY_OF_MONTH, calender.getActualMaximum(Calendar.DAY_OF_MONTH));
        setEndTimeOfDay(calender);
        return calender.getTime();
    }

    /**
     * 设置到当前月的最后时刻
     * @param calender 日期
     */
    private static void setEndTimeOfDay(Calendar calender) {
        calender.set(Calendar.HOUR_OF_DAY, calender.getActualMaximum(Calendar.HOUR_OF_DAY));
        calender.set(Calendar.MINUTE, calender.getActualMaximum(Calendar.MINUTE));
        calender.set(Calendar.SECOND, calender.getActualMaximum(Calendar.SECOND));
        calender.set(Calendar.MILLISECOND, calender.getActualMaximum(Calendar.MILLISECOND));
    }

    /**
     * 设置到月份开始的时刻
     * @param calender 日期
     */
    private static void setStartTimeOfDay(Calendar calender) {
        calender.set(Calendar.HOUR_OF_DAY, calender.getActualMinimum(Calendar.HOUR_OF_DAY));
        calender.set(Calendar.MINUTE, calender.getActualMinimum(Calendar.MINUTE));
        calender.set(Calendar.SECOND, calender.getActualMinimum(Calendar.SECOND));
        calender.set(Calendar.MILLISECOND, calender.getActualMinimum(Calendar.MILLISECOND));
    }

    /**
     * 得到指定月的第一天
     * @param year  年
     * @param month 月
     * @return 第一天
     */
    public static Date getFirstDayOfMonth(int year, int month) {
        Calendar calender = Calendar.getInstance();
        calender.set(year, month - 1, 1);
        calender.set(Calendar.DAY_OF_MONTH, calender.getActualMinimum(Calendar.DAY_OF_MONTH));
        setStartTimeOfDay(calender);
        return calender.getTime();
    }

    /**
     * 得到指定日期所在月的第一天
     * @param date 日期
     * @return 第一天
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.set(Calendar.DAY_OF_MONTH, calender.getActualMinimum(Calendar.DAY_OF_MONTH));
        setStartTimeOfDay(calender);
        return calender.getTime();
    }

    /**
     * 某一个月第一天和最后一天
     * @param date 某月
     * @return 第一天和最后一天
     */
    public static Map<String, String> getFirstDayLastDayMonth(Date date) {
        DateFormat sdf = getSdf(FORMAT_DATE_YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        Date theDate = calendar.getTime(); // 上个月第一天
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1); // 上个月最后一天
        calendar.add(Calendar.MONTH, 1); // 加一个月
        calendar.set(Calendar.DATE, 1); // 设置为该月第一天
        calendar.add(Calendar.DATE, -1); // 再减一天即为上个月最后一天
        Map<String, String> map = new HashMap<String, String>();
        map.put("first", sdf.format(gcLast.getTime()) + " 00:00:00");
        map.put("last", sdf.format(calendar.getTime()) + " 23:59:59");
        return map;
    }

    /**
     * 获取本月第一天
     * @return 本月第一天日期
     */
    public static Date getCurrentMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * 获取本月最后一天
     * @return 本月最后一天日期
     */
    public static Date getCurrentMonthLastDay() {
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(new Date());
        calendar2.set(Calendar.DAY_OF_MONTH, calendar2.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar2.getTime();
    }

    /**
     * 得到年月
     * @return 格式：2008-11
     */
    public static String getYearMonth(Date date) {
        Calendar today = Calendar.getInstance();
        today.setTime(date);
        return (today.get(Calendar.YEAR)) + "-" + ((today.get(Calendar.MONTH) + 1) >= 10 ? (today.get(Calendar.MONTH) + 1) : ("0" + (today.get(Calendar.MONTH) + 1)));
    }

    /**
     * 获取当前年份
     * @return 年
     */
    public static String getCurrentYear() {
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        return String.valueOf(today.get(Calendar.YEAR));
    }

    /**
     * 获取指定日期年份
     * @param date 指定日期
     * @return 当前年
     */
    public static String getYear(Date date) {
        Calendar today = Calendar.getInstance();
        today.setTime(date);
        return String.valueOf(today.get(Calendar.YEAR));
    }

    /**
     * 获取当前月份
     * @return 当前月份
     */
    public static String getCurrentMonth() {
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        return String.valueOf(today.get(Calendar.MONTH) + 1);
    }

    /**
     * 获取当月的上一个月
     * @return 上个月
     */
    public static String getLastMonth() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // 设置为当前时间
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1); // 设置为上一个月
        date = calendar.getTime();
        return getSdf(DATE_PATTERN_YYYY_MM).format(date);
    }

    /**
     * 计算两个日期之间相差的月份数
     * <br> 日期顺序不分先后不会返回负数
     * <br> 不足一个月不算做一个月
     * @param date1 日期1
     * @param date2 日期2
     * @return 月数
     */
    public static int getBetweenMonths(Date date1, Date date2) {
        int iMonth = 0;
        int flag = 0;
        Calendar objCalendarDate1 = Calendar.getInstance();
        objCalendarDate1.setTime(date1);
        Calendar objCalendarDate2 = Calendar.getInstance();
        objCalendarDate2.setTime(date2);
        if (objCalendarDate2.equals(objCalendarDate1)) {
            return 0;
        }
        if (objCalendarDate1.after(objCalendarDate2)) {
            Calendar temp = objCalendarDate1;
            objCalendarDate1 = objCalendarDate2;
            objCalendarDate2 = temp;
        }
        if (objCalendarDate2.get(Calendar.DAY_OF_MONTH) < objCalendarDate1.get(Calendar.DAY_OF_MONTH)) {
            flag = 1;
        }
        if (objCalendarDate2.get(Calendar.YEAR) > objCalendarDate1.get(Calendar.YEAR)) {
            iMonth = ((objCalendarDate2.get(Calendar.YEAR) - objCalendarDate1.get(Calendar.YEAR))
                    * 12 + objCalendarDate2.get(Calendar.MONTH) - flag)
                    - objCalendarDate1.get(Calendar.MONTH);
        } else {
            iMonth = objCalendarDate2.get(Calendar.MONTH)
                    - objCalendarDate1.get(Calendar.MONTH) - flag;
        }
        return iMonth;
    }

    /**
     * 计算两个日期之间相差的月份数
     * @param date1 日期1
     * @param date2 日期2
     * @return 相差的月份数
     */
    public static int countMonths(Date date1, Date date2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        int year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        //开始日期若小月结束日期
        if (year < 0) {
            year = -year;
            return year * 12 + c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
        }
        return year * 12 + c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
    }

    /**
     * 计算两个日期之间相差的年份数
     * <br> 日期顺序不分先后不会返回负数
     * <br> 不足一个年不算做一个年
     * @param date1 日期1
     * @param date2 日期2
     * @return 年数
     */
    public static int getBetweenYears(Date date1, Date date2) {
        return getBetweenMonths(date1, date2) / 12;
    }

    public static boolean before(Date date1, Date date2) {
        if (date1 == null
                || date2 == null) {
            return false;
        }
        Calendar c1 = new GregorianCalendar();
        c1.setTime(date1);
        Calendar c2 = new GregorianCalendar();
        c2.setTime(date2);
        return c1.before(c2);
    }

    /**
     * 当前时间是否失效
     * @param expiresDate 判断的日期
     * @return boolean
     */
    public static boolean isExpires(Date expiresDate) {
        return after(new Date(), expiresDate);
    }

    public static boolean after(Date date1, Date date2) {
        if (date1 == null
                || date2 == null) {
            return false;
        }
        Calendar c1 = new GregorianCalendar();
        c1.setTime(date1);
        Calendar c2 = new GregorianCalendar();
        c2.setTime(date2);
        return c1.after(c2);
    }


    /**
     * 根据日期获取 星期 （2019-05-06 ——> 星期一）
     * @param date 日期
     * @return 星期几
     */
    public static String dateToWeek(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //一周的第几天
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 判断两个日期是否相差一天
     * @return boolean
     */
    public static boolean isYesterday(Date oldDate, Date newDate) {
        Calendar oldCal = Calendar.getInstance();
        Calendar newCal = Calendar.getInstance();

        oldCal.setTime(oldDate);
        newCal.setTime(newDate);
        return Math.abs(newCal.get(Calendar.DAY_OF_YEAR) - oldCal.get(Calendar.DAY_OF_YEAR)) == 1;
    }

    /**
     * 校验日期格式是否合法
     * @param date   待校验的日期
     * @param format 日期格式
     * @return boolean
     */
    public static boolean isValidDate(String date, String format) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        DateFormat sdf = getSdf(format);
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            sdf.setLenient(false);
            sdf.parse(date);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 转换日期
     * @param stringValue 字符串日期
     * @return 返回日期
     */
    public static Date tryParse(String stringValue) {
        Date date = parse(stringValue, FORMAT_DATE_YYYY_MM_DD);
        if (date != null) {
            return date;
        }
        date = parse(stringValue, FORMAT_DATE_YYYYMMDD);
        if (date != null) {
            return date;
        }
        date = parse(stringValue, FORMAT_DATE_YYYY_MM_DD_HHMMSS);
        if (date != null) {
            return date;
        }
        date = parse(stringValue, FORMAT_DATE_YYYY_MM_DD_HH_MM_SS);
        if (date != null) {
            return date;
        }
        date = parse(stringValue, FORMAT_DATE_YYYY_MM_DD_HHMM);
        if (date != null) {
            return date;
        }
        date = parse(stringValue, FORMAT_DATE_PATTERN_1);
        if (date != null) {
            return date;
        }
        date = parse(stringValue, FORMAT_DATE_PATTERN_2);
        if (date != null) {
            return date;
        }
        date = parse(stringValue, FORMAT_DATE_PATTERN_3);
        if (date != null) {
            return date;
        }
        date = parse(stringValue, FORMAT_DATE_PATTERN_4);
        if (date != null) {
            return date;
        }
        return date;
    }

    public static Date parse(String stringValue, String formatPattern) {
        return parseToDate(stringValue, formatPattern);
    }

    /**
     * 获取当前日期
     * @return 当前日期
     */
    public static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }

    /**
     * date转LocalDateTime
     * @param date 日期
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    /**
     * 字符串日期转date转LocalDateTime
     * @param date    字符串日期
     * @param pattern 格式
     * @return LocalDateTime
     */
    public static LocalDateTime parseStringToLocalDateTime(String date, String pattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(date, df);
    }

    /**
     * 字符串日期转date转LocalDate
     * @param date    字符串日期
     * @param pattern 格式
     * @return LocalDate
     */
    public static LocalDate parseStringToLocalDate(String date, String pattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(date, df);
    }

    /**
     * localDate转Date
     * @param localDate localDate
     * @return Date
     */
    public static Date LocalDateToDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * localDatetime to string
     * @param localDateTime localDateTime
     * @return String
     */
    public static String parseLocalDateTimeToString(LocalDateTime localDateTime) {
        return parseLocalDateTimeToString(localDateTime, FORMAT_DATE_YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * localDatetime to string
     * @param localDateTime localDateTime
     * @param pattern       pattern
     * @return String
     */
    public static String parseLocalDateTimeToString(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return df.format(localDateTime);
    }

    /**
     * 比较日期大小
     * @param date1 时间1
     * @param date2 时间2
     * @return boolean
     */
    public static boolean compare(Date date1, Date date2) {
        // 方法一：
        // Date类的一个方法，如果s早于或等于e返回true，否则返回false
        return date1.equals(date2) || date1.before(date2);
        // 方法二：
        // 如果你不喜欢用上面这个太流氓的方法，也可以根据将Date转换成毫秒
        // return date1.getTime() - date2.getTime() < 0;
    }

    /**
     * 判断当前是否是否在 指定时间范围内
     * @param nowTime   当前时间
     * @param startTime 开始时间  yyyy-MM-dd HH:mm
     * @param endTime   结束时间  yyyy-MM-dd HH:mm
     * @return boolean
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        return date.after(begin) && date.before(end);
    }

    /**
     * 获取当前时间之前的小时集合
     * @return [14, 15, 16]
     */
    public static List<Integer> getLast24Hour() {
        List<Integer> timeIntList = new ArrayList<>(24);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.add(Calendar.DATE, -1);
        int startIndex;
        if (0 == hour) {
            startIndex = 0;
            for (int j = startIndex; j < 24; j++) {
                timeIntList.add(j);
            }
        } else {
            startIndex = hour + 1;
            for (int k = startIndex; k < 24; k++) {
                timeIntList.add(k);
            }
            for (int l = 0; l <= hour; l++) {
                timeIntList.add(l);
            }
        }
        return timeIntList;
    }

    /**
     * 获取当前时间之前的日期集合
     * @return [8-17,8-18]
     */
    public static List<String> getLastMonthDate() {
        List<String> dayStrList = new ArrayList<>(31);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        int lastMonth = calendar.get(Calendar.MONTH) + 1;
        int lastEnd = calendar.get(Calendar.DAY_OF_MONTH);
        int startIndex;
        if (currentDate >= 30) {
            startIndex = 1;
            for (int j = startIndex; j <= currentDate; j++) {
                dayStrList.add(currentMonth + "-" + j);
            }
        } else {
            startIndex = currentDate + 1;
            for (int k = startIndex; k <= lastEnd; k++) {
                dayStrList.add(lastMonth + "-" + k);
            }
            for (int l = 1; l <= currentDate; l++) {
                dayStrList.add(currentMonth + "-" + l);
            }
        }
        return dayStrList;
    }
}
