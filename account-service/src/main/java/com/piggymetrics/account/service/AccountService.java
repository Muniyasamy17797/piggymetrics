package com.piggymetrics.account.service;

import com.piggymetrics.account.dto.AccountDTO;
import com.piggymetrics.account.dto.AccountCreateDTO;
import com.piggymetrics.account.dto.TransactionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Validated
public interface AccountService {
    
    AccountDTO findByName(@NotBlank String name);
    
    AccountDTO create(@Valid AccountCreateDTO createDTO);
    
    AccountDTO saveChanges(@NotBlank String name, @Valid AccountDTO accountDTO);
    
    void deleteAccount(@NotBlank String name);
    
    List<AccountDTO> findInactiveAccounts();
    
    AccountDTO addTransaction(@NotBlank String accountName, @Valid TransactionDTO transaction);
    
    Page<AccountDTO> findAccountsByBalanceRange(
        @NotNull BigDecimal minBalance,
        @NotNull BigDecimal maxBalance,
        @NotNull Pageable pageable
    );
    
    List<AccountDTO> findAccountsCreatedBetween(
        @NotNull LocalDateTime startDate,
        @NotNull LocalDateTime endDate
    );
    
    long getAccountsAboveBalanceCount(@NotNull BigDecimal threshold);
    
    List<AccountDTO> findStaleAccounts(@NotNull LocalDateTime lastUpdateTime);
    
    void updateLastSeen(@NotBlank String name);
}