package com.financeiro.payment.service;

import com.financeiro.payment.entity.dto.response.AccountResponseDTO;
import com.financeiro.payment.exception.AccountServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Service
public class AccountServiceClient {

    @Value("${account.service.url}")
    private String accountServiceUrl;

    private final RestClient restClient;

    public AccountServiceClient(RestClient.Builder restBuilder) {
        this.restClient = restBuilder.build();
    }

    public AccountResponseDTO getAccount(Long accountId) {
        try {
            String url = accountServiceUrl + "/{accountId}";
            return restClient.get()
                    .uri(url, accountId)
                    .retrieve()
                    .body(AccountResponseDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new AccountServiceException("Conta não encontrada");
        } catch (Exception e) {
            throw new AccountServiceException("Erro ao obter conta: " + e.getMessage());
        }
    }

    public boolean validateAccountExists(Long accountId) {
        try {
            String url = accountServiceUrl + "/internal/validate/{accountId}";
            Boolean exists = restClient.get()
                    .uri(url, accountId)
                    .retrieve()
                    .body(Boolean.class);
            return Boolean.TRUE.equals(exists);
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void debit(Long accountId, BigDecimal amount) {
        try {
            String url = accountServiceUrl + "/internal/{accountId}/debit?amount={amount}";
            restClient.put()
                    .uri(url, accountId, amount)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest e) {
            throw new AccountServiceException("Saldo insuficiente");
        } catch (HttpClientErrorException.NotFound e) {
            throw new AccountServiceException("Conta não encontrada");
        } catch (Exception e) {
            throw new AccountServiceException("Erro ao debitar conta: " + e.getMessage());
        }
    }

    public void credit(Long accountId, BigDecimal amount) {
        try {
            String url = accountServiceUrl + "/internal/{accountId}/credit?amount={amount}";
            restClient.put()
                    .uri(url, accountId, amount)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new AccountServiceException("Conta não encontrada");
        } catch (Exception e) {
            throw new AccountServiceException("Erro ao creditar conta: " + e.getMessage());
        }
    }
}
