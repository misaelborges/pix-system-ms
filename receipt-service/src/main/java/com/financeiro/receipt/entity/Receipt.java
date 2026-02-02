package com.financeiro.receipt.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "receipt")
@Table(name = "tbl_receipt")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "sender_account_id")
    private Long senderAccountId;

    @Column(name = "receiver_account_id")
    private Long receiverAccountId;

    private BigDecimal amount;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(name = "pdf_generated_at")
    private LocalDateTime pdfGeneratedAt;

    public Receipt() {
    }

    public Receipt(String transactionId, Long senderAccountId, Long receiverAccountId, BigDecimal amount) {
        this.transactionId = transactionId;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
    }

    public Receipt(String transactionId, Long senderAccountId, Long receiverAccountId, BigDecimal amount, String pdfPath, LocalDateTime pdfGeneratedAt) {
        this.transactionId = transactionId;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
        this.pdfPath = pdfPath;
        this.pdfGeneratedAt = pdfGeneratedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getSenderAccountId() {
        return senderAccountId;
    }

    public void setSenderAccountId(Long senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    public Long getReceiverAccountId() {
        return receiverAccountId;
    }

    public void setReceiverAccountId(Long receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public LocalDateTime getPdfGeneratedAt() {
        return pdfGeneratedAt;
    }

    public void setPdfGeneratedAt(LocalDateTime pdfGeneratedAt) {
        this.pdfGeneratedAt = pdfGeneratedAt;
    }
}
