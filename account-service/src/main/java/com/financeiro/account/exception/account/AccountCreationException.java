package com.financeiro.account.exception.account;

import com.financeiro.account.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.Collections;

public class AccountCreationException extends BusinessException {
    public AccountCreationException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, Collections.emptyList());
        initCause(cause);
    }
}
