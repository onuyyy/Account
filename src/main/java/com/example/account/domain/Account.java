package com.example.account.domain;

import com.example.account.exception.AccountException;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static com.example.account.type.ErrorCode.AMOUNT_EXCEED_BALANCE;
import static com.example.account.type.ErrorCode.INVALID_REQUEST;

// Entity : 일종의 설정 클래스
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
// EntityListeners는  jpa에서 엔티티의 라이프사이클 이벤트 감지
// 자동으로 정보 처리하는 어노테이션
// 어플리케이션 config에 넣어줘야 사용 가능
public class Account {

    @Id
    @GeneratedValue
    long id;
    // @Id > Account pk : id로 지정한다는 의미
    // @GeneratedValue : 기본 키 값의 자동 생성 설정

    // n : 1 관계
    // jpa에서 엔티티 간의 관계를 매핑할 때 사용
    @ManyToOne
    private AccountUser accountUser;
    private String accountNumber;

    // accountStatus 필드는 AccountStatus 라는 Enum 타입인데,
    // DB에 저장될 때, Enum의 이름이 문자열로 저장된다는 뜻
    // JPA는 DB와 매핑을 위해 테이블 간 변환을 자동으로 처리하는데,
    // Enum은 표준 데이터 타입이 아니기 때문에 명확하게 해줌
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

    // jpa에서 처리하기 번거로운 부분이라
    // 자동으로 저장해주는 기능을 제공한다
    // @EntityListeners(AuditingEntityListener.class) 얘랑 한 쌍이다
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 중요한 데이터를 변경하는 로직은 Entity 안에 있는 것이 좋다.
    public void useBalance(Long amount) {
        if (amount > balance) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        }
        balance -= amount;
    }

    public void cancelBalance(long amount) {
        if (amount < 0) {
            throw new AccountException(INVALID_REQUEST);
        }

        balance += amount;
    }

}
