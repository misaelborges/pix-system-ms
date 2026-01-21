package com.financeiro.auth.entity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequestDTO(
        @Email(message = "Email inválido, tente novamente.")
        @NotBlank(message = "Email precisa ser preenchido.")
        String email,

        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
                message = "A senha deve conter no mínimo 8 caracteres, incluindo letra maiúscula, letra minúscula, número e caractere especial."
        )
        @NotBlank(message = "A senha é obrigatória.")
        String password
) {

}
