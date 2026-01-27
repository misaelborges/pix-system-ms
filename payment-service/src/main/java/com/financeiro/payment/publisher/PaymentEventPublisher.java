package com.financeiro.payment.publisher;

import com.financeiro.payment.event.PaymentCompletedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventPublisher {

    @Value("${payments.exchange.name}")
    private String paymentsExchange;

    @Value("${payment.completed.routing.key}")
    private String paymentCompletedRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPaymentCompleted(PaymentCompletedEvent paymentCompletedEvent) {
        try {
            rabbitTemplate.convertAndSend(paymentsExchange, paymentCompletedRoutingKey, paymentCompletedEvent);
        } catch (Exception e ) {
            throw new RuntimeException("Erro ao publicar evento de pagamento");
        }
    }

}
