package com.financeiro.account.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public abstract class BusinessException extends RuntimeException {

    private final List<String> violations;
    private final HttpStatus httpStatus;

    protected BusinessException(String message, List<String> violations, HttpStatus httpStatus) {
        super(message);
        this.violations = violations;
        this.httpStatus = httpStatus;
    }

    public BusinessException(String message, HttpStatus httpStatus, List<String> violations) {
        super(message);
        this.httpStatus = httpStatus;
        this.violations = violations;
    }

}