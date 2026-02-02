package com.financeiro.receipt.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${payments.exchange.name}")
    public String paymentsExchange;

    @Value("${payment.completed.queue}")
    public String paymentCompletedQueue;

    @Value("${payment.completed.routing.key}")
    public String paymentCompletedRoutingKey;

    @Bean
    public DirectExchange paymentsExchange() {
        return new DirectExchange(paymentsExchange);
    }

   @Bean
   public Queue paymentCompletedQueue() {
        return new Queue(paymentCompletedQueue);
   }

   @Bean
   public Binding paymentBinding(DirectExchange paymentsExchange, Queue paymentCompletedQueue) {
        return BindingBuilder
                .bind(paymentCompletedQueue)
                .to(paymentsExchange).with(paymentCompletedRoutingKey);
   }

   @Bean
   public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
   }

   @Bean
   public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
   }

   @Bean
   public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
   }

   @Bean
   public ApplicationListener<ApplicationReadyEvent> listener(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
   }
}
