package com.financeiro.account.entity.dto.request;

public record CreateAccountRequestDTO(Long userId, String email, String phone, String cpf) {
}
