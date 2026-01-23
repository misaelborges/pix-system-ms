package com.financeiro.pixkey.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class MaxPixKeysLimitException extends RuntimeException {
    public MaxPixKeysLimitException(String message) {
        super(message);
    }
}
