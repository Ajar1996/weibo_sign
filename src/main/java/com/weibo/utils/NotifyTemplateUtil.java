package com.weibo.utils;

import java.util.Map;

public class NotifyTemplateUtil {
    public static String render(String template, Map<String, Object> params) {
        String result = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            result = result.replace("${" + entry.getKey() + "}", 
                entry.getValue() != null ? entry.getValue().toString() : "");
        }
        return result;
    }
}