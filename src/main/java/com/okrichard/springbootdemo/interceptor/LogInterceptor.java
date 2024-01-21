package com.okrichard.springbootdemo.interceptor;

import com.okrichard.springbootdemo.aop.DependencyAspect;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

/**
 * @Author zhuhaotian
 * @Date 2024/1/20
 */
@Component
public class LogInterceptor implements HandlerInterceptor {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LogInterceptor.class);

    // This method is called before the controller.
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Set start time in request.
        request.setAttribute("startTime", System.currentTimeMillis());
        // Set an operation ID in request.
        request.setAttribute("operationId", UUID.randomUUID());

        logger.info(
                "Request Log, operation ID: {}, start time: {}, parameters: {}, full url: {}",
                request.getAttribute("operationId"),
                request.getAttribute("startTime"),
                request.getQueryString(),
                request.getRequestURL());
        return true;
    }

    // This method is called after the controller.
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        // Calculate latency.
        long endTime = System.currentTimeMillis();
        long latency = endTime - (long) request.getAttribute("startTime");

        logger.info(
                "Response Log, operation ID: {}, api name: {}, end time: {}, latency: {}ms, status code: {}",
                request.getAttribute("operationId"),
                request.getAttribute("apiName"),
                endTime,
                latency,
                response.getStatus());

        List<String> dependencyList = DependencyAspect.getDependencyList();
        if (dependencyList != null) {
            logger.info(
                    "Dependencies log, operation ID: {}, dependencies: {}",
                    request.getAttribute("operationId"),
                    String.join("->", dependencyList));
        } else {
            logger.info(
                    "Dependencies log, operation ID: {}, dependencies: {}",
                    request.getAttribute("operationId"),
                    "No dependencies");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        // Clear the thread local.
        DependencyAspect.clear();
    }
}
