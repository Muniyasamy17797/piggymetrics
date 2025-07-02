package com.piggymetrics.account.service;

import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.dto.AccountDTO;
import com.piggymetrics.account.dto.AccountCreateDTO;
import com.piggymetrics.account.dto.TransactionDTO;
import com.piggymetrics.account.exception.AccountAlreadyExistsException;
import com.piggymetrics.account.exception.AccountNotFoundException;
import com.piggymetrics.account.exception.InsufficientBalanceException;
import com.piggymetrics.account.exception.ValidationException;
import com.piggymetrics.account.mapper.AccountMapper;
import com.piggymetrics.account.mapper.TransactionMapper;
import com.piggymetrics.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;
    
    private static final int INACTIVE_DAYS_THRESHOLD = 30;

    @Override
    @Transactional(readOnly = true)
    public AccountDTO findByName(@NotBlank String name) {
        log.debug("Finding account by name: {}", name);
        return accountRepository.findByNameWithTransactions(name)
                .map(accountMapper::toDto)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + name));
    }

    @Override
    @Transactional
    public AccountDTO create(@Valid AccountCreateDTO createDTO) {
        log.debug("Creating new account with name: {}", createDTO.getName());
        
        if (accountRepository.existsByName(createDTO.getName())) {
            throw new AccountAlreadyExistsException("Account already exists: " + createDTO.getName());
        }

        Account account = accountMapper.toEntity(createDTO);
        
        // Process initial transactions if any
        if (createDTO.getInitialTransactions() != null && !createDTO.getInitialTransactions().isEmpty()) {
            createDTO.getInitialTransactions().forEach(txn -> {
                account.getTransactions().add(transactionMapper.toEntity(txn));
            });
        }

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional
    public AccountDTO saveChanges(@NotBlank String name, @Valid AccountDTO accountDTO) {
        log.debug("Updating account: {}", name);
        
        Account account = accountRepository.findByName(name)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + name));

        if (!name.equals(accountDTO.getName())) {
            throw new ValidationException("Account name cannot be changed");
        }

        accountMapper.updateEntityFromDto(accountDTO, account);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional
    public void deleteAccount(@NotBlank String name) {
        log.debug("Deleting account: {}", name);
        
        Account account = accountRepository.findByName(name)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + name));
        
        accountRepository.delete(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> findInactiveAccounts() {
        log.debug("Finding inactive accounts");
        
        LocalDateTime threshold = LocalDateTime.now().minus(INACTIVE_DAYS_THRESHOLD, ChronoUnit.DAYS);
        return accountRepository.findInactiveAccounts(threshold).stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AccountDTO addTransaction(@NotBlank String accountName, @Valid TransactionDTO transaction) {
        log.debug("Adding transaction to account: {}", accountName);
        
        Account account = accountRepository.findByNameWithTransactions(accountName)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountName));

        // Validate transaction amount
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0 &&
            account.getBalance().add(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for transaction");
        }

        account.setBalance(account.getBalance().add(transaction.getAmount()));
        account.getTransactions().add(transactionMapper.toEntity(transaction));
        
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountDTO> findAccountsByBalanceRange(
            @NotNull BigDecimal minBalance,
            @NotNull BigDecimal maxBalance,
            @NotNull Pageable pageable) {
        log.debug("Finding accounts with balance between {} and {}", minBalance, maxBalance);
        return accountRepository.findAccountsByBalanceRange(minBalance, maxBalance, pageable)
                .map(accountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> findAccountsCreatedBetween(
            @NotNull LocalDateTime startDate,
            @NotNull LocalDateTime endDate) {
        log.debug("Finding accounts created between {} and {}", startDate, endDate);
        return accountRepository.findAccountsCreatedBetween(startDate, endDate).stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getAccountsAboveBalanceCount(@NotNull BigDecimal threshold) {
        log.debug("Counting accounts with balance above {}", threshold);
        return accountRepository.countAccountsAboveBalance(threshold);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> findStaleAccounts(@NotNull LocalDateTime lastUpdateTime) {
        log.debug("Finding stale accounts not updated since {}", lastUpdateTime);
        return accountRepository.findStaleAccounts(lastUpdateTime).stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateLastSeen(@NotBlank String name) {
        log.debug("Updating last seen for account: {}", name);
        
        Account account = accountRepository.findByName(name)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + name));
        
        account.setLastSeen(LocalDateTime.now());
        accountRepository.save(account);
    }
}