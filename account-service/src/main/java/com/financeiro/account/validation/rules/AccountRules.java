package com.financeiro.account.validation.rules;

import com.financeiro.account.entity.dto.request.CreateAccountRequestDTO;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static java.util.Objects.isNull;

@Component
public class AccountRules {

    private AccountRules() {
    }

    public static ValidationRule<CreateAccountRequestDTO> cpfIsRequired() {
        return createRequiredFieldValidation(
                CreateAccountRequestDTO::cpf,
                "O cpf do cliente é obrigatório.",
                "O cpf do cliente não pode ser vazio."
        );
    }


    public static ValidationRule<CreateAccountRequestDTO> cpfIsValid() {
        return (request, violations) -> {
            if (request == null || request.cpf() == null) {
                return;
            }

            String cpf = request.cpf().replaceAll("\\D", "");

            if (cpf.length() != 11) {
                violations.add("O CPF deve conter 11 dígitos.");
                return;
            }

            if (!isValidCPF(cpf)) {
                violations.add("O CPF informado é inválido.");
            }
        };
    }

    private static <T> ValidationRule<T> createRequiredFieldValidation(
            Function<T, String> fieldExtractor,
            String requiredMessage,
            String emptyMessage) {

        return (request, violations) -> {
            if (request == null) {
                return;
            }
            String fieldValue = fieldExtractor.apply(request);
            if (isNull(fieldValue)) {
                violations.add(requiredMessage);
            } else if (fieldValue.trim().isEmpty()) {
                violations.add(emptyMessage);
            }
        };
    }

    private static boolean isValidCPF(String cpf) {
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int sum1 = 0;
        for (int i = 0; i < 9; i++) {
            sum1 += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int check1 = 11 - (sum1 % 11);
        if (check1 >= 10) {
            check1 = 0;
        }

        int sum2 = 0;
        for (int i = 0; i < 10; i++) {
            sum2 += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int check2 = 11 - (sum2 % 11);
        if (check2 >= 10) {
            check2 = 0;
        }

        return check1 == Character.getNumericValue(cpf.charAt(9)) &&
                check2 == Character.getNumericValue(cpf.charAt(10));
    }
}

