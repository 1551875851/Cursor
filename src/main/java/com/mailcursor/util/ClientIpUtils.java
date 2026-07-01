package com.mailcursor.util;

import javax.servlet.http.HttpServletRequest;

public final class ClientIpUtils {

    private ClientIpUtils() {
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isUsable(ip)) {
            int commaIndex = ip.indexOf(',');
            return commaIndex > 0 ? ip.substring(0, commaIndex).trim() : ip.trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (isUsable(ip)) {
            return ip.trim();
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (isUsable(ip)) {
            return ip.trim();
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isUsable(ip)) {
            return ip.trim();
        }

        return request.getRemoteAddr();
    }

    private static boolean isUsable(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }
}
