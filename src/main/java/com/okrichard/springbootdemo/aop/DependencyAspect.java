package com.okrichard.springbootdemo.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhuhaotian
 * @Date 2024/1/20
 */

@Aspect
@Component
public class DependencyAspect {

    private static final ThreadLocal<List<String>> DEPENDENCY_LIST = new ThreadLocal<>();

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void service() {
    }

    @Before("service()")
    public void log(JoinPoint joinPoint) {
        List<String> dependencyList = DEPENDENCY_LIST.get();
        if (dependencyList == null) {
            dependencyList = new ArrayList<>();
            DEPENDENCY_LIST.set(dependencyList);
        }

        dependencyList.add(joinPoint.getSignature().toShortString());
    }

    public static List<String> getDependencyList() {
        return DEPENDENCY_LIST.get();
    }

    public static void clear() {
        DEPENDENCY_LIST.remove();
    }
}
