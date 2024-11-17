package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountStatus;
import com.example.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
// 꼭 필요한 아규먼트가 들어간 생성자를 만드는 것
public class AccountService {
    private final AccountRepository accountRepository;
    // final 타입을 생성자에 추가할 수 있다.

    @Transactional
    public void createAccount() {
        // builder는 빌더 패턴을 사용하여 객체를 생성하는 방법
        Account account = Account.builder().accountNumber("4000")
                .accountStatus(AccountStatus.IN_USE)
                .build();
        accountRepository.save(account);
    }

    @Transactional
    public Account getAccount(long id) {
        return accountRepository.findById(id).get();
    }


}
