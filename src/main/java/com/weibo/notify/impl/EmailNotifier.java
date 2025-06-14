package com.weibo.notify.impl;

import com.weibo.config.NotifyTemplateProperties;
import com.weibo.model.Topic;
import com.weibo.model.WeiboConfig;
import com.weibo.notify.Notifier;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotifier implements Notifier {
    private final JavaMailSender mailSender;
    private final NotifyTemplateProperties templates;

    @Override
    public void notify(WeiboConfig config, String username, List<Topic> signedTopics) {
        if (StringUtils.isBlank(config.getEmail())) {
            return;
        }

        try {
            // 从YAML加载模板
            String content = templates.getEmail()
                .replace("${username}", username)
                .replace("${count}", String.valueOf(signedTopics.size()))
                .replace("${details}", buildDetails(signedTopics));

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

    private String buildDetails(List<Topic> topics) {
        return topics.stream()
            .map(t -> t.getTitle() + "(Lv." + t.getLevel() + ")")
            .collect(Collectors.joining("、"));
    }
}