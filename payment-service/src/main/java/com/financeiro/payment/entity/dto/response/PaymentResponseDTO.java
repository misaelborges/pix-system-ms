package com.financeiro.payment.entity.dto.response;

import com.financeiro.payment.entity.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponseDTO(
        String id,
        Long senderAccountId,
        Long receiverAccountId,
        BigDecimal amount,
        String status,
        Instant createdAt
) {

    public PaymentResponseDTO(Transaction transaction) {
        this(transaction.getId(),
                transaction.getSenderAccountId(),
                transaction.getReceiverAccountId(),
                transaction.getAmount(),
                transaction.getStatusTransaction().getDescription(),
                transaction.getCreatedAt());
    }
}
