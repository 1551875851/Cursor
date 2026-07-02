package com.mailcursor.operlog.controller;

import com.mailcursor.auth.AuthUser;
import com.mailcursor.auth.AuthUserContext;
import com.mailcursor.common.ApiResult;
import com.mailcursor.operlog.model.SysOperLog;
import com.mailcursor.operlog.service.SysOperLogService;
import com.mailcursor.util.ClientIpUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/system/oper-logs")
public class SysOperLogController {

    private final SysOperLogService sysOperLogService;

    public SysOperLogController(SysOperLogService sysOperLogService) {
        this.sysOperLogService = sysOperLogService;
    }

    @GetMapping
    public ApiResult<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operType,
            @RequestParam(required = false) String menuName) {
        return ApiResult.success(sysOperLogService.list(pageNum, pageSize, username, operType, menuName));
    }

    @PostMapping
    public ApiResult<String> record(@RequestBody SysOperLog log, HttpServletRequest request) {
        AuthUser user = AuthUserContext.get();
        if (user != null) {
            log.setUserId(user.getId());
            log.setUsername(user.getUsername());
        }
        if (!org.springframework.util.StringUtils.hasText(log.getClientIp())) {
            log.setClientIp(ClientIpUtils.getClientIp(request));
        }
        sysOperLogService.record(log);
        return ApiResult.success("记录成功", "ok");
    }
}
