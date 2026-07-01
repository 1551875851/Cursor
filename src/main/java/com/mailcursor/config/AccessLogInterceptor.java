package com.mailcursor.config;

import com.mailcursor.util.ClientIpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AccessLogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AccessLogInterceptor.class);
    private static final String START_TIME_ATTR = "accessLogStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        log.info("客户端访问，ip={}，method={}，uri={}",
                ClientIpUtils.getClientIp(request),
                request.getMethod(),
                request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        long costMs = startTime == null ? -1 : System.currentTimeMillis() - startTime;
        if (ex != null) {
            log.warn("请求异常，ip={}，method={}，uri={}，status={}，costMs={}，error={}",
                    ClientIpUtils.getClientIp(request),
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    costMs,
                    ex.getMessage());
            return;
        }
        log.info("请求完成，ip={}，method={}，uri={}，status={}，costMs={}",
                ClientIpUtils.getClientIp(request),
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                costMs);
    }
}
