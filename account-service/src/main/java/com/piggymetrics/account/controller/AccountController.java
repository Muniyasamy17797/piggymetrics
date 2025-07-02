package com.piggymetrics.account.controller;

import com.piggymetrics.account.dto.AccountDTO;
import com.piggymetrics.account.dto.TransactionDTO;
import com.piggymetrics.account.domain.User;
import com.piggymetrics.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize("#oauth2.hasScope('server') or #name.equals('demo')")
    @GetMapping("/{name}")
    public ResponseEntity<AccountDTO> getAccountByName(@PathVariable String name) {
        return ResponseEntity.ok(accountService.findByName(name));
    }

    @GetMapping("/current")
    public ResponseEntity<AccountDTO> getCurrentAccount(Principal principal) {
        return ResponseEntity.ok(accountService.findByName(principal.getName()));
    }

    @PutMapping("/current")
    public ResponseEntity<AccountDTO> saveCurrentAccount(Principal principal, @Valid @RequestBody AccountDTO account) {
        return ResponseEntity.ok(accountService.saveChanges(principal.getName(), account));
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createNewAccount(@Valid @RequestBody User user) {
        return ResponseEntity.ok(accountService.create(user));
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccount(@PathVariable String name) {
        accountService.deleteAccount(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountDTO>> getInactiveAccounts() {
        return ResponseEntity.ok(accountService.findInactiveAccounts());
    }

    @PostMapping("/{name}/transactions")
    public ResponseEntity<AccountDTO> addTransaction(
            @PathVariable String name,
            @Valid @RequestBody TransactionDTO transaction) {
        return ResponseEntity.ok(accountService.addTransaction(name, transaction));
    }
}