package com.financeiro.payment.entity;

public enum StatusTransaction {

    PENDING("Pending"),
    COMPLETED("Completed"),
    FAILED("Failed");

    private final String description;

    StatusTransaction(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
