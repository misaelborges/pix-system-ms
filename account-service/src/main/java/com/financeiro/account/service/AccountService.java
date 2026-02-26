package com.financeiro.account.service;

import com.financeiro.account.config.mapper.AccountMapper;
import com.financeiro.account.entity.Account;
import com.financeiro.account.entity.dto.request.UpdateAccountRequestDTO;
import com.financeiro.account.entity.dto.response.AccountResponseDTO;
import com.financeiro.account.entity.dto.response.AccountResumoDTO;
import com.financeiro.account.entity.dto.response.BalanceResponseDTO;
import com.financeiro.account.exception.account.AccountCreationException;
import com.financeiro.account.exception.account.AccountNotFoundException;
import com.financeiro.account.exception.BusinessException;
import com.financeiro.account.exception.validation.CpfAlreadyExistsException;
import com.financeiro.account.exception.validation.EmailAlreadyExistsException;
import com.financeiro.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CacheService cacheService;

    public AccountService(AccountRepository accountRepository, AccountMapper accountMapper, CacheService cacheService) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.cacheService = cacheService;
    }

    @Transactional
    public Account create(Account account) {
        try {
            validateEmailUniqueness(account.getEmail());
            validateCpfUniqueness(account.getCpf());

            return saveAccount(account);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new AccountCreationException("Erro ao criar conta: " + e.getMessage(), e);
        }
    }

    public AccountResponseDTO findAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Não existe uma conta com esse Id"));

        return accountMapper.toResponseDTO(account);
    }

    @Transactional
    public AccountResponseDTO updateAccount(Long accountId, UpdateAccountRequestDTO updateAccountRequestDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Não existe uma conta com esse Id"));

        if (!updateAccountRequestDTO.email().equals(account.getEmail())) {
            if (accountRepository.existsByEmail(updateAccountRequestDTO.email())) {
                throw new EmailAlreadyExistsException("Esse email já esta em uso");
            }
        }

        accountMapper.updateEntity(updateAccountRequestDTO, account);
        accountRepository.save(account);

        return accountMapper.toResponseDTO(account);
    }

    public BalanceResponseDTO getBalance(Long accountId) {
        BigDecimal balance;
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Não existe uma conta com esse Id"));

        Optional<BigDecimal> balanceFromCache = cacheService.getBalanceFromCache(account.getId());
        if (balanceFromCache.isPresent()) {
            balance = balanceFromCache.get();
        } else {
            balance = account.getBalance();
            cacheService.cacheBalance(account.getId(), balance);
        }

        return new BalanceResponseDTO(account.getId(), balance, OffsetDateTime.now());
    }

    public List<AccountResumoDTO> listByUserId(Long userId) {
        List<Account> account = accountRepository.findByUserId(userId);
        return accountMapper.toListAccountResumoDTO(account);
    }

    @Transactional
    public void debit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Não existe uma conta com esse Id"));

        account.debit(amount);
        accountRepository.save(account);
        cacheService.invalidateBalance(account.getId());
        cacheService.cacheBalance(account.getId(), account.getBalance());
    }

    @Transactional
    public void credit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Não existe uma conta com esse Id"));

        account.credit(amount);
        accountRepository.save(account);
        cacheService.invalidateBalance(account.getId());
        cacheService.cacheBalance(account.getId(), account.getBalance());
    }

    public boolean validateAccountExists(Long accountId) {//isso aqui é outra service
        Optional<Account> account = accountRepository.findById(accountId);
        return account.isPresent();
    }


    private void validateEmailUniqueness(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Esse email já esta em uso");
        }
    }

    private void validateCpfUniqueness(String cpf) {
        if (accountRepository.existsByCpf(cpf)) {
            throw new CpfAlreadyExistsException("Esse CPF já esta em uso");
        }
    }

    private Account saveAccount(Account account) {
        account.setAccountNumber(account.generateAccountNumber());
        account = accountRepository.save(account);
        return account;
    }

}
