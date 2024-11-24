package com.example.account.service;

import com.example.account.dto.UseBalance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {
    private final LockService lockService;

    // 어떤 경우에 이 aspect를 적용할 것인가 정의
    // args(request) : AccountLock 어노테이션을 붙인 컨트롤러에서
    // 파라미터를 가져오겠다는 뜻
    @Around("@annotation(com.example.account.aop.AccountLock) && args(request)")
    public Object aroundMethod(
            ProceedingJoinPoint pjp,
            UseBalance.Request request
    ) throws Throwable {
        // lock 취득 시도
        lockService.lock(request.getAccountNumber());
        try {
            return pjp.proceed();
        } finally {
            // 성공하든 실패하든 lock 해제
            lockService.unlock(request.getAccountNumber());
        }
    }
}
