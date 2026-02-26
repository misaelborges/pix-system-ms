package com.financeiro.account.controller;

import com.financeiro.account.config.mapper.AccountMapper;
import com.financeiro.account.entity.Account;
import com.financeiro.account.entity.dto.request.CreateAccountRequestDTO;
import com.financeiro.account.entity.dto.request.UpdateAccountRequestDTO;
import com.financeiro.account.entity.dto.response.AccountResponseDTO;
import com.financeiro.account.entity.dto.response.AccountResumoDTO;
import com.financeiro.account.entity.dto.response.BalanceResponseDTO;
import com.financeiro.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

import static com.financeiro.account.validation.Validator.validate;

@RestController
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public AccountController(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @PostMapping("/accounts")
    public ResponseEntity<AccountResponseDTO> create(@RequestBody CreateAccountRequestDTO createAccountRequestDTO) {
        validate(createAccountRequestDTO);

        Account account = accountService.create(accountMapper.toEntity(createAccountRequestDTO)); //mapeia aqui

        return ResponseEntity.status(HttpStatus.CREATED).body(accountMapper.toResponseDTO(account)); //mapeia aqui, porem na creacao melhor nao retornar nada se tu nao for usar
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

    @PutMapping("/accounts/internal/{accountId}/debit")
    public ResponseEntity<?> debit(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        accountService.debit(accountId, amount);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/accounts/internal/{accountId}/credit")
    public ResponseEntity<?> credit(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        accountService.credit(accountId, amount);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
