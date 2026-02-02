package com.financeiro.receipt.controller;

import com.financeiro.receipt.entity.dto.response.ReceiptResponseDTO;
import com.financeiro.receipt.service.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/receipts/")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ReceiptResponseDTO> getReceipt(@PathVariable String transactionId) {
        ReceiptResponseDTO receipt = receiptService.getReceipt(transactionId);
        return ResponseEntity.ok(receipt);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<ReceiptResponseDTO>> getReceiptsByAccount(@PathVariable Long accountId) {
        List<ReceiptResponseDTO> receipts = receiptService.getReceiptsByAccount(accountId);
        return ResponseEntity.ok(receipts);
    }
}
