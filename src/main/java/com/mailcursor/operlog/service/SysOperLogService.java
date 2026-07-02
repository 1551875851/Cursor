package com.mailcursor.operlog.service;

import com.mailcursor.auth.AuthUser;
import com.mailcursor.auth.AuthUserContext;
import com.mailcursor.common.DateTimeUtils;
import com.mailcursor.mapper.SysOperLogMapper;
import com.mailcursor.mapper.dto.OperLogQuery;
import com.mailcursor.operlog.model.SysOperLog;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysOperLogService {

    private final SysOperLogMapper sysOperLogMapper;

    public SysOperLogService(SysOperLogMapper sysOperLogMapper) {
        this.sysOperLogMapper = sysOperLogMapper;
    }

    public void record(SysOperLog log) {
        if (log.getUserId() == null || !StringUtils.hasText(log.getUsername())) {
            AuthUser user = AuthUserContext.get();
            if (user != null) {
                if (log.getUserId() == null) {
                    log.setUserId(user.getId());
                }
                if (!StringUtils.hasText(log.getUsername())) {
                    log.setUsername(user.getUsername());
                }
            }
        }
        if (!StringUtils.hasText(log.getModuleName())) {
            log.setModuleName("系统");
        }
        if (!StringUtils.hasText(log.getOperType())) {
            log.setOperType("ACTION");
        }
        if (log.getStatus() == null) {
            log.setStatus(1);
        }
        log.setCreatedAt(DateTimeUtils.now());
        sysOperLogMapper.insert(log);
    }

    public void recordLogin(Long userId, String username, String clientIp) {
        SysOperLog log = new SysOperLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setModuleName("认证登录");
        log.setMenuName("登录页");
        log.setOperType("LOGIN");
        log.setOperDesc("用户登录系统");
        log.setRequestUri("/api/auth/login");
        log.setRequestMethod("POST");
        log.setClientIp(clientIp);
        log.setStatus(1);
        record(log);
    }

    public Map<String, Object> list(int pageNum, int pageSize, String username, String operType, String menuName) {
        OperLogQuery query = new OperLogQuery();
        if (StringUtils.hasText(username)) {
            query.setUsername("%" + username.trim() + "%");
        }
        if (StringUtils.hasText(operType)) {
            query.setOperType(operType.trim());
        }
        if (StringUtils.hasText(menuName)) {
            query.setMenuName("%" + menuName.trim() + "%");
        }
        query.setOffset(Math.max(pageNum - 1, 0) * pageSize);
        query.setLimit(pageSize);

        int total = sysOperLogMapper.countByQuery(query);
        List<SysOperLog> list = sysOperLogMapper.selectByQuery(query);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        return result;
    }
}
