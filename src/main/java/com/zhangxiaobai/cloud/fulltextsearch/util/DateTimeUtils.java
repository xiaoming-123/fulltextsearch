package com.zhangxiaobai.cloud.fulltextsearch.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具类
 *
 * @author zhangcq
 */
public class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static String getString() {
        return LocalDateTime.now().format(getDateTimeFormatter("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getString(String format) {
        return LocalDateTime.now().format(getDateTimeFormatter(format));
    }

    public static DateTimeFormatter getDateTimeFormatter(String format) {
        return DateTimeFormatter.ofPattern(format);
    }

}
