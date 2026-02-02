package com.financeiro.receipt.entity.dto.response;

import com.financeiro.receipt.entity.Receipt;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReceiptResponseDTO(
        Long id,
        String transactionId,
        Long senderAccountId,
        Long receiverAccountId,
        BigDecimal amount,
        String pdfPath,
        LocalDateTime pdfGeneratedAt
) {

    public ReceiptResponseDTO(Receipt receipt) {
        this(receipt.getId(), receipt.getTransactionId(), receipt.getSenderAccountId(), receipt.getSenderAccountId(),
                receipt.getAmount(), receipt.getPdfPath(), receipt.getPdfGeneratedAt());
    }
}
