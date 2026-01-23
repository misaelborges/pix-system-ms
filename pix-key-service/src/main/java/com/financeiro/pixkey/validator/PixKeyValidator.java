package com.financeiro.pixkey.validator;

import com.financeiro.pixkey.exception.InvalidPixKeyFormatException;

public class PixKeyValidator {

    public void validateCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new InvalidPixKeyFormatException("CPF não pode ser vazio");
        }

        String cleanCpf = cpf.replaceAll("\\D", "");
        if (!cleanCpf.matches("\\d{11}")) {
            throw new InvalidPixKeyFormatException("CPF deve conter 11 dígitos");
        }
    }

    public void validateCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            throw new InvalidPixKeyFormatException("CNPJ não pode ser vazio");
        }

        String cleanCnpj = cnpj.replaceAll("\\D", "");
        if (!cleanCnpj.matches("\\d{14}")) {
            throw new InvalidPixKeyFormatException("CNPJ deve conter 14 dígitos");
        }
    }

    public void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidPixKeyFormatException("Email não pode ser vazio");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidPixKeyFormatException("Email inválido");
        }
    }

    public void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new InvalidPixKeyFormatException("Telefone não pode ser vazio");
        }
        String cleanPhone = phone.replaceAll("\\D", "");
        if (!cleanPhone.matches("\\d{10,11}")) {
            throw new InvalidPixKeyFormatException("Telefone deve conter 10 ou 11 dígitos");
        }
    }
}
