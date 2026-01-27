package com.financeiro.payment.service;

import com.financeiro.payment.entity.dto.response.ValidatePixKeyResponseDTO;
import com.financeiro.payment.exception.AccountServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
public class PixKeyServiceClient {

    @Value("${pixkey.service.url}")
    private String pixkeyServiceUrl;

    private final RestClient restClient;

    public PixKeyServiceClient(RestClient.Builder restBuilder) {
        this.restClient = restBuilder.build();
    }

    public boolean validatePixKeyExists(String pixKeyValue) {
        try {
            String url = pixkeyServiceUrl + "/internal/validate/{pixKeyValue}";
            Boolean exists = restClient.get()
                    .uri(url, pixKeyValue)
                    .retrieve()
                    .body(Boolean.class);
            return Boolean.TRUE.equals(exists);
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public ValidatePixKeyResponseDTO validatePixKey(String pixKeyValue) {
        try {
            String url = pixkeyServiceUrl + "/validate/{pixKeyValue}";
            return restClient.get()
                    .uri(url, pixKeyValue)
                    .retrieve()
                    .body(ValidatePixKeyResponseDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new AccountServiceException("Conta não encontrada");
        } catch (Exception e) {
            throw new AccountServiceException("Erro ao obter conta" + e.getMessage());
        }
    }
}
