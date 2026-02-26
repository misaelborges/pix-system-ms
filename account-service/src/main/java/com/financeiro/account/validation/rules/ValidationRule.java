package com.financeiro.account.validation.rules;

import java.util.List;

@FunctionalInterface
public interface ValidationRule<T> {
    void validate(T object, List<String> violations);
}
