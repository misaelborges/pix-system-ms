package com.financeiro.payment.service;

import com.financeiro.payment.entity.StatusTransaction;
import com.financeiro.payment.entity.Transaction;
import com.financeiro.payment.entity.dto.request.PaymentRequestDTO;
import com.financeiro.payment.entity.dto.response.AccountResponseDTO;
import com.financeiro.payment.entity.dto.response.PaymentResponseDTO;
import com.financeiro.payment.entity.dto.response.ValidatePixKeyResponseDTO;
import com.financeiro.payment.event.PaymentCompletedEvent;
import com.financeiro.payment.exception.InsufficientBalanceException;
import com.financeiro.payment.exception.TransactionNotFoundException;
import com.financeiro.payment.publisher.PaymentEventPublisher;
import com.financeiro.payment.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentEventPublisher paymentEventPublisher;
    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;
    private final PixKeyServiceClient pixKeyServiceClient;

    public PaymentService(PaymentEventPublisher paymentEventPublisher,
                          TransactionRepository transactionRepository,
                          AccountServiceClient accountServiceClient,
                          PixKeyServiceClient pixKeyServiceClient) {

        this.paymentEventPublisher = paymentEventPublisher;
        this.transactionRepository = transactionRepository;
        this.accountServiceClient = accountServiceClient;
        this.pixKeyServiceClient = pixKeyServiceClient;
    }

    @Transactional
    public PaymentResponseDTO transfer(PaymentRequestDTO paymentRequestDTO) {
        accountServiceClient.validateAccountExists(paymentRequestDTO.senderAccountId());

        ValidatePixKeyResponseDTO validatePixKeyResponseDTO = pixKeyServiceClient.validatePixKey(paymentRequestDTO.pixKeyReceiver());

        AccountResponseDTO accountResponseDTO = accountServiceClient.getAccount(paymentRequestDTO.senderAccountId());

        if (accountResponseDTO.balance().compareTo(paymentRequestDTO.amount()) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente para transação PIX");
        }

        Long senderAccountId = paymentRequestDTO.senderAccountId();
        Long receiverAccountId = validatePixKeyResponseDTO.accountId();
        BigDecimal amount = paymentRequestDTO.amount();
        String description = paymentRequestDTO.description();
        String pixkeyReceiver = paymentRequestDTO.pixKeyReceiver();
        StatusTransaction statusTransaction = StatusTransaction.PENDING;
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();

        Transaction transaction = new Transaction(senderAccountId, receiverAccountId, amount, description,
                                                                pixkeyReceiver, statusTransaction, createdAt, updatedAt);

        accountServiceClient.debit(senderAccountId, amount);
        accountServiceClient.credit(receiverAccountId, amount);

        transaction.setStatusTransaction(StatusTransaction.COMPLETED);
        transaction.setUpdatedAt(Instant.now());

        transaction = transactionRepository.save(transaction);

        PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent(transaction);

        paymentEventPublisher.publishPaymentCompleted(paymentCompletedEvent);

        return new PaymentResponseDTO(transaction);
    }

    public PaymentResponseDTO getTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Não foi encontrada nenhuma transação"));

        return new PaymentResponseDTO(transaction);
    }

    public List<PaymentResponseDTO> getAccountTransactions(Long accountId) {
        List<Transaction> transactionList = transactionRepository.findBySenderAccountId(accountId);
        return transactionList
                .stream()
                .map(PaymentResponseDTO::new)
                .toList();
    }
}
