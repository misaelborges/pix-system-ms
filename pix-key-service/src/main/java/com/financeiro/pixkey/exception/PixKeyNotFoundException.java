package com.financeiro.pixkey.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PixKeyNotFoundException extends RuntimeException {
    public PixKeyNotFoundException(String message) {
        super(message);
    }
}
