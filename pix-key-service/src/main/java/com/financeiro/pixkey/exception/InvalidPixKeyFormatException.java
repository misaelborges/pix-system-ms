package com.financeiro.pixkey.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPixKeyFormatException extends RuntimeException {
    public InvalidPixKeyFormatException(String message) {
        super(message);
    }
}
