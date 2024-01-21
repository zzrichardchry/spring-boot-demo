package com.okrichard.springbootdemo.exception;

import com.okrichard.springbootdemo.aop.DependencyAspect;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

/**
 * @Author zhuhaotian
 * @Date 2024/1/20
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public String handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        long endTime = System.currentTimeMillis();
        long latency = endTime - (long) request.getAttribute("startTime");

        logger.info(
                "Response Log, operation ID: {}, end time: {}, latency: {}ms, status code: {}",
                request.getAttribute("operationId"),
                endTime,
                latency,
                HttpStatusCode.valueOf(400));

        List<String> dependencyList = DependencyAspect.getDependencyList();
        if (dependencyList != null) {
            logger.info(
                    "Dependencies log, operation ID: {}, dependencies: {}",
                    request.getAttribute("operationId"),
                    String.format("%s->%s",
                            String.join("->", dependencyList),
                            e.getClass().getSimpleName()));
        } else {
            logger.info(
                    "Dependencies log, operation ID: {}, dependencies: {}",
                    request.getAttribute("operationId"),
                    String.format("%s->%s",
                            "No dependencies",
                            e.getClass().getSimpleName()));
        }

        DependencyAspect.clear();
        return "error";
    }
}
