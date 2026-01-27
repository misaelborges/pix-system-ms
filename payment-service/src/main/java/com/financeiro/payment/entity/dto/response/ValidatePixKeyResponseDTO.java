package com.financeiro.payment.entity.dto.response;

public record ValidatePixKeyResponseDTO (
        Long accountId,
        String keyType,
        String keyValue
){
}