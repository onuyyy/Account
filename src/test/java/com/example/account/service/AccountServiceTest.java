package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.repository.AccountRepository;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    // 가짜 객체를 생성
    // AccountRepository는 DB에 접근하는 클래스이기 때문에
    // 실제 DB에 접근하지 않고 가짜 객로 대체
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    // accouctMocksntRepository를 accountservice에 inject해준다
    @InjectMocks
    private AccountService accountService;
    // 2개의 Mock 달려있는 service가 생성된 것

    @Test
    void failedToGetAccounts() {
        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L,"1234567890"));

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void successGetAccountsByUserId() {
        // given
        AccountUser pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi")
                .build();
        List<Account> accounts = Arrays.asList(
                Account.builder()
                        .accountNumber("1111111111")
                        .accountUser(pobi)
                        .balance(1000L)
                        .build(),
                Account.builder()
                        .accountNumber("2222222222")
                        .accountUser(pobi)
                        .balance(2000L)
                        .build(),
                Account.builder()
                        .accountNumber("3333333333")
                        .accountUser(pobi)
                        .balance(3000L)
                        .build()
        );

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));
        given(accountRepository.findByAccountUser(any()))
                .willReturn(accounts);
        // when
        List<AccountDto> accountDtos = accountService.getAccountsByUserId(1L);

        // then
        assertEquals(3, accountDtos.size());
        assertEquals("1111111111",accountDtos.get(0).getAccountNumber());
        assertEquals(1000L,accountDtos.get(0).getBalance());
        assertEquals("2222222222",accountDtos.get(1).getAccountNumber());
        assertEquals(2000L,accountDtos.get(1).getBalance());
        assertEquals("3333333333",accountDtos.get(2).getAccountNumber());
        assertEquals(3000L,accountDtos.get(2).getBalance());
    }

    @Test
    @DisplayName("해지 계좌는 해지할 수 없다.")
    void deleteAccountFailed_AlreadyUnregistered() {
        // given
        AccountUser pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(pobi)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .balance(100L)
                        .accountNumber("1000000012").build()));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L,"1234567890"));

        // then
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
    }

    @Test
    @DisplayName("해지 계좌는 잔액이 없어야 한다.")
    void deleteAccountFailed_balanceNotEmpty() {
        // given
        AccountUser pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(pobi)
                        .balance(100L)
                        .accountNumber("1000000012").build()));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L,"1234567890"));

        // then
        assertEquals(ErrorCode.BALANCE_NOT_EMPTY, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 소유주 다름")
    void deleteAccountFailed_userMatch() {
        // given
        AccountUser pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi")
                .build();

        AccountUser harry = AccountUser.builder()
                .id(13L)
                .name("Harry")
                .build();


        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(harry)
                        .balance(0L)
                        .accountNumber("1000000012").build()));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L,"1234567890"));

        // then
        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH, exception.getErrorCode());
    }


    @Test
    @DisplayName("해당 계좌 없음 - 계좌 해지 실패")
    void deleteAccount_AccountNotFound() {
        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L,"1234567890"));
        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 해지 실패")
    void deleteAccount_UserNotFound() {
        // given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L,"1234567890"));

        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void deleteAccountSuccess() {
        // given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                                .accountUser(user)
                                .balance(0L)
                                .accountNumber("1000000012").build()));
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        // when
        AccountDto accountDto =
                accountService.deleteAccount(1L, "1234567890");

        // then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("1000000012", captor.getValue().getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());
    }


    @Test
    @DisplayName("유저 당 최대 계좌는 10개")
    void createAccount_maxAccountIs10() {
        // given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(15L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.countByAccountUser(any()))
                .willReturn(10);

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L,1000L));

        // then
        assertEquals(ErrorCode.USER_ACCOUNT_PER_USER_10, exception.getErrorCode());
    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 생성 실패")
    void createAccount_UserNotFound() {
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //then
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L,1000L));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createFirstAccount() {
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(15L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000015").build());
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(15L, accountDto.getUserId());
        assertEquals("1000000000", captor.getValue().getAccountNumber());
    }


    @Test
    void createAccountSuccess() {
        // given
        AccountUser user = AccountUser.builder()
                .id(1L)
                .name("Pobi")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1000000000").build()));
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000000").build());

        // when
        AccountDto accountDto =
                accountService.createAccount(1L, 1000L);

        // then
        assertEquals(2, accountDto.getUserId());
        assertEquals("1000000001", accountDto.getAccountNumber());
    }


    /**
     * 아래부터는 연습 test
     */
    @Test
    @DisplayName("계좌 조회 실패")
    void testFail() {

        // when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.getAccount(-10l));

        assertEquals("id is negative", exception.getMessage());

    }

    @Test
    @DisplayName("계좌 조회 성공")
    void testxx() {
        // given
        //
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("654")
                        .build()));

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);


        // when
        Account account = accountService.getAccount(2342l);

        // then
        verify(accountRepository, times(1)).findById(captor.capture());
        verify(accountRepository, times(0)).save(any());
        assertEquals(2342l, captor.getValue());

        assertEquals("654", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    @DisplayName("test name changed")
    void testSomething() {
        Account account = accountService.getAccount(1L);

        assertEquals("4000",account.getAccountNumber());
        assertEquals(AccountStatus.IN_USE, account.getAccountStatus());
    }

    @Test
    void testSomething2() {
        // given
        String something = "something";
        // when

        // then
        assertEquals("something", something);
    }
}
