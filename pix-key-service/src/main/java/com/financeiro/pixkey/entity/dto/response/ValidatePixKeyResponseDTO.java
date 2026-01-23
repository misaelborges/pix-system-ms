package com.financeiro.pixkey.entity.dto.response;

public record ValidatePixKeyResponseDTO (
        Long accountId,
        String keyType,
        String keyValue
){
}
