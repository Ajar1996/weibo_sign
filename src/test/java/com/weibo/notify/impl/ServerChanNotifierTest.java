package com.weibo.notify.impl;

import com.weibo.config.NotifyTemplateProperties;
import com.weibo.model.Topic;
import com.weibo.model.WeiboConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ServerChanNotifierTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private NotifyTemplateProperties templateProperties;

    private ServerChanNotifier serverChanNotifier;
    private WeiboConfig config;
    private List<Topic> signedTopics;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 设置模板属性
        when(templateProperties.getServerchan()).thenReturn("【${username}】微博签到完成\n已签超话：${count}个\n列表：${details}");
        
        serverChanNotifier = new ServerChanNotifier(restTemplate, templateProperties);
        
        // 设置测试配置
        config = new WeiboConfig();
        config.setServerKey("SCT273149TKcuZJH5X90Ui5wWyaYnq3g0i");
        config.setName("测试配置");
        
        // 设置测试数据
        signedTopics = new ArrayList<>();
        Topic topic1 = new Topic();
        topic1.setTitle("测试超话1");
        topic1.setSignStatus("已签");
        topic1.setLevel("2");
        signedTopics.add(topic1);
        
        Topic topic2 = new Topic();
        topic2.setTitle("测试超话2");
        topic2.setSignStatus("已签");
        topic2.setLevel("2");
        signedTopics.add(topic2);

        // 模拟 Server 酱 API 的响应
        String mockResponse = "{\"code\":0,\"message\":\"success\",\"data\":{\"pushid\":\"123456\",\"readkey\":\"abcdef\"}}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);
    }

    @Test
    void testNotify() {
        // 执行通知
        serverChanNotifier.notify(config, "testUser", signedTopics);

        // 验证 RestTemplate 是否被调用
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testNotifyWithEmptyTopics() {
        // 测试空列表
        serverChanNotifier.notify(config, "testUser", new ArrayList<>());
        
        // 验证 RestTemplate 是否被调用
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testNotifyWithNullConfig() {
        // 测试空配置
        WeiboConfig nullConfig = new WeiboConfig();
        serverChanNotifier.notify(nullConfig, "testUser", signedTopics);
        
        // 验证 RestTemplate 是否被调用
        verify(restTemplate, never()).getForObject(anyString(), eq(String.class));
    }
} 