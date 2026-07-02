package com.mailcursor.service;

import com.mailcursor.mapper.AppLogMapper;
import com.mailcursor.mapper.dto.AccessLogRecord;
import com.mailcursor.mapper.dto.MailLogRecord;
import com.mailcursor.mapper.dto.RuankaoScanLogRecord;
import com.mailcursor.mapper.dto.SystemEventLogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LogPersistenceService {

    private static final Logger log = LoggerFactory.getLogger(LogPersistenceService.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AppLogMapper appLogMapper;

    public LogPersistenceService(AppLogMapper appLogMapper) {
        this.appLogMapper = appLogMapper;
    }

    public void saveAccessLog(String clientIp, String httpMethod, String requestUri,
                              Integer responseStatus, Long costMs, String logType, String errorMessage) {
        execute("access_log", () -> {
            AccessLogRecord record = new AccessLogRecord();
            record.setClientIp(clientIp);
            record.setHttpMethod(httpMethod);
            record.setRequestUri(requestUri);
            record.setResponseStatus(responseStatus);
            record.setCostMs(costMs);
            record.setLogType(logType);
            record.setErrorMessage(errorMessage);
            record.setCreatedAt(now());
            appLogMapper.insertAccessLog(record);
        });
    }

    public void saveMailLog(String clientIp, String sender, String recipient, String subject,
                            String content, String status, String errorMessage) {
        execute("mail_send_log", () -> {
            MailLogRecord record = new MailLogRecord();
            record.setClientIp(clientIp);
            record.setSender(sender);
            record.setRecipient(recipient);
            record.setSubject(subject);
            record.setContent(content);
            record.setStatus(status);
            record.setErrorMessage(errorMessage);
            record.setCreatedAt(now());
            appLogMapper.insertMailLog(record);
        });
    }

    public void saveRuankaoScanLog(String clientIp, String triggerType, boolean matched, boolean emailSent,
                                   String message, List<String> matchedTitles, String status, String errorMessage) {
        execute("ruankao_scan_log", () -> {
            RuankaoScanLogRecord record = new RuankaoScanLogRecord();
            record.setClientIp(clientIp);
            record.setTriggerType(triggerType);
            record.setMatched(matched ? 1 : 0);
            record.setEmailSent(emailSent ? 1 : 0);
            record.setMessage(message);
            record.setMatchedTitles(joinTitles(matchedTitles));
            record.setStatus(status);
            record.setErrorMessage(errorMessage);
            record.setCreatedAt(now());
            appLogMapper.insertRuankaoScanLog(record);
        });
    }

    public void saveSystemEvent(String level, String category, String message, String detail) {
        execute("system_event_log", () -> {
            SystemEventLogRecord record = new SystemEventLogRecord();
            record.setLogLevel(level);
            record.setCategory(category);
            record.setMessage(message);
            record.setDetail(detail);
            record.setCreatedAt(now());
            appLogMapper.insertSystemEventLog(record);
        });
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
