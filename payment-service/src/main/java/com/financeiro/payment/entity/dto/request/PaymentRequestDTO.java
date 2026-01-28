package com.financeiro.payment.entity.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequestDTO(
        @NotNull(message = "Conta de origem deve ser informada")
        Long senderAccountId,

        @NotNull(message = "Chave PIX deve ser informada")
        String pixKeyReceiver,

        @NotNull(message = "Valor não pode ser vazio")
        @Positive(message = "Valor da transferência deve ser positivo")
        BigDecimal amount,

        String description
    ){
}
