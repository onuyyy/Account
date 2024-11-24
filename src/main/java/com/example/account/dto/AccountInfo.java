package com.example.account.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfo {
    // client와 application 간에 송수신에 최적화된 dto
    // AccountDto와 유사한데 거기서 쓰면 되지 않나?
    // > 각각의 목적마다 Dto를 만드는 것이 좋음

    private String accountNumber;
    private long balance;

}
