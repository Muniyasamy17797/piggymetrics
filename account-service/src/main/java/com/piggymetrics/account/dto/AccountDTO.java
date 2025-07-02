package com.piggymetrics.account.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDTO {
    private Long id;

    @NotBlank(message = "Account name cannot be empty")
    @Size(min = 3, max = 50, message = "Account name must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Account name can only contain letters, numbers, dots, underscores and hyphens")
    private String name;

    @NotNull(message = "Last seen date cannot be null")
    private LocalDateTime lastSeen;

    @Size(max = 1000, message = "Note cannot exceed 1000 characters")
    private String note;

    @NotNull(message = "Balance cannot be null")
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Balance must have at most 10 digits and 2 decimal places")
    private BigDecimal balance;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @Valid
    private List<TransactionDTO> transactions;

    private Long userId;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}