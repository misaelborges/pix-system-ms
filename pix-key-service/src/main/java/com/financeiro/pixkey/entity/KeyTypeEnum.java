package com.financeiro.pixkey.entity;

public enum KeyTypeEnum {

    CPF("CPF"),
    CNPJ("CNPJ"),
    EMAIL("EMAIL"),
    PHONE("PHONE"),
    RANDOM("RANDOM");

    private String type;

    KeyTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
