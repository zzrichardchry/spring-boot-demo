package com.okrichard.springbootdemo.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * @Author zhuhaotian
 * @Date 2024/1/20
 */

@Aspect
@Component
public class LogAspect {

    // AOP expression for which methods shall be intercepted.
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {
    }

    // AOP expression for which methods shall be intercepted.
    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controller() {
    }

    // AOP expression for which methods shall be intercepted.
    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    // This method will be called when a public method in classes annotated with @Controller or @RestController.
    @Before("publicMethod() && (restController() || controller())")
    public void log(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        // Find out the Mapping annotation on the method.
        Optional<List<String[]>> methodPaths = Optional.ofNullable(getMappingPaths(method));
        
        // Find out the Mapping annotation on the class.
        Optional<RequestMapping> classAnnotation = Optional.ofNullable(method.getDeclaringClass().getAnnotation(RequestMapping.class));
        Optional<String[]> classPaths = classAnnotation.map(RequestMapping::value);

        // Get the HttpRequest.
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // Concat the real path.
        String path = concatPaths(
                classPaths.orElse(new String[] { "" }),
                methodPaths.orElse(List.of(new String[] { "" }, new String[] { "" }))
        );

        // Set the path in request.
        request.setAttribute("apiName", path);
    }

    private List<String[]> getMappingPaths(Method method) {
        // Only consider @GetMapping for now, the other mappings can be added according to your needs.
        if (method.isAnnotationPresent(GetMapping.class)) {
            return List.of(method.getAnnotation(GetMapping.class).value(), new String[] { "GET" });
        }

        // Doesn't have any mapping annotation.
        return null;
    }

    private String concatPaths(String[] classPaths, List<String[]> methodPaths) {
        return String.format(
                "%s %s%s",
                methodPaths.get(1)[0],
                classPaths[0],
                methodPaths.get(0)[0]);
    }
}
