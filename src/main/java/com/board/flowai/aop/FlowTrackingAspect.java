package com.board.flowai.aop;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.*;

@Getter
@Aspect
@Component
@Slf4j
public class FlowTrackingAspect {
    private final List<Map<String, String>> flowList = new ArrayList<>();
    private final Map<String, String> previousCallerMap = new HashMap<>();

    @Around("execution(* com.board.flowai..controller..*(..)) || execution(* com.board.flowai..service..*(..)) || execution(* com.board.flowai..repository..*(..))")
    public Object trackExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullName = className + "." + methodName;

        // `ApiScannerService` 및 `AnalysisController` 관련 API는 추적하지 않음
        if (className.contains("ApiScannerService") || className.contains("AnalysisController")) {
            return joinPoint.proceed();
        }


        log.info("실행됨: {}", fullName);

        // 컨트롤러에서 실행된 경우 Auto_Discovered_API에 연결
        String previousCaller = previousCallerMap.getOrDefault(Thread.currentThread().getName(), "Auto_Discovered_API");

        Object result;
        try {
            result = joinPoint.proceed();
        } finally {
            if (!fullName.equals("UnknownClass.UnknownMethod")) {
                // 컨트롤러 → 서비스 연결
                if (className.endsWith("Controller")) {
                    flowList.add(Map.of("caller", "Auto_Discovered_API", "callee", fullName));
                } else if (className.endsWith("ServiceImpl")) {
                    // 서비스 → 레포지토리 연결
                    flowList.add(Map.of("caller", previousCaller, "callee", className));
                }

                flowList.add(Map.of("caller", className, "callee", fullName));
                previousCallerMap.put(Thread.currentThread().getName(), fullName);
            }
        }
        return result;
    }
}
