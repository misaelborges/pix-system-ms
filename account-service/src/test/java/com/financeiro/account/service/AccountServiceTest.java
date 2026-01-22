package com.financeiro.account.service;

import com.financeiro.account.config.mapper.AccountMapper;
import com.financeiro.account.entity.Account;
import com.financeiro.account.entity.dto.request.CreateAccountRequestDTO;
import com.financeiro.account.entity.dto.response.AccountResponseDTO;
import com.financeiro.account.entity.dto.response.BalanceResponseDTO;
import com.financeiro.account.exception.CpfAlreadyExistsException;
import com.financeiro.account.exception.EmailAlreadyExistsException;
import com.financeiro.account.exception.InsufficientBalanceException;
import com.financeiro.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private AccountService accountService;

    Account account;
    AccountResponseDTO accountResponseDTO;
    CreateAccountRequestDTO createAccountRequestDTO;

    @BeforeEach
    void setUp() {
        account = new Account(1L, 1L, "joao@email.com", "11999999999", "12345678901",
                "0001", BigDecimal.valueOf(1500.00), true, OffsetDateTime.now(), OffsetDateTime.now());

        accountResponseDTO = new AccountResponseDTO(1L, 1L, "joao@email.com", "11999999999",
                "0001", BigDecimal.valueOf(1500.00), true, OffsetDateTime.now());

        createAccountRequestDTO = new CreateAccountRequestDTO("joao@email.com", "11999999999", "12345678901");

    }

    @Test
    @DisplayName("Deve cadastrar com sucesso quando email, phone e cpf forem válidos")
    void shouldCreateAccountSuccessfully() {
        when(accountRepository.existsByEmail(createAccountRequestDTO.email())).thenReturn(false);
        when(accountRepository.existsByCpf(createAccountRequestDTO.cpf())).thenReturn(false);
        when(accountMapper.toEntity(createAccountRequestDTO)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toResponseDTO(account)).thenReturn(accountResponseDTO);

        AccountResponseDTO result = accountService.create(createAccountRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.userId());
        assertEquals(createAccountRequestDTO.email(), result.email());

        verify(accountRepository, times(1)).existsByEmail(createAccountRequestDTO.email());
        verify(accountRepository, times(1)).existsByCpf(createAccountRequestDTO.cpf());
        verify(accountMapper, times(1)).toEntity(createAccountRequestDTO);
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).toResponseDTO(account);

    }

    @Test
    @DisplayName("Deve retornar 409 quando tentar criar conta com email duplicado")
    void shouldReturnConflictWhenEmailAlreadyExists() {
        when(accountRepository.existsByEmail(createAccountRequestDTO.email())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> {
            accountService.create(createAccountRequestDTO);
        });

    }

    @Test
    @DisplayName("Deve retornar 409 quando tentar criar conta com CPF duplicado")
    void shouldReturnConflictWhenCpfAlreadyExists() {
        when(accountRepository.existsByCpf(createAccountRequestDTO.cpf())).thenReturn(true);

        assertThrows(CpfAlreadyExistsException.class, () -> {
            accountService.create(createAccountRequestDTO);
        });

    }

    @Test
    @DisplayName("Deve recuperar saldo do cache com sucesso")
    void shouldRetrieveBalanceFromCache(){
        BigDecimal balance = BigDecimal.valueOf(100);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(cacheService.getBalanceFromCache(account.getId())).thenReturn(Optional.of(balance));

        BalanceResponseDTO result = accountService.getBalance(1L);

        assertNotNull(result);
        assertEquals(1L, result.accountId());
        assertEquals(balance, result.balance());

        verify(cacheService, times(1)).getBalanceFromCache(1L);
        verify(cacheService, never()).cacheBalance(1L, balance);


    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar debitar com saldo insuficiente")
    void shouldThrowExceptionWhenDebitWithInsufficientBalance(){
        assertThrows(InsufficientBalanceException.class, () -> {
            account.debit(BigDecimal.valueOf(10000.00));
        });
    }
}