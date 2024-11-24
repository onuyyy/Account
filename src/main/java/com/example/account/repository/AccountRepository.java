package com.example.account.repository;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // account 라는 테이블에 접근하기 위한 interface
    // JpaRepository를 상속하면, 엔티티에 대해 기본적인 crud를 사용 할 수 있다.

    // Optional은 값이 있을 수도 없을 수도 있다는 것을 나타냄
    // null을 더 안전하고 명확하게 처리 가능
    Optional<Account> findFirstByOrderByIdDesc();
    // 메서드 이름을 기반으로 자동으로 쿼리를 생성해준다!!! > 쿼리 메서드

    Integer countByAccountUser(AccountUser accountUser);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByAccountUser(AccountUser accountUser);

}
