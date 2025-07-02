package com.piggymetrics.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreateDTO {
    
    @NotBlank(message = "Account name cannot be empty")
    @Size(min = 3, max = 50, message = "Account name must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Account name can only contain letters, numbers, dots, underscores and hyphens")
    private String name;
    
    @Size(max = 1000, message = "Note cannot exceed 1000 characters")
    private String note;
    
    @NotNull(message = "Initial balance cannot be null")
    @DecimalMin(value = "0.00", message = "Initial balance cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Balance must have at most 10 digits and 2 decimal places")
    private BigDecimal initialBalance;
    
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    
    private List<TransactionDTO> initialTransactions = new ArrayList<>();
}