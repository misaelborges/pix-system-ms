package com.financeiro.payment.entity.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequestDTO(
        @NotNull(message = "")
        Long senderAccountId,

        @NotNull(message = "Chave PIX deve ser informado")
        String pixKeyReceiver,

        @Positive(message = "Valor da transferencia deve ser positivo")
        @NotNull(message = "Valor não pode ser vazio")
        BigDecimal amount,

        String description
    ){
}
