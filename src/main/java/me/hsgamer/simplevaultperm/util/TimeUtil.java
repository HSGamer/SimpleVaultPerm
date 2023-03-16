package me.hsgamer.simplevaultperm.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.time.DurationFormatUtils;

@UtilityClass
public class TimeUtil {
    public static long toMillis(String time) {
        if (time == null || time.isEmpty()) {
            return 0;
        }
        long millis = 0;
        StringBuilder number = new StringBuilder();
        for (char c : time.toCharArray()) {
            if (Character.isDigit(c)) {
                number.append(c);
            } else {
                millis += getMillis(number.toString(), c);
                number = new StringBuilder();
            }
        }
        return millis;
    }

    private static long getMillis(String number, char unit) {
        if (number.isEmpty()) {
            return 0;
        }
        long value = Long.parseLong(number);
        switch (unit) {
            case 'd':
                return value * 86400000;
            case 'h':
                return value * 3600000;
            case 'm':
                return value * 60000;
            case 's':
                return value * 1000;
            default:
                return value;
        }
    }

    public static String displayDuration(long time) {
        return DurationFormatUtils.formatDuration(time, "HH:mm:ss", true);
    }
}
