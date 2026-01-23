package com.financeiro.pixkey.entity.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreatePixKeyRequestDTO(
        @NotNull(message = "Usuario obrigatório para gerar chave Pix")
        Long accountId,

        @NotNull(message = "Tipo de chave Pix é obrigatório")
        String keyType,

        @NotNull(message = "Valor da chave Pix é obrigatório")
        String keyValue
) {
}
