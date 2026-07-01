package com.mailcursor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LogPersistenceService {

    private static final Logger log = LoggerFactory.getLogger(LogPersistenceService.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final JdbcTemplate jdbcTemplate;

    public LogPersistenceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveAccessLog(String clientIp, String httpMethod, String requestUri,
                              Integer responseStatus, Long costMs, String logType, String errorMessage) {
        execute("access_log", () -> jdbcTemplate.update(
                "INSERT INTO access_log (client_ip, http_method, request_uri, response_status, cost_ms, log_type, error_message, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                clientIp, httpMethod, requestUri, responseStatus, costMs, logType, errorMessage, now()));
    }

    public void saveMailLog(String clientIp, String sender, String recipient, String subject,
                            String content, String status, String errorMessage) {
        execute("mail_send_log", () -> jdbcTemplate.update(
                "INSERT INTO mail_send_log (client_ip, sender, recipient, subject, content, status, error_message, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                clientIp, sender, recipient, subject, content, status, errorMessage, now()));
    }

    public void saveRuankaoScanLog(String clientIp, String triggerType, boolean matched, boolean emailSent,
                                   String message, List<String> matchedTitles, String status, String errorMessage) {
        execute("ruankao_scan_log", () -> jdbcTemplate.update(
                "INSERT INTO ruankao_scan_log (client_ip, trigger_type, matched, email_sent, message, matched_titles, status, error_message, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                clientIp,
                triggerType,
                matched ? 1 : 0,
                emailSent ? 1 : 0,
                message,
                joinTitles(matchedTitles),
                status,
                errorMessage,
                now()));
    }

    public void saveSystemEvent(String level, String category, String message, String detail) {
        execute("system_event_log", () -> jdbcTemplate.update(
                "INSERT INTO system_event_log (log_level, category, message, detail, created_at) VALUES (?, ?, ?, ?, ?)",
                level, category, message, detail, now()));
    }

    private String joinTitles(List<String> matchedTitles) {
        if (matchedTitles == null || matchedTitles.isEmpty()) {
            return null;
        }
        return String.join(" | ", matchedTitles);
    }

    private String now() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    private void execute(String tableName, Runnable action) {
        try {
            action.run();
        } catch (Exception ex) {
            log.warn("写入{}失败：{}", tableName, ex.getMessage());
        }
    }
}
