package com.piggymetrics.account.service;

import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.Transaction;
import com.piggymetrics.account.domain.User;
import com.piggymetrics.account.dto.AccountDTO;
import com.piggymetrics.account.dto.TransactionDTO;
import com.piggymetrics.account.mapper.AccountMapper;
import com.piggymetrics.account.mapper.TransactionMapper;
import com.piggymetrics.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionMapper transactionMapper;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl(accountRepository, accountMapper, transactionMapper);
    }

    @Test
    void findByName_WhenAccountExists_ReturnsAccountDTO() {
        // Arrange
        String name = "testUser";
        Account account = new Account();
        account.setName(name);
        AccountDTO expectedDTO = new AccountDTO();
        expectedDTO.setName(name);

        when(accountRepository.findByName(name)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(expectedDTO);

        // Act
        AccountDTO result = accountService.findByName(name);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(accountRepository).findByName(name);
        verify(accountMapper).toDto(account);
    }

    @Test
    void findByName_WhenAccountDoesNotExist_ThrowsException() {
        // Arrange
        String name = "nonExistentUser";
        when(accountRepository.findByName(name)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> accountService.findByName(name));
        verify(accountRepository).findByName(name);
    }

    @Test
    void create_WhenAccountDoesNotExist_CreatesNewAccount() {
        // Arrange
        User user = new User();
        user.setUsername("newUser");
        Account account = new Account();
        account.setName(user.getUsername());
        AccountDTO expectedDTO = new AccountDTO();
        expectedDTO.setName(user.getUsername());

        when(accountRepository.existsByName(user.getUsername())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(expectedDTO);

        // Act
        AccountDTO result = accountService.create(user);

        // Assert
        assertNotNull(result);
        assertEquals(user.getUsername(), result.getName());
        verify(accountRepository).existsByName(user.getUsername());
        verify(accountRepository).save(any(Account.class));
        verify(accountMapper).toDto(account);
    }

    @Test
    void create_WhenAccountExists_ThrowsValidationException() {
        // Arrange
        User user = new User();
        user.setUsername("existingUser");
        when(accountRepository.existsByName(user.getUsername())).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> accountService.create(user));
        verify(accountRepository).existsByName(user.getUsername());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void findInactiveAccounts_ReturnsInactiveAccounts() {
        // Arrange
        Account account1 = new Account();
        account1.setName("inactive1");
        Account account2 = new Account();
        account2.setName("inactive2");
        List<Account> inactiveAccounts = Arrays.asList(account1, account2);

        AccountDTO dto1 = new AccountDTO();
        dto1.setName("inactive1");
        AccountDTO dto2 = new AccountDTO();
        dto2.setName("inactive2");

        when(accountRepository.findInactiveAccounts(any(LocalDateTime.class)))
                .thenReturn(inactiveAccounts);
        when(accountMapper.toDto(account1)).thenReturn(dto1);
        when(accountMapper.toDto(account2)).thenReturn(dto2);

        // Act
        List<AccountDTO> result = accountService.findInactiveAccounts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(accountRepository).findInactiveAccounts(any(LocalDateTime.class));
        verify(accountMapper, times(2)).toDto(any(Account.class));
    }

    @Test
    void addTransaction_UpdatesBalanceCorrectly() {
        // Arrange
        String accountName = "testUser";
        Account account = new Account();
        account.setName(accountName);
        account.setBalance(new BigDecimal("100.00"));

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(new BigDecimal("50.00"));
        transactionDTO.setType(Transaction.TransactionType.CREDIT);

        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("50.00"));
        transaction.setType(Transaction.TransactionType.CREDIT);

        AccountDTO expectedDTO = new AccountDTO();
        expectedDTO.setBalance(new BigDecimal("150.00"));

        when(accountRepository.findByName(accountName)).thenReturn(Optional.of(account));
        when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(expectedDTO);

        // Act
        AccountDTO result = accountService.addTransaction(accountName, transactionDTO);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("150.00"), result.getBalance());
        verify(accountRepository).findByName(accountName);
        verify(accountRepository).save(account);
        verify(accountMapper).toDto(account);
    }
}