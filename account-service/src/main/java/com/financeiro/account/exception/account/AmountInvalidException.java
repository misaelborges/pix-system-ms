package com.financeiro.account.exception.account;

import com.financeiro.account.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.Collections;

public class AmountInvalidException extends BusinessException {
    public AmountInvalidException(String message) {
        super(message, HttpStatus.BAD_REQUEST, Collections.emptyList());
    }
}
