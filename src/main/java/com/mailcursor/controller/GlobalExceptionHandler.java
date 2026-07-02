package com.mailcursor.controller;

import com.mailcursor.common.ApiResult;
import com.mailcursor.service.LogPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final LogPersistenceService logPersistenceService;

    public GlobalExceptionHandler(LogPersistenceService logPersistenceService) {
        this.logPersistenceService = logPersistenceService;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResult<Void>> handleNotReadable(HttpMessageNotReadableException ex) {
        log.error("请求体解析失败：{}", ex.getMessage());
        logPersistenceService.saveSystemEvent("ERROR", "VALIDATION", "请求体解析失败", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResult.fail(400, "请求体格式错误，请使用 JSON 格式，例如：{\"username\":\"admin\",\"password\":\"admin123\"}"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("请求参数校验失败：{}", errors);
        logPersistenceService.saveSystemEvent("ERROR", "VALIDATION", "请求参数校验失败", errors);
        return ResponseEntity.badRequest().body(ApiResult.fail(400, errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResult<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("参数或业务校验失败：{}", ex.getMessage());
        logPersistenceService.saveSystemEvent("ERROR", "VALIDATION", ex.getMessage(), null);
        return ResponseEntity.badRequest().body(ApiResult.fail(400, ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResult<Void>> handleIllegalState(IllegalStateException ex) {
        log.error("业务状态异常：{}", ex.getMessage(), ex);
        logPersistenceService.saveSystemEvent("ERROR", "BUSINESS", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResult.fail(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleGeneral(Exception ex) {
        log.error("请求处理失败：{}", ex.getMessage(), ex);
        logPersistenceService.saveSystemEvent("ERROR", "REQUEST", "请求处理失败", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResult.fail("服务器内部错误: " + ex.getMessage()));
    }
}
