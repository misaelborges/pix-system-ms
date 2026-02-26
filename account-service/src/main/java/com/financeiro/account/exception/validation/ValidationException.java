package com.financeiro.account.exception.validation;

import com.financeiro.account.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ValidationException extends BusinessException {

    public ValidationException(List<String> violations) {
        super("Validation failed", violations, HttpStatus.BAD_REQUEST);
    }
}