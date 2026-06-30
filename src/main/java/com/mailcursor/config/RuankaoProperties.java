package com.mailcursor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "ruankao")
public class RuankaoProperties {

    private String homepageUrl = "https://www.ruankao.org.cn/";
    private List<String> keywords = new ArrayList<String>();
    private String notifySubject = "软考成绩通知";
    private String notifyContent = "软件成绩已出，可登录查询";
    private boolean scheduleEnabled = true;
    private String scheduleCron = "0 0 9,15,21 * * ?";

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getNotifySubject() {
        return notifySubject;
    }

    public void setNotifySubject(String notifySubject) {
        this.notifySubject = notifySubject;
    }

    public String getNotifyContent() {
        return notifyContent;
    }

    public void setNotifyContent(String notifyContent) {
        this.notifyContent = notifyContent;
    }

    public boolean isScheduleEnabled() {
        return scheduleEnabled;
    }

    public void setScheduleEnabled(boolean scheduleEnabled) {
        this.scheduleEnabled = scheduleEnabled;
    }

    public String getScheduleCron() {
        return scheduleCron;
    }

    public void setScheduleCron(String scheduleCron) {
        this.scheduleCron = scheduleCron;
    }
}
