package com.mailcursor.controller;

import com.mailcursor.service.LogPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final LogPersistenceService logPersistenceService;

    public GlobalExceptionHandler(LogPersistenceService logPersistenceService) {
        this.logPersistenceService = logPersistenceService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("请求参数校验失败：{}", errors);
        logPersistenceService.saveSystemEvent("ERROR", "VALIDATION", "请求参数校验失败", errors);
        return ResponseEntity.badRequest().body(Collections.singletonMap("message", errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("参数或业务校验失败：{}", ex.getMessage());
        logPersistenceService.saveSystemEvent("ERROR", "VALIDATION", ex.getMessage(), null);
        return ResponseEntity.badRequest().body(Collections.singletonMap("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        log.error("业务状态异常：{}", ex.getMessage(), ex);
        logPersistenceService.saveSystemEvent("ERROR", "BUSINESS", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("message", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        log.error("邮件相关请求处理失败：{}", ex.getMessage(), ex);
        logPersistenceService.saveSystemEvent("ERROR", "REQUEST", "邮件相关请求处理失败", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("message", "邮件发送失败: " + ex.getMessage()));
    }
}
