package com.weibo.notify.impl;

import com.weibo.config.NotifyTemplateProperties;
import com.weibo.model.Topic;
import com.weibo.model.WeiboConfig;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailNotifierTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private NotifyTemplateProperties templateProperties;

    @Mock
    private MimeMessage mimeMessage;

    private EmailNotifier emailNotifier;
    private WeiboConfig config;
    private List<Topic> signedTopics;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 设置模板属性
        when(templateProperties.getEmail()).thenReturn(
            "### --------微博超话签到-------- \n" +
            "${date} ${weekday}\n\n" +
            "用户 ${username}\n\n" +
            "成功签到 ${count} / ${total}\n\n" +
            "${details}"
        );
        
        emailNotifier = new EmailNotifier(mailSender, templateProperties);
        ReflectionTestUtils.setField(emailNotifier, "adminEmail", "test@example.com");
        
        // 设置测试配置
        config = new WeiboConfig();
        config.setId(1);
        config.setName("测试配置");
        config.setEmail("991823402@qq.com");
        config.setRowUrl("https://api.weibo.cn/2/cardlist?aid=01A8vEx3Dot4keenEPVXPe4amUH0z_P7BrosagmyPJ-EwP264.&c=weicoabroad&containerid=100803_-_followsuper&count=20&from=12E5093010&gsid=_2A25K0iy_DeRxGeBP71AS8i_MyTqIHXVnxid3rDV6PUJbj9AbLXP_kWpNRVJncoMDvdK-2s9F5qNWK0CuTOMuWI4R&lang=zh_CN&page=1&s=56222222&ua=iPhone14%2C2_iOS18.3.2_Weibo_intl._6680_wifi__iphone__os18.3.2&v_p=90");
        config.setSignOnceCount(8);
        config.setServerKey("SCT273149TKcuZJH5X90Ui5wWyaYnq3g0i");
        config.setIsSort("NO_SORT");
        config.setDispType("DETAIL");
        config.setIsActive(true);
        
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

        // 模拟 MimeMessage
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testNotify() {
        // 执行通知
        emailNotifier.notify(config, "testUser", signedTopics);

        // 验证邮件发送
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testNotifyWithEmptyTopics() {
        // 测试空列表
        emailNotifier.notify(config, "testUser", new ArrayList<>());

        // 验证邮件发送
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }



    @Test
    void testSendAlert() {
        // 测试发送告警邮件
        emailNotifier.sendAlert("测试告警", "测试内容");

        // 验证邮件发送
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

} 