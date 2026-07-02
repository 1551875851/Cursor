package com.mailcursor.mapper.dto;

public class RuankaoScanLogRecord {

    private String clientIp;
    private String triggerType;
    private Integer matched;
    private Integer emailSent;
    private String message;
    private String matchedTitles;
    private String status;
    private String errorMessage;
    private String createdAt;

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public Integer getMatched() {
        return matched;
    }

    public void setMatched(Integer matched) {
        this.matched = matched;
    }

    public Integer getEmailSent() {
        return emailSent;
    }

    public void setEmailSent(Integer emailSent) {
        this.emailSent = emailSent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMatchedTitles() {
        return matchedTitles;
    }

    public void setMatchedTitles(String matchedTitles) {
        this.matchedTitles = matchedTitles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
