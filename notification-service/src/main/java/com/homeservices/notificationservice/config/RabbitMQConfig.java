package com.homeservices.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class RabbitMQConfig {

    public static final String BOOKING_CONFIRMATION_QUEUE = "booking.confirmation";
    public static final String BOOKING_FAILURE_QUEUE = "booking.failure";
    public static final String PAYMENT_FAILED_QUEUE = "payment.failed.queue";
    public static final String SERVICE_REQUEST_QUEUE = "service.request.notification";

    public static final String BOOKING_EXCHANGE = "booking.exchange";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    public static final String CONFIRMATION_ROUTING_KEY = "booking.confirmed";
    public static final String FAILURE_ROUTING_KEY = "booking.failed";
    public static final String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";
    public static final String SERVICE_REQUEST_ROUTING_KEY = "service.request.matched";

    @Bean
    public Queue confirmationQueue() {
        return new Queue(BOOKING_CONFIRMATION_QUEUE, true);
    }

    @Bean
    public Queue failureQueue() {
        return new Queue(BOOKING_FAILURE_QUEUE, true);
    }

    @Bean
    public Queue paymentFailedQueue() {
        return new Queue(PAYMENT_FAILED_QUEUE, true);
    }

    @Bean
    public Queue serviceRequestQueue() {
        return new Queue(SERVICE_REQUEST_QUEUE, true);
    }

    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(BOOKING_EXCHANGE);
    }

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Binding confirmationBinding() {
        return BindingBuilder
                .bind(confirmationQueue())
                .to(bookingExchange())
                .with(CONFIRMATION_ROUTING_KEY);
    }

    @Bean
    public Binding failureBinding() {
        return BindingBuilder
                .bind(failureQueue())
                .to(bookingExchange())
                .with(FAILURE_ROUTING_KEY);
    }

    @Bean
    public Binding paymentFailedBinding() {
        return BindingBuilder
                .bind(paymentFailedQueue())
                .to(paymentExchange())
                .with(PAYMENT_FAILED_ROUTING_KEY);
    }

    @Bean
    public Binding serviceRequestBinding() {
        return BindingBuilder
                .bind(serviceRequestQueue())
                .to(bookingExchange())
                .with(SERVICE_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");
        classMapper.setDefaultType(HashMap.class);
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }
}