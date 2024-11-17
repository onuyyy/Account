package com.example.account.domain;

import jakarta.persistence.*;
import lombok.*;

// Entity : 일종의 설정 클래스
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue
    long id;
    // @Id > Account pk : id로 지정한다는 의미
    // @GeneratedValue : 기본 키 값의 자동 생성 설정

    private String accountNumber;

    // accountStatus 필드는 AccountStatus 라는 Enum 타입인데,
    // DB에 저장될 때, Enum의 이름이 문자열로 저장된다는 뜻
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    // JPA는 DB와 매핑을 위해 테이블 간 변환을 자동으로 처리하는데,
    // Enum은 표준 데이터 타입이 아니기 때문에 명확하게 해줌
}
