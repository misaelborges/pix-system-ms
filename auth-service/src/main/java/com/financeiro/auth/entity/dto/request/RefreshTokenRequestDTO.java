package com.financeiro.auth.entity.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
        @NotBlank(message = "Refresh token não pode estar em branco")
        String refreshToken
) {

}
