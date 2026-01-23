package com.financeiro.pixkey.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PixKeyAlreadyExistsException extends RuntimeException {
    public PixKeyAlreadyExistsException(String message) {
        super(message);
    }
}
