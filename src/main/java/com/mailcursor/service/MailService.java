package com.mailcursor.service;

import com.mailcursor.config.MailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.internet.MimeMessage;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public MailService(JavaMailSender mailSender, MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    public void sendToSelf(String subject, String content) {
        String recipient = mailProperties.getAllowedRecipient();
        if (!StringUtils.hasText(recipient)) {
            log.error("邮件发送失败：未配置 mail.allowed-recipient");
            throw new IllegalStateException("mail.allowed-recipient is not configured");
        }

        log.info("准备发送邮件，发件人={}，收件人={}，主题={}", fromAddress, recipient, subject);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(content, false);

            mailSender.send(mimeMessage);
            log.info("邮件发送成功，收件人={}，主题={}", recipient, subject);
        } catch (Exception ex) {
            log.error("邮件发送失败，收件人={}，主题={}，原因={}", recipient, subject, ex.getMessage(), ex);
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            throw new IllegalStateException("邮件发送失败: " + ex.getMessage(), ex);
        }
    }
}
