package com.example.account.controller;

import com.example.account.aop.AccountLock;
import com.example.account.dto.CancelBalance;
import com.example.account.dto.QueryTransactionResponse;
import com.example.account.dto.TransactionDto;
import com.example.account.dto.UseBalance;
import com.example.account.exception.AccountException;
import com.example.account.service.TransactionService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 잔액 관련 컨트롤러
 * 1. 잔액 사용
 * 2. 잔액 사용 취소
 * 3. 거래 확인
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @AccountLock
    @PostMapping("/transaction/use")
    public UseBalance.Response useBalance(
            // @Valid는 request 객체의 필드의 검증 조건을 자동으로 확인(notnull, min 등)
            @Valid @RequestBody UseBalance.Request request
    ) throws InterruptedException {
        try {
            Thread.sleep(5000L);
            return UseBalance.Response.from(
                    transactionService.useBalance(request.getUserId(),
                            request.getAccountNumber(), request.getAmount())
            );
        } catch (AccountException e) {
            // S 아닌 F일 때 저장
            log.error("Failed to use balance." +e.getMessage());

            transactionService.saveFailedUserTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }

    @AccountLock
    @PostMapping("/transaction/cancel")
    public CancelBalance.Response cancelBalance(
            // @Valid는 request 객체의 필드의 검증 조건을 자동으로 확인(notnull, min 등)
            @Valid @RequestBody CancelBalance.Request request
    ) {
        try {
            return CancelBalance.Response.from(
                    transactionService.cancelBalance(request.getTransactionId(),
                            request.getAccountNumber(), request.getAmount())
            );
        } catch (AccountException e) {
            // S 아닌 F일 때 저장
            log.error("Failed to cancel balance." +e.getMessage());

            transactionService.saveFailedCancelTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }

    @GetMapping("/transaction/{transactionId}")
    public QueryTransactionResponse queryTransaction(
            @PathVariable("transactionId") String transactionId
    ) {
        return QueryTransactionResponse.from(
                transactionService.queryTransaction(transactionId));
    }
}
