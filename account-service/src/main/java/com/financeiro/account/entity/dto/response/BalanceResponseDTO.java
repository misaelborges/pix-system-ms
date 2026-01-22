package com.financeiro.account.entity.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record BalanceResponseDTO(
        Long accountId,
        BigDecimal balance,
        OffsetDateTime consultedAt
) {
}
