package com.financeiro.pixkey.entity.dto.response;

import java.time.OffsetDateTime;

public record PixKeyResponseDTO(
        Long id,
        Long accountId,
        String keyType,
        String keyValue,
        OffsetDateTime createdAt,
        Boolean active
) {
}
