package com.mailcursor.controller;

import com.mailcursor.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/mail")
@Validated
public class MailController {

    private static final Logger log = LoggerFactory.getLogger(MailController.class);

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> send(@RequestBody @Validated SendMailRequest request) {
        log.info("收到邮件发送请求，主题={}", request.getSubject());
        mailService.sendToSelf(request.getSubject(), request.getContent());
        log.info("邮件发送接口处理完成，主题={}", request.getSubject());
        return ResponseEntity.ok(Collections.singletonMap("message", "邮件发送成功"));
    }

    public static class SendMailRequest {

        @NotBlank(message = "主题不能为空")
        private String subject;

        @NotBlank(message = "内容不能为空")
        private String content;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
