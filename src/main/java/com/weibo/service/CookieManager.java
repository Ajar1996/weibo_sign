package com.weibo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.weibo.config.AppConfig;
import com.weibo.exception.WeiboApiException;
import com.weibo.model.WeiboConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CookieManager {
    @Autowired
    private WeiboApiClient apiClient;



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