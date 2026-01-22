package com.financeiro.account.controller;

import com.financeiro.account.entity.dto.request.CreateAccountRequestDTO;
import com.financeiro.account.entity.dto.request.UpdateAccountRequestDTO;
import com.financeiro.account.entity.dto.response.AccountResponseDTO;
import com.financeiro.account.entity.dto.response.AccountResumoDTO;
import com.financeiro.account.entity.dto.response.BalanceResponseDTO;
import com.financeiro.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/accounts")
    public ResponseEntity<AccountResponseDTO> create(@RequestBody @Valid CreateAccountRequestDTO createAccountRequestDTO) {
        AccountResponseDTO accountResponseDTO = accountService.create(createAccountRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponseDTO);
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<AccountResponseDTO> findAccountById(@PathVariable Long accountId) {
        AccountResponseDTO accountResponseDTO = accountService.findAccountById(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(accountResponseDTO);
    }

    @PutMapping("/accounts/{accountId}")
    public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable Long accountId,
                                                            @RequestBody @Valid UpdateAccountRequestDTO updateAccountRequestDTO) {
        AccountResponseDTO accountResponseDTO = accountService.updateAccount(accountId, updateAccountRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(accountResponseDTO);
    }

    @GetMapping("/accounts/{accountId}/balance")
    public ResponseEntity<BalanceResponseDTO> getBalance(@PathVariable Long accountId) {
        BalanceResponseDTO balanceResponseDTO = accountService.getBalance(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(balanceResponseDTO);
    }

    @GetMapping("/accounts/user/{userId}")
    public ResponseEntity<List<AccountResumoDTO>> listByUserId(@PathVariable Long userId) {
        List<AccountResumoDTO> accountResumoDTOS = accountService.listByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(accountResumoDTOS);
    }

    @PostMapping("/accounts/internal/validate/{accountId}")
    public ResponseEntity<Boolean> validateAccountExists(@PathVariable Long accountId) {
        boolean validateAccountExists = accountService.validateAccountExists(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(validateAccountExists);
    }
}
