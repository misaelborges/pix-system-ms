package com.financeiro.auth.entity.dto.response;

import java.util.List;

public record ValidateTokenResponseDTO(
        boolean isValidm,
        Long userId,
        List<String> roles
) {
}
