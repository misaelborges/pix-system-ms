package com.financeiro.payment.event;

import com.financeiro.payment.entity.Transaction;

import java.math.BigDecimal;

public record PaymentCompletedEvent(
        String transactionId,
        Long senderAccountId,
        Long receiverAccountId,
        BigDecimal amount,
        String status,
        String createdAt
) {

    public PaymentCompletedEvent(Transaction transaction) {
        this(transaction.getId(),
             transaction.getSenderAccountId(),
             transaction.getReceiverAccountId(),
             transaction.getAmount(),
             transaction.getStatusTransaction().getDescription(),
             transaction.getCreatedAt().toString());
    }
}
