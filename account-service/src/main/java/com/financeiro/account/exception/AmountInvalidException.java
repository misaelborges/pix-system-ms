package com.financeiro.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AmountInvalidException extends RuntimeException {
    public AmountInvalidException(String message) {
        super(message);
    }
}
