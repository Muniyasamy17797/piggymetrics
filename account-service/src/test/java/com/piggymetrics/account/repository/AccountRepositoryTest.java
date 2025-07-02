package com.piggymetrics.account.repository;

import com.piggymetrics.account.domain.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void shouldSaveAndFindAccountByName() {
        // given
        Account account = new Account();
        account.setName("test");
        account.setLastSeen(LocalDateTime.now());
        account.setNote("test note");
        account.setBalance(new BigDecimal("100.00"));

        // when
        Account savedAccount = accountRepository.save(account);
        Optional<Account> found = accountRepository.findByName("test");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(savedAccount.getId());
        assertThat(found.get().getName()).isEqualTo("test");
        assertThat(found.get().getBalance()).isEqualByComparingTo(new BigDecimal("100.00"));
    }
}