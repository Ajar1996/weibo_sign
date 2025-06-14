package com.weibo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weibo.constant.ResultCode;
import com.weibo.exception.WeiboException;
import com.weibo.model.WeiboConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
@Service
@Slf4j
public class WeiboApiClient {
    private static final String BASE_API_URL = "https://api.weibo.cn";
    private static final MediaType FORM = MediaType.get("application/x-www-form-urlencoded; charset=utf-8");

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final WeiboConfig config;

    public WeiboApiClient(WeiboConfig config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }


    public JsonNode getUserInfo(String cookie) {
        Request request = new Request.Builder()
                .url(BASE_API_URL + "/2/users/update")
                .post(buildFormBody(cookie))
                .headers(buildCommonHeaders())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return objectMapper.readTree(response.body().string());
        } catch (IOException e) {
            log.error("获取用户信息出错{}",ResultCode.CONNECT_ERROR.getMessage(),e);
            throw new WeiboException(
                    ResultCode.CONNECT_ERROR,
                    e.getMessage()
            );
        }
    }

    public JsonNode getTopicList(String cookie, String page, String sinceId) throws IOException {
        Request request = buildPaginatedRequest(cookie, page, sinceId);
        try (Response response = httpClient.newCall(request).execute()) {
            return objectMapper.readTree(response.body().string());
        }
    }

    public JsonNode signTopic(String cookie, String signAction) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_API_URL + signAction)
                .post(buildFormBody(cookie))
                .headers(buildCommonHeaders())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return objectMapper.readTree(response.body().string());
        }
    }

    private Request buildPaginatedRequest(String cookie, String page, String sinceId) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_API_URL + "/2/cardlist").newBuilder()
                .addQueryParameter("page", page)
                .addQueryParameter("since_id", sinceId);

        Arrays.stream(cookie.split("&"))
                .filter(p -> p.contains("="))
                .forEach(p -> {
                    String[] kv = p.split("=", 2);
                    urlBuilder.addQueryParameter(kv[0], kv[1]);
                });

        return new Request.Builder()
                .url(urlBuilder.build())
                .headers(buildHeaders())
                .build();
    }

    private Headers buildHeaders() {
        return new Headers.Builder()
                .add("Accept", "*/*")
                .add("Host", "api.weibo.cn")
                .add("User-Agent", "WeiboOverseas/4.4.1 (iPhone; iOS 14.7.1; Scale/3.00)")
                .add("Accept-Language", "zh-Hans-CN;q=1, en-CN;q=0.9")
                .add("X-Requested-With", "XMLHttpRequest")
                .add("Referer", "https://api.weibo.cn/")
                .build();
    }

    private Headers buildCommonHeaders() {
        return new Headers.Builder()
                .add("User-Agent", "WeiboOverseas/4.4.1 (iPhone; iOS 14.7.1; Scale/3.00)")
                .add("Accept-Language", "zh-Hans-CN;q=1, en-CN;q=0.9")
                .build();
    }

    private RequestBody buildFormBody(String params) {
        return RequestBody.create(params, FORM);
    }
}