package com.financeiro.payment.service;

import com.financeiro.payment.entity.StatusTransaction;
import com.financeiro.payment.entity.Transaction;
import com.financeiro.payment.entity.dto.request.PaymentRequestDTO;
import com.financeiro.payment.entity.dto.response.AccountResponseDTO;
import com.financeiro.payment.entity.dto.response.PaymentResponseDTO;
import com.financeiro.payment.entity.dto.response.ValidatePixKeyResponseDTO;
import com.financeiro.payment.event.PaymentCompletedEvent;
import com.financeiro.payment.exception.AccountServiceException;
import com.financeiro.payment.exception.InsufficientBalanceException;
import com.financeiro.payment.publisher.PaymentEventPublisher;
import com.financeiro.payment.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private PixKeyServiceClient pixKeyServiceClient;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequestDTO paymentRequestDTO;
    private ValidatePixKeyResponseDTO pixKeyResponseDTO;
    private AccountResponseDTO senderAccount;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        paymentRequestDTO = new PaymentRequestDTO(
                1L,
                "joao@email.com",
                BigDecimal.valueOf(200),
                "Pagamento PIX"
        );

        pixKeyResponseDTO = new ValidatePixKeyResponseDTO(
                2L,
                "EMAIL",
                "joao@email.com"
        );

        senderAccount = new AccountResponseDTO(
                1L,
                1L,
                "sender@email.com",
                "11999999999",
                "0001",
                BigDecimal.valueOf(1000),
                true,
                OffsetDateTime.now()
        );

        transaction = new Transaction(
                1L,
                2L,
                BigDecimal.valueOf(200),
                "Pagamento PIX",
                "joao@email.com",
                StatusTransaction.COMPLETED,
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    @DisplayName("Deve realizar a transferência com sucesso")
    void shouldTransferSuccessfully() {

        when(accountServiceClient.validateAccountExists(1L))
                .thenReturn(true);

        when(pixKeyServiceClient.validatePixKey(paymentRequestDTO.pixKeyReceiver()))
                .thenReturn(pixKeyResponseDTO);

        when(accountServiceClient.getAccount(1L))
                .thenReturn(senderAccount);

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction);

        PaymentResponseDTO response = paymentService.transfer(paymentRequestDTO);

        assertNotNull(response);
        assertEquals(1L, response.senderAccountId());
        assertEquals(2L, response.receiverAccountId());
        assertEquals(BigDecimal.valueOf(200), response.amount());
        assertEquals(StatusTransaction.COMPLETED.getDescription(), response.status());

        verify(accountServiceClient).validateAccountExists(1L);
        verify(accountServiceClient).debit(1L, BigDecimal.valueOf(200));
        verify(accountServiceClient).credit(2L, BigDecimal.valueOf(200));
        verify(transactionRepository).save(any(Transaction.class));
        verify(paymentEventPublisher).publishPaymentCompleted(any(PaymentCompletedEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a conta de origem não existir")
    void shouldThrowExceptionWhenAccountDoesNotExist() {

        when(accountServiceClient.validateAccountExists(1L))
                .thenReturn(false);

        assertThrows(AccountServiceException.class, () ->
                paymentService.transfer(paymentRequestDTO)
        );

        verify(accountServiceClient, never()).debit(anyLong(), any());
        verify(transactionRepository, never()).save(any());
        verify(paymentEventPublisher, never()).publishPaymentCompleted(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o saldo for insuficiente")
    void shouldThrowExceptionWhenBalanceIsInsufficient() {
        AccountResponseDTO accountWithLowBalance = new AccountResponseDTO(
                1L,
                1L,
                "sender@email.com",
                "11999999999",
                "0001",
                BigDecimal.valueOf(100),
                true,
                OffsetDateTime.now()
        );

        when(accountServiceClient.validateAccountExists(1L))
                .thenReturn(true);

        when(pixKeyServiceClient.validatePixKey(paymentRequestDTO.pixKeyReceiver()))
                .thenReturn(pixKeyResponseDTO);

        when(accountServiceClient.getAccount(paymentRequestDTO.senderAccountId()))
                .thenReturn(accountWithLowBalance);

        assertThrows(InsufficientBalanceException.class, () -> {
            paymentService.transfer(paymentRequestDTO);
        });

        verify(accountServiceClient, never()).debit(anyLong(), any());
        verify(accountServiceClient, never()).credit(anyLong(), any());
        verify(transactionRepository, never()).save(any());
        verify(paymentEventPublisher, never()).publishPaymentCompleted(any());
    }
}