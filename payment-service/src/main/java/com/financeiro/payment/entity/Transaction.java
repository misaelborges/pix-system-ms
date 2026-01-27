package com.financeiro.payment.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;

@Document("transactions")
public class Transaction {

    @Id
    private String id;

    @Field("sender_account_id")
    private Long senderAccountId;

    @Field("receiver_account_id")
    private Long receiverAccountId;

    private BigDecimal amount;
    private String description;

    @Field("pixkey_receiver")
    private String pixkeyReceiver;

    @Field("status_transaction")
    private StatusTransaction statusTransaction;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public Transaction() {
    }

    public Transaction(Long senderAccountId, Long receiverAccountId, BigDecimal amount, String description,
                       String pixkeyReceiver, StatusTransaction statusTransaction, Instant createdAt, Instant updatedAt) {
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
        this.description = description;
        this.pixkeyReceiver = pixkeyReceiver;
        this.statusTransaction = statusTransaction;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPixkeyReceiver() {
        return pixkeyReceiver;
    }

    public void setPixkeyReceiver(String pixkeyReceiver) {
        this.pixkeyReceiver = pixkeyReceiver;
    }

    public StatusTransaction getStatusTransaction() {
        return statusTransaction;
    }

    public void setStatusTransaction(StatusTransaction statusTransaction) {
        this.statusTransaction = statusTransaction;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
