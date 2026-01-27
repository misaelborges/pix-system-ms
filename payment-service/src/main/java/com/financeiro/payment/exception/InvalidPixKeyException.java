package com.financeiro.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPixKeyException extends RuntimeException {
    public InvalidPixKeyException(String message) {
        super(message);
    }
}
