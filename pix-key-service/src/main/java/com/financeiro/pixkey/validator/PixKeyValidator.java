package com.financeiro.pixkey.validator;

public class PixKeyValidator {

    public boolean validateCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return false;
        }

        String cleanCpf = cpf.replaceAll("\\D", "");
        return cleanCpf.matches("\\d{11}");
    }

    public boolean validateCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            return false;
        }

        String cleanCnpj = cnpj.replaceAll("\\D", "");
        return cleanCnpj.matches("\\d{14}");
    }

    public boolean validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public boolean validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return false;
        }
        String cleanPhone = phone.replaceAll("\\D", "");
        return cleanPhone.matches("\\d{10,11}");
    }
}
