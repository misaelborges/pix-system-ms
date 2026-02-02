package com.financeiro.receipt.service;

import com.financeiro.receipt.entity.Receipt;
import com.financeiro.receipt.entity.dto.response.ReceiptResponseDTO;
import com.financeiro.receipt.event.PaymentCompletedEvent;
import com.financeiro.receipt.exception.ReceiptNotFoundException;
import com.financeiro.receipt.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final PdfService pdfService;

    @Value("${app.upload.dir:./uploads/receipts}")
    private String uploadDir;

    public ReceiptService(ReceiptRepository receiptRepository, PdfService pdfService) {
        this.receiptRepository = receiptRepository;
        this.pdfService = pdfService;
    }

    public void createReceipt(PaymentCompletedEvent paymentCompletedEvent) {
        byte[] bytes = pdfService.generateReceipt(paymentCompletedEvent);

        String pdfPath = salvarPdfEmDisco(bytes, paymentCompletedEvent.transactionId());

        Receipt receipt = new Receipt(paymentCompletedEvent.transactionId(), paymentCompletedEvent.senderAccountId(),
                paymentCompletedEvent.receiverAccountId(), paymentCompletedEvent.amount(), pdfPath, LocalDateTime.now());

        receiptRepository.save(receipt);
    }

    public ReceiptResponseDTO getReceipt(String transactionId) {
        Receipt receipt = receiptRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ReceiptNotFoundException("Não foi encontrado nenhum recebido dessa transação"));

        return new ReceiptResponseDTO(receipt.getId(),
                receipt.getTransactionId(),
                receipt.getSenderAccountId(),
                receipt.getReceiverAccountId(),
                receipt.getAmount(),
                receipt.getPdfPath(),
                receipt.getPdfGeneratedAt());
    }

    public List<ReceiptResponseDTO> getReceiptsByAccount(Long accountId) {

        List<Receipt> receipts = receiptRepository.findByReceiverAccountId(accountId);

        return receipts.stream().map(ReceiptResponseDTO::new).toList();
    }

    private String salvarPdfEmDisco(byte[] bytes, String transactionId) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String filename = transactionId + "-" + System.currentTimeMillis() + ".pdf";
            Path filePath = uploadPath.resolve(filename);

            Files.write(filePath, bytes);

            return filePath.toAbsolutePath().toString();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar recibo em disco", e);
        }
    }
}
