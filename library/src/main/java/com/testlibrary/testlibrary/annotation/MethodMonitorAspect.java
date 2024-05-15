package com.testlibrary.testlibrary.annotation;

import com.testlibrary.testlibrary.monitoring.MonitorMethodModel;
import com.testlibrary.testlibrary.service.RabbitMQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class MethodMonitorAspect {

    private final RabbitMQService rabbitMQService;

    @Around("@annotation(monitorMethod)")
    public Object monitorMethodExecution(ProceedingJoinPoint joinPoint, MonitorMethod monitorMethod) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed();

        stopWatch.stop();

        MonitorMethodModel monitorMethodModel = MonitorMethodModel.builder()
                .method(joinPoint.getSignature().getName() + " : " + joinPoint.getTarget().getClass().toString())
                .timestamp(stopWatch.getTotalTimeMillis())
                .user(SecurityContextHolder.getContext().getAuthentication().getName())
                .datetime(LocalDateTime.now())
                .build();

        rabbitMQService.sendMessageToRabbitMQ(monitorMethodModel);

        return result;
    }
}

