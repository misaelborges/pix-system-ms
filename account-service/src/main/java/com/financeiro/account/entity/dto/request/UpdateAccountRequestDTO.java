package com.financeiro.account.entity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateAccountRequestDTO(
        @Email(message = "Formato de email inválido")
        @NotNull(message = "Email é obrigatório")
        String email,

        @Pattern(
                regexp = "\\d{10,11}",
                message = "Telefone deve conter 10 ou 11 dígitos numéricos"
        )
        @NotNull(message = "Telefone é obrigatório")
        String phone
) {
}
