package com.example.account.dto;

import com.example.account.domain.Account;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    // Entity 클래스와 유사하지만 필요한 부분만 넣어두는 형태로 만든다

    // accountController와 accountService 간에 데이터 송수신에 최적화된 dto
    private Long userId;
    private String accountNumber;
    private Long balance;
    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;
    
    public static AccountDto fromEntity(Account account) {
        // 특정 Entity에서 Dto로 변환해줄 때 쓰는 방법
        return AccountDto.builder()
                .userId(account.getId())
                .accountNumber(account.getAccountNumber())
                .registeredAt(account.getRegisteredAt())
                .balance(account.getBalance())
                .unRegisteredAt(account.getUnRegisteredAt())
                .build();
    }
}
