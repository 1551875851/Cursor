package com.mailcursor.dashboard;

import com.mailcursor.mapper.DashboardStatsMapper;
import com.mailcursor.mapper.dto.DayCount;
import com.mailcursor.mapper.dto.NameCount;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardStatsService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int TREND_DAYS = 7;
    private static final int API_RANK_LIMIT = 10;

    private static final Map<String, String> URI_LABELS = new LinkedHashMap<String, String>();

    static {
        URI_LABELS.put("/api/auth/login", "用户登录");
        URI_LABELS.put("/api/auth/me", "获取当前用户");
        URI_LABELS.put("/api/mail/send", "发送邮件");
        URI_LABELS.put("/api/ruankao/scan", "软考扫描");
        URI_LABELS.put("/api/dashboard/stats", "首页统计");
        URI_LABELS.put("/api/system/users", "用户列表");
        URI_LABELS.put("/api/system/roles", "角色列表");
        URI_LABELS.put("/api/system/orgs", "机构列表");
        URI_LABELS.put("/api/system/menus", "菜单列表");
        URI_LABELS.put("/api/system/menus/tree", "菜单树");
    }

    private final DashboardStatsMapper dashboardStatsMapper;

    public DashboardStatsService(DashboardStatsMapper dashboardStatsMapper) {
        this.dashboardStatsMapper = dashboardStatsMapper;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("summary", buildSummary());
        result.put("accessTrend", buildAccessTrend());
        result.put("featureUsage", buildFeatureUsage());
        result.put("apiRanking", buildApiRanking());
        result.put("statusDistribution", buildStatusDistribution());
        return result;
    }

    private Map<String, Object> buildSummary() {
        String today = LocalDate.now().format(DATE_FORMATTER);
        String todayPrefix = today + "%";

        int todayAccess = intValue(dashboardStatsMapper.countTodayAccess(todayPrefix));
        int todayActiveUsers = intValue(dashboardStatsMapper.countTodayActiveUsers(todayPrefix));
        int totalUsers = intValue(dashboardStatsMapper.countActiveUsers());
        int todaySuccess = intValue(dashboardStatsMapper.countTodayApiSuccess(todayPrefix));
        int todayApiTotal = intValue(dashboardStatsMapper.countTodayApiTotal(todayPrefix));

        double successRate = todayApiTotal == 0
                ? 100D
                : Math.round(todaySuccess * 10000D / todayApiTotal) / 100D;

        Map<String, Object> summary = new HashMap<String, Object>();
        summary.put("todayAccess", todayAccess);
        summary.put("todayActiveUsers", todayActiveUsers);
        summary.put("totalUsers", totalUsers);
        summary.put("todayApiSuccessRate", successRate);
        return summary;
    }

    private List<Map<String, Object>> buildAccessTrend() {
        LocalDate startDate = LocalDate.now().minusDays(TREND_DAYS - 1L);
        String startDay = startDate.format(DATE_FORMATTER);

        Map<String, Integer> accessMap = toDayCountMap(dashboardStatsMapper.countAccessByDay(startDay));
        Map<String, Integer> activeMap = toDayCountMap(dashboardStatsMapper.countActiveUsersByDay(startDay));

        List<Map<String, Object>> trend = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < TREND_DAYS; i++) {
            LocalDate day = startDate.plusDays(i);
            String dayKey = day.format(DATE_FORMATTER);
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("date", dayKey);
            item.put("accessCount", accessMap.containsKey(dayKey) ? accessMap.get(dayKey) : 0);
            item.put("activeUserCount", activeMap.containsKey(dayKey) ? activeMap.get(dayKey) : 0);
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> buildFeatureUsage() {
        Map<String, Integer> counter = new LinkedHashMap<String, Integer>();
        counter.put("邮件服务", 0);
        counter.put("软考扫描", 0);
        counter.put("系统管理", 0);
        counter.put("认证登录", 0);
        counter.put("其他", 0);

        for (NameCount row : dashboardStatsMapper.countFeatureByUri()) {
            String uri = row.getUri();
            int count = intValue(row.getCount());
            String module = mapModule(uri);
            counter.put(module, counter.get(module) + count);
        }

        int mailLogCount = intValue(dashboardStatsMapper.countMailSendLog());
        int ruankaoLogCount = intValue(dashboardStatsMapper.countRuankaoScanLog());
        if (mailLogCount > 0) {
            counter.put("邮件服务", counter.get("邮件服务") + mailLogCount);
        }
        if (ruankaoLogCount > 0) {
            counter.put("软考扫描", counter.get("软考扫描") + ruankaoLogCount);
        }

        List<Map<String, Object>> usage = new ArrayList<Map<String, Object>>();
        for (Map.Entry<String, Integer> entry : counter.entrySet()) {
            if (entry.getValue() <= 0) {
                continue;
            }
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            usage.add(item);
        }
        return usage;
    }

    private List<Map<String, Object>> buildApiRanking() {
        List<Map<String, Object>> ranking = new ArrayList<Map<String, Object>>();
        for (NameCount row : dashboardStatsMapper.countApiRanking(API_RANK_LIMIT)) {
            String uri = row.getUri();
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("uri", uri);
            item.put("label", resolveUriLabel(uri));
            item.put("count", intValue(row.getCount()));
            ranking.add(item);
        }
        return ranking;
    }

    private List<Map<String, Object>> buildStatusDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<Map<String, Object>>();
        addStatusItem(distribution, "成功(2xx/3xx)", dashboardStatsMapper.countApiSuccess());
        addStatusItem(distribution, "客户端错误(4xx)", dashboardStatsMapper.countApiClientError());
        addStatusItem(distribution, "服务端错误(5xx)", dashboardStatsMapper.countApiServerError());
        addStatusItem(distribution, "未知", dashboardStatsMapper.countApiUnknown());
        return distribution;
    }

    private void addStatusItem(List<Map<String, Object>> list, String name, Integer value) {
        int count = intValue(value);
        if (count <= 0) {
            return;
        }
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("name", name);
        item.put("value", count);
        list.add(item);
    }

    private String mapModule(String uri) {
        if (uri.startsWith("/api/mail")) {
            return "邮件服务";
        }
        if (uri.startsWith("/api/ruankao")) {
            return "软考扫描";
        }
        if (uri.startsWith("/api/system")) {
            return "系统管理";
        }
        if (uri.startsWith("/api/auth")) {
            return "认证登录";
        }
        return "其他";
    }

    private String resolveUriLabel(String uri) {
        if (URI_LABELS.containsKey(uri)) {
            return URI_LABELS.get(uri);
        }
        if (uri.matches("/api/system/users/\\d+")) {
            return "用户详情";
        }
        if (uri.matches("/api/system/roles/\\d+")) {
            return "角色详情";
        }
        return uri;
    }

    private Map<String, Integer> toDayCountMap(List<DayCount> rows) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (DayCount row : rows) {
            map.put(row.getDayKey(), intValue(row.getCnt()));
        }
        return map;
    }

    private int intValue(Integer value) {
        return value == null ? 0 : value;
    }
}
