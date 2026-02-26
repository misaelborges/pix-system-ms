package com.financeiro.account.validation;

import com.financeiro.account.entity.dto.request.CreateAccountRequestDTO;
import com.financeiro.account.exception.validation.ValidationException;
import com.financeiro.account.validation.rules.ValidationRule;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.financeiro.account.validation.rules.AccountRules.cpfIsRequired;
import static com.financeiro.account.validation.rules.AccountRules.cpfIsValid;


@Component
public class Validator {

    private Validator() {
    }

    public static void validate(CreateAccountRequestDTO createAccountRequestDTO) {
        ValidationExecutor<CreateAccountRequestDTO> executor = new ValidationExecutor<>();

        List<ValidationRule<CreateAccountRequestDTO>> validationRules = List.of(
                cpfIsRequired(),
                cpfIsValid()// aqui tu vai colocando as validacoes novas
        );

        List<String> violations = executor.execute(createAccountRequestDTO, validationRules);

        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }

    }

}
