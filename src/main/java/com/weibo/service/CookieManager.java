package com.weibo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.weibo.config.AppConfig;
import com.weibo.exception.WeiboApiException;
import com.weibo.model.WeiboConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CookieManager {
    private final Map<String, String> userCookies = new HashMap<>();
    private final WeiboApiClient apiClient;
    private final String rowUrl;  // 直接存储需要的配置

    public CookieManager(WeiboApiClient apiClient, String rowUrl) {
        this.apiClient = apiClient;
        this.rowUrl = rowUrl;
    }

    public Map<String, String> loadCookiesFromConfig(WeiboConfig config) throws IOException {
        Map<String, String> userCookies = new HashMap<>();
        String[] urls = config.getRowUrl().split(";");

        for (String url : urls) {
            String cookie = extractCookieFromUrl(url);
            String username = getUsernameFromCookie(cookie);
            userCookies.put(username, cookie);
            randomSleep(3, 5);
        }

        return userCookies;
    }

    public void loadUserCookies() throws IOException {
        String[] urls = this.rowUrl.split(";");  // 使用存储的配置
        for (String url : urls) {
            String cookie = extractCookieFromUrl(url);
            String username = getUsernameFromCookie(cookie);
            userCookies.put(username, cookie);
            randomSleep(3, 5);
        }
    }

    public Map<String, String> getUserCookies() {
        return userCookies;
    }

    public boolean isEmpty() {
        return userCookies.isEmpty();
    }

    private String extractCookieFromUrl(String url) {
        int startPos = url.contains("cardlist?") ?
                url.indexOf("cardlist?") + "cardlist?".length() :
                url.indexOf("aid");
        return url.substring(startPos);
    }

    private String getUsernameFromCookie(String cookie) throws IOException {
        JsonNode json = apiClient.getUserInfo(cookie);
        return json.has("name") ?
                json.get("name").asText() :
                "user_" + System.currentTimeMillis();
    }

    private void randomSleep(int minSec, int maxSec) {
        try {
            TimeUnit.SECONDS.sleep(minSec + new Random().nextInt(maxSec - minSec));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}