package com.financeiro.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class AccountServiceException extends RuntimeException {
    public AccountServiceException(String message) {
        super(message);
    }
}
