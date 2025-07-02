package com.piggymetrics.course.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COURSE_EXCHANGE = "course.exchange";
    public static final String COURSE_QUEUE = "course.queue";
    public static final String COURSE_CREATED_ROUTING_KEY = "course.created";

    @Bean
    public Exchange courseExchange() {
        return ExchangeBuilder.directExchange(COURSE_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue courseQueue() {
        return QueueBuilder.durable(COURSE_QUEUE)
                .build();
    }

    @Bean
    public Binding courseBinding() {
        return BindingBuilder
                .bind(courseQueue())
                .to(courseExchange())
                .with(COURSE_CREATED_ROUTING_KEY)
                .noargs();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}