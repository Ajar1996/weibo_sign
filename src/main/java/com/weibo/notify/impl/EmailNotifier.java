package com.weibo.notify.impl;

import com.weibo.config.NotifyTemplateProperties;
import com.weibo.model.Topic;
import com.weibo.model.WeiboConfig;
import com.weibo.notify.Notifier;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotifier implements Notifier {
    private final JavaMailSender mailSender;
    private final NotifyTemplateProperties templates;
    private static final String[] WEEKDAYS = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};

    @Value("${spring.mail.admin}") // 从配置读取管理员邮箱
    private String adminEmail;

    @Override
    public void notify(WeiboConfig config, String username, List<Topic> signedTopics) {
        if (StringUtils.isBlank(config.getEmail())) {
            return;
        }

        try {
            // 获取当前日期和星期
            LocalDate now = LocalDate.now();
            String date = now.format(DateTimeFormatter.ofPattern("MM-dd"));
            String weekday = WEEKDAYS[now.getDayOfWeek().getValue() - 1];

            // 统计成功和失败的数量
            long successCount = signedTopics.stream()
                .filter(topic -> "已签".equals(topic.getSignStatus()))
                .count();
            List<Topic> failedTopics = signedTopics.stream()
                .filter(topic -> "签到".equals(topic.getSignStatus()))
                .collect(Collectors.toList());

            // 从YAML加载模板
            String content = templates.getEmail()
                .replace("${date}", date)
                .replace("${weekday}", weekday)
                .replace("${username}", username)
                .replace("${count}", String.valueOf(successCount))
                .replace("${total}", String.valueOf(signedTopics.size()))
                .replace("${details}", buildDetails(signedTopics, failedTopics));

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("ajar1996@163.com");
            helper.setTo(config.getEmail().split(";"));
            helper.setSubject("微博签到结果");
            helper.setText(content, false); // 纯文本模式
            
            mailSender.send(message);
            log.info("邮件通知已发送至: {}", config.getEmail());
        } catch (Exception e) {
            log.error("邮件发送失败", e);
        }
    }

    public void sendAlert(String title, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("ajar1996@163.com");
            helper.setTo(adminEmail); // 直接发送到管理员邮箱
            helper.setSubject(title);
            helper.setText(content);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("告警邮件发送失败", e);
        }
    }

    private String buildDetails(List<Topic> allTopics, List<Topic> failedTopics) {
        StringBuilder details = new StringBuilder();
        
        // 添加成功签到的超话
        details.append("成功签到\n\n");
        allTopics.stream()
            .filter(topic -> "已签".equals(topic.getSignStatus()))
            .forEach(topic -> details.append("- ").append(topic.getTitle())
                .append(" (等级: ").append(topic.getLevel()).append(") ✅\n"));

        // 如果有失败的，添加失败的超话
        if (!failedTopics.isEmpty()) {
            details.append("\n签到失败\n\n");
            failedTopics.forEach(topic -> details.append("- ")
                .append(topic.getTitle()).append(" ❌\n"));
        }

        return details.toString();
    }
}