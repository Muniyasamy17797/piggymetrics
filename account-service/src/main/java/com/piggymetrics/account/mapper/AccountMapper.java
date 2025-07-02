package com.piggymetrics.account.mapper;

import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.dto.AccountDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {TransactionMapper.class})
public interface AccountMapper {
    
    @Mapping(target = "userId", source = "user.id")
    AccountDTO toDto(Account account);
    
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Account toEntity(AccountDTO accountDTO);
    
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "balance", source = "initialBalance")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastSeen", expression = "java(java.time.LocalDateTime.now())")
    Account toEntity(AccountCreateDTO createDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(AccountDTO accountDTO, @MappingTarget Account account);

    @AfterMapping
    default void setTimestamps(@MappingTarget Account account) {
        if (account.getCreatedAt() == null) {
            account.setCreatedAt(LocalDateTime.now());
        }
        account.setUpdatedAt(LocalDateTime.now());
    }
}