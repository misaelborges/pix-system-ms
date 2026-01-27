package com.financeiro.payment.controller;

import com.financeiro.payment.entity.dto.request.PaymentRequestDTO;
import com.financeiro.payment.entity.dto.response.PaymentResponseDTO;
import com.financeiro.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pix-transfer")
    public ResponseEntity<PaymentResponseDTO> transfer(@RequestBody @Valid PaymentRequestDTO paymentRequestDTO) {
        PaymentResponseDTO paymentResponseDTO = paymentService.transfer(paymentRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDTO);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentResponseDTO> getTransaction(@PathVariable String transactionId) {
        PaymentResponseDTO paymentResponseDTO = paymentService.getTransaction(transactionId);
        return ResponseEntity.status(HttpStatus.OK).body(paymentResponseDTO);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<PaymentResponseDTO>> getAccountTransactions(@PathVariable Long accountId) {
        List<PaymentResponseDTO> paymentResponseDTO = paymentService.getAccountTransactions(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(paymentResponseDTO);
    }
}
