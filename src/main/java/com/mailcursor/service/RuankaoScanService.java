package com.mailcursor.service;

import com.mailcursor.config.RuankaoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RuankaoScanService {

    private static final Logger log = LoggerFactory.getLogger(RuankaoScanService.class);
    private static final Pattern TITLE_PATTERN = Pattern.compile("title=\"([^\"]+)\"");
    private static final String WORK_SECTION_START = "<div class=\"work\">";

    private final RestTemplate restTemplate;
    private final RuankaoProperties ruankaoProperties;
    private final MailService mailService;

    public RuankaoScanService(RestTemplate restTemplate,
                              RuankaoProperties ruankaoProperties,
                              MailService mailService) {
        this.restTemplate = restTemplate;
        this.ruankaoProperties = ruankaoProperties;
        this.mailService = mailService;
    }

    public ScanResult scanAndNotify() {
        log.info("开始扫描软考首页工作动态，url={}，关键字={}",
                ruankaoProperties.getHomepageUrl(), ruankaoProperties.getKeywords());
        try {
            String html = fetchHomepage();
            String workSection = extractWorkSection(html);
            List<String> matchedTitles = findMatchedTitles(workSection);

            if (matchedTitles.isEmpty()) {
                log.info("工作动态中未发现成绩查询相关通知");
                return ScanResult.noMatch();
            }

            log.info("工作动态匹配到 {} 条成绩查询通知：{}", matchedTitles.size(), matchedTitles);
            log.info("准备发送软考成绩通知邮件，主题={}，内容={}",
                    ruankaoProperties.getNotifySubject(), ruankaoProperties.getNotifyContent());
            mailService.sendToSelf(
                    ruankaoProperties.getNotifySubject(),
                    ruankaoProperties.getNotifyContent()
            );
            log.info("软考成绩通知邮件已发送，匹配标题={}", matchedTitles.get(0));
            return ScanResult.sent(matchedTitles);
        } catch (Exception ex) {
            log.error("软考工作动态扫描失败：{}", ex.getMessage(), ex);
            throw ex;
        }
    }

    private String fetchHomepage() {
        log.info("正在抓取软考首页：{}", ruankaoProperties.getHomepageUrl());
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) MailCursor/1.0");
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    ruankaoProperties.getHomepageUrl(),
                    HttpMethod.GET,
                    new HttpEntity<Void>(headers),
                    byte[].class
            );
            byte[] body = response.getBody();
            if (body == null || body.length == 0) {
                log.error("软考首页返回为空，url={}", ruankaoProperties.getHomepageUrl());
                throw new IllegalStateException("软考首页返回为空");
            }
            log.info("软考首页抓取成功，响应大小={} bytes", body.length);
            return new String(body, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.error("软考首页抓取失败，url={}，原因={}", ruankaoProperties.getHomepageUrl(), ex.getMessage(), ex);
            throw ex;
        }
    }

    private String extractWorkSection(String html) {
        int start = html.indexOf(WORK_SECTION_START);
        if (start < 0) {
            log.error("软考首页 HTML 中未找到工作动态区块");
            throw new IllegalStateException("未找到工作动态区块");
        }
        int end = html.indexOf("<div class=\"server-box\">", start);
        if (end < 0) {
            end = html.length();
        }
        return html.substring(start, end);
    }

    private List<String> findMatchedTitles(String workSection) {
        List<String> keywords = ruankaoProperties.getKeywords();
        List<String> matched = new ArrayList<String>();
        Matcher matcher = TITLE_PATTERN.matcher(workSection);
        while (matcher.find()) {
            String title = matcher.group(1).trim();
            if (containsKeyword(title, keywords)) {
                matched.add(title);
            }
        }
        return matched;
    }

    private boolean containsKeyword(String text, List<String> keywords) {
        for (String keyword : keywords) {
            if (StringUtils.hasText(keyword) && text.contains(keyword.trim())) {
                return true;
            }
        }
        return false;
    }

    public static class ScanResult {
        private final boolean matched;
        private final boolean emailSent;
        private final List<String> matchedTitles;

        private ScanResult(boolean matched, boolean emailSent, List<String> matchedTitles) {
            this.matched = matched;
            this.emailSent = emailSent;
            this.matchedTitles = matchedTitles;
        }

        static ScanResult noMatch() {
            return new ScanResult(false, false, new ArrayList<String>());
        }

        static ScanResult sent(List<String> matchedTitles) {
            return new ScanResult(true, true, matchedTitles);
        }

        public boolean isMatched() {
            return matched;
        }

        public boolean isEmailSent() {
            return emailSent;
        }

        public List<String> getMatchedTitles() {
            return matchedTitles;
        }

        public String getMessage() {
            if (!matched) {
                return "工作动态中未发现成绩查询相关通知";
            }
            return "发现成绩查询通知，邮件已发送";
        }
    }
}
