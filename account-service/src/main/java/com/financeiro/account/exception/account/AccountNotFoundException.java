package com.financeiro.account.exception.account;

import com.financeiro.account.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.Collections;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, Collections.emptyList());
    }
}
