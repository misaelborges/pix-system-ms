package com.financeiro.receipt.event;

import java.math.BigDecimal;

public record PaymentCompletedEvent(
        String transactionId,
        Long senderAccountId,
        Long receiverAccountId,
        BigDecimal amount,
        String status,
        String createdAt
) {
}
