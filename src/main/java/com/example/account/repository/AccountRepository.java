package com.example.account.repository;

import com.example.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // account 라는 테이블에 접근하기 위한 interface
    // JpaRepository를 상속하면, 엔티티에 대해 기본적인 crud를 사용 할 수 있다.

}
