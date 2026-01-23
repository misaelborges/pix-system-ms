package com.financeiro.pixkey.service;

import com.financeiro.pixkey.exception.AccountServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

@Service
public class AccountServiceClient {

    private static final String URL = "http://localhost:8081/api/v1/accounts/internal/validate/{accountId}";
    private final RestClient restClient;

    public AccountServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public boolean validateAccountExists(Long accountId) {

        try {
            return Boolean.TRUE.equals(
                    restClient.post()
                            .uri(URL, accountId)
                            .retrieve()
                            .body(Boolean.class)
            );
        } catch (HttpServerErrorException e) {
            throw new AccountServiceException("Erro ao validar conta: " + e.getMessage());
        }
    }
}
