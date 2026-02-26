package com.financeiro.account.repository;

import com.financeiro.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);
    Optional<Account> findByCpf(String cpf);
    List<Account> findByUserId(Long userId);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
