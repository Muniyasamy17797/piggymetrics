package com.piggymetrics.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggymetrics.account.dto.AccountDTO;
import com.piggymetrics.account.dto.TransactionDTO;
import com.piggymetrics.account.domain.User;
import com.piggymetrics.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAccountByName_ReturnsAccount() throws Exception {
        // Arrange
        String accountName = "testUser";
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setName(accountName);
        accountDTO.setBalance(new BigDecimal("100.00"));

        when(accountService.findByName(accountName)).thenReturn(accountDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/{name}", accountName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(accountName))
                .andExpect(jsonPath("$.balance").value("100.00"));
    }

    @Test
    @WithMockUser
    void getCurrentAccount_ReturnsCurrentUserAccount() throws Exception {
        // Arrange
        String username = "currentUser";
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setName(username);

        when(accountService.findByName(username)).thenReturn(accountDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/current")
                        .principal(() -> username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(username));
    }

    @Test
    @WithMockUser
    void createNewAccount_CreatesAccount() throws Exception {
        // Arrange
        User user = new User();
        user.setUsername("newUser");

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setName(user.getUsername());

        when(accountService.create(any(User.class))).thenReturn(accountDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getUsername()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getInactiveAccounts_ReturnsInactiveAccounts() throws Exception {
        // Arrange
        AccountDTO account1 = new AccountDTO();
        account1.setName("inactive1");
        AccountDTO account2 = new AccountDTO();
        account2.setName("inactive2");

        when(accountService.findInactiveAccounts()).thenReturn(Arrays.asList(account1, account2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/inactive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("inactive1"))
                .andExpect(jsonPath("$[1].name").value("inactive2"));
    }

    @Test
    @WithMockUser
    void addTransaction_AddsTransactionToAccount() throws Exception {
        // Arrange
        String accountName = "testUser";
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(new BigDecimal("50.00"));

        AccountDTO updatedAccount = new AccountDTO();
        updatedAccount.setName(accountName);
        updatedAccount.setBalance(new BigDecimal("150.00"));

        when(accountService.addTransaction(eq(accountName), any(TransactionDTO.class)))
                .thenReturn(updatedAccount);

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/{name}/transactions", accountName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(accountName))
                .andExpect(jsonPath("$.balance").value("150.00"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAccount_DeletesAccount() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/accounts/{name}", "testUser"))
                .andExpect(status().isNoContent());
    }
}