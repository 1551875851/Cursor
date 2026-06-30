package com.mailcursor.service;

import com.mailcursor.config.RuankaoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RuankaoScanScheduler {

    private static final Logger log = LoggerFactory.getLogger(RuankaoScanScheduler.class);

    private final RuankaoScanService ruankaoScanService;
    private final RuankaoProperties ruankaoProperties;

    public RuankaoScanScheduler(RuankaoScanService ruankaoScanService,
                                RuankaoProperties ruankaoProperties) {
        this.ruankaoScanService = ruankaoScanService;
        this.ruankaoProperties = ruankaoProperties;
    }

    @Scheduled(cron = "${ruankao.schedule-cron:0 0 9,15,21 * * ?}")
    public void scheduledScan() {
        if (!ruankaoProperties.isScheduleEnabled()) {
            log.info("软考定时扫描已关闭，跳过本次任务");
            return;
        }
        log.info("开始执行软考定时扫描任务");
        try {
            RuankaoScanService.ScanResult result = ruankaoScanService.scanAndNotify();
            log.info("软考定时扫描完成，matched={}，emailSent={}，message={}",
                    result.isMatched(), result.isEmailSent(), result.getMessage());
        } catch (Exception ex) {
            log.error("软考定时扫描失败：{}", ex.getMessage(), ex);
        }
    }
}
