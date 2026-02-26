package com.financeiro.account.exception.account;

import com.financeiro.account.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.Collections;

public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, Collections.emptyList());
    }
}
