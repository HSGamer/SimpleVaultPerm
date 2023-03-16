package me.hsgamer.simplevaultperm.util;

import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class ValueUtil {
    public static Map<String, Boolean> toBooleanMap(List<String> list) {
        Map<String, Boolean> map = new HashMap<>();
        for (String key : list) {
            if (key.startsWith("-")) {
                map.put(key.substring(1), false);
            } else {
                map.put(key, true);
            }
        }
        return map;
    }

    public static List<String> toStringList(Map<String, Boolean> map) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) {
                list.add(entry.getKey());
            } else {
                list.add("-" + entry.getKey());
            }
        }
        return list;
    }

    public static Map<String, Long> toLongMap(Object object) {
        Map<String, Long> map = new HashMap<>();
        if (object instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
                try {
                    map.put(Objects.toString(entry.getKey()), Long.parseLong(Objects.toString(entry.getValue())));
                } catch (NumberFormatException ignored) {
                    // IGNORED
                }
            }
        }
        return map;
    }
}
