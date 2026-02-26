package com.financeiro.account.exception.validation;

import com.financeiro.account.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.Collections;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT, Collections.emptyList());
    }
}
