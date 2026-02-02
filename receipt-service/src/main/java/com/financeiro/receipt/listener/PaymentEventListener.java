package com.financeiro.receipt.listener;

import com.financeiro.receipt.event.PaymentCompletedEvent;
import com.financeiro.receipt.service.ReceiptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

    private final ReceiptService receiptService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentEventListener.class);

    public PaymentEventListener(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @RabbitListener(queues = "payment.completed")
    public void onPaymentCompleted(PaymentCompletedEvent paymentCompletedEvent) {
        try {
            receiptService.createReceipt(paymentCompletedEvent);
        } catch (Exception e) {
            logger.error("Erro ao criar recibo para transação: {}",
                    paymentCompletedEvent.transactionId(), e);
        }
    }
}
