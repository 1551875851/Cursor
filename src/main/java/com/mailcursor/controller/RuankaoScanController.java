package com.mailcursor.controller;

import com.mailcursor.service.RuankaoScanService;
import com.mailcursor.util.ClientIpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ruankao")
public class RuankaoScanController {

    private static final Logger log = LoggerFactory.getLogger(RuankaoScanController.class);

    private final RuankaoScanService ruankaoScanService;

    public RuankaoScanController(RuankaoScanService ruankaoScanService) {
        this.ruankaoScanService = ruankaoScanService;
    }

    @PostMapping("/scan")
    public ResponseEntity<Map<String, Object>> scan(HttpServletRequest httpRequest) {
        String clientIp = ClientIpUtils.getClientIp(httpRequest);
        log.info("收到软考工作动态扫描请求，clientIp={}", clientIp);
        RuankaoScanService.ScanResult result = ruankaoScanService.scanAndNotify();
        log.info("软考扫描完成，clientIp={}，matched={}，emailSent={}，message={}",
                clientIp, result.isMatched(), result.isEmailSent(), result.getMessage());
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("message", result.getMessage());
        body.put("matched", result.isMatched());
        body.put("emailSent", result.isEmailSent());
        body.put("matchedTitles", result.getMatchedTitles());
        return ResponseEntity.ok(body);
    }
}
