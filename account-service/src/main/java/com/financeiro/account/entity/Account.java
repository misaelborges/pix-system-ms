package com.financeiro.account.entity;

import com.financeiro.account.exception.account.AmountInvalidException;
import com.financeiro.account.exception.account.InsufficientBalanceException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@Entity(name = "account")
@Table(name = "tbl_account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, unique = true, name = "account_number")
    private String accountNumber;

    @Column(nullable = false, scale = 2)
    private BigDecimal balance = BigDecimal.valueOf(0.00);

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private OffsetDateTime updatedAt;

    public Account() {
    }

    public Account(Long id, Long userId, String email, String phone, String cpf, String accountNumber,
                   BigDecimal balance, Boolean active, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.phone = phone;
        this.cpf = cpf;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void debit(BigDecimal amount) {
        validateAmount(amount);

        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        validateAmount(amount);

        this.balance = this.balance.add(amount);
    }

    private void validateAmount(BigDecimal amoun) {
        if (amoun == null | amoun.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AmountInvalidException("Valor deve ser maior que $0.00");
        }
    }

    public String generateAccountNumber() {
        int number = ThreadLocalRandom.current().nextInt(10000, 100000);
        return "ACC-" + number;
    }

}
