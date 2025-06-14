package com.weibo.notify;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotifyTemplateEngine {
    private static final Pattern PATTERN = Pattern.compile("\\$\\{(\\w+)\\}");

    public static String render(String template, Map<String, Object> params) {
        Matcher matcher = PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = params.getOrDefault(key, "");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value.toString()));
        }
        
        return matcher.appendTail(sb).toString();
    }
}