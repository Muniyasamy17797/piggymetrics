package com.piggymetrics.account.repository;

import com.piggymetrics.account.domain.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByName(String name);
    
    @Query("SELECT a FROM Account a WHERE a.balance > :minBalance")
    List<Account> findAccountsWithBalanceGreaterThan(@Param("minBalance") BigDecimal minBalance);
    
    @Query("SELECT a FROM Account a WHERE a.lastSeen < :date")
    List<Account> findInactiveAccounts(@Param("date") LocalDateTime date);
    
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.transactions WHERE a.id = :id")
    Optional<Account> findByIdWithTransactions(@Param("id") Long id);
    
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.transactions WHERE a.name = :name")
    Optional<Account> findByNameWithTransactions(@Param("name") String name);
    
    @Query("SELECT a FROM Account a WHERE a.balance BETWEEN :minBalance AND :maxBalance")
    Page<Account> findAccountsByBalanceRange(
        @Param("minBalance") BigDecimal minBalance,
        @Param("maxBalance") BigDecimal maxBalance,
        Pageable pageable
    );
    
    @Query("SELECT a FROM Account a WHERE a.createdAt >= :startDate AND a.createdAt <= :endDate")
    List<Account> findAccountsCreatedBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.balance > :threshold")
    long countAccountsAboveBalance(@Param("threshold") BigDecimal threshold);
    
    @Query("SELECT a FROM Account a WHERE a.updatedAt < :lastUpdateTime")
    List<Account> findStaleAccounts(@Param("lastUpdateTime") LocalDateTime lastUpdateTime);
    
    boolean existsByName(String name);
}