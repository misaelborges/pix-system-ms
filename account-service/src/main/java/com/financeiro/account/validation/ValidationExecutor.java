package com.financeiro.account.validation;

import com.financeiro.account.validation.rules.ValidationRule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ValidationExecutor<T> {

    @SafeVarargs
    public final List<String> execute(T object, ValidationRule<T>... rules) {
        return execute(object, Arrays.asList(rules));
    }

    public List<String> execute(T object, List<ValidationRule<T>> rules) {
        List<String> violations = new ArrayList<>();

        rules.forEach(rule -> rule.validate(object, violations));

        return violations;
    }

}
