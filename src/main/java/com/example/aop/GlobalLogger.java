package com.example.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class GlobalLogger {

    @Before("execution(* com.example.api.*.*(..))")
    void logEndpointHit(JoinPoint joinPoint) {
        log.info("{} called with args: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterReturning(value = "execution(* com.example.api.*.*(..))", returning = "result")
    void logEndpointReturn(JoinPoint joinPoint, Object result) {
        log.info("{} exits returning: {}", joinPoint.getSignature().getName(), result);
    }
}