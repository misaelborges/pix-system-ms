package com.financeiro.auth.entity.dto.response;

public record AuthResponseDTO(
        String acessToken,
        String refreshToken,
        Long expiresIn,
        Long userId
) {
}
