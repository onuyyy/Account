package com.example.account.service;

import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.example.account.type.ErrorCode.ACCOUNT_TRANSACTION_LOCK;

@Service
@Slf4j
@RequiredArgsConstructor
public class LockService {
    // 자동 생성자 주입 : RequiredArgsConstructor
    private final RedissonClient redissonClient;

    public void lock(String accountNumber) {
        // lock에 쓸 키 지정
        RLock lock = redissonClient.getLock(getLockKey(accountNumber));
        log.debug("Trying lock for accountNumber : {}", accountNumber);

        try {
            boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS);
            // 스핀락 시도
            // 1초동안 기다리면서 락 획득 시도
            // 락 해제를 위해 유지할 시간 5초 > 다른 프로세스가 해당 락 사용 금지

            if (!isLock) {
                // lock 획득에 실패
                log.error("==================Lock failed==================");
                throw new AccountException(ACCOUNT_TRANSACTION_LOCK);
            }

        } catch(AccountException e) {
            throw e;
        } catch (Exception e) {
            log.error("Redis lock failed", e);
        }
    }

    public void unlock(String accountNumber) {
        log.debug("Trying unlock for accountNumber : {}", accountNumber);
        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }

    private static String getLockKey(String accountNumber) {
        return "ACLK : " + accountNumber;
    }
}
