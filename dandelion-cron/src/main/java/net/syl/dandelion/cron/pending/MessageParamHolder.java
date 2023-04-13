package net.syl.dandelion.cron.pending;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageParamHolder {

    private static Map<String, List<Map<String, String>>> paramMap = new HashMap<>();

    public static void put(String key, List<Map<String, String>> list) {
        paramMap.put(key, list);
    }

    public static List<Map<String, String>> get(String key) {
        return paramMap.get(key);
    }

    public static void remove(String key) {
        paramMap.remove(key);
    }
}
