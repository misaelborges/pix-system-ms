package com.financeiro.payment.entity.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AccountResponseDTO(
        Long id,
        Long userId,
        String email,
        String phone,
        String accountNumber,
        BigDecimal balance,
        Boolean active,
        OffsetDateTime createdAt
) {
}
