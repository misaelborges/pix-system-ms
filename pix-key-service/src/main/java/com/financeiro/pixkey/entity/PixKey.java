package com.financeiro.pixkey.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity(name = "pixkey")
@Table(name = "tbl_pixkey")
public class PixKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "account_id")
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "key_type")
    private KeyTypeEnum keyType;

    @Column(nullable = false, name = "key_value", unique = true)
    private String keyValue;

    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "inactivated_at")
    private OffsetDateTime inactivatedAt;

    public PixKey() {
    }

    public PixKey(Long id, Long accountId, KeyTypeEnum keyType, String keyValue, Boolean active, OffsetDateTime createdAt,
                  OffsetDateTime inactivatedAt) {
        this.id = id;
        this.accountId = accountId;
        this.keyType = keyType;
        this.keyValue = keyValue;
        this.active = active;
        this.createdAt = createdAt;
        this.inactivatedAt = inactivatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public KeyTypeEnum getKeyType() {
        return keyType;
    }

    public void setKeyType(KeyTypeEnum keyType) {
        this.keyType = keyType;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getInactivatedAt() {
        return inactivatedAt;
    }

    public void setInactivatedAt(OffsetDateTime inactivatedAt) {
        this.inactivatedAt = inactivatedAt;
    }
}
