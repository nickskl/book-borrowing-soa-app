package ru.nikolay.service.gateway.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    Queue confirmationQueue() {
        return new Queue("confirmationQueue");
    }

    @Bean
    Queue errorQueue() {
        return new Queue("errorQueue");
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("rsoi.statistics.status");
    }

    @Bean
    public Binding errorBinding(DirectExchange directExchange, Queue errorQueue) {
        return BindingBuilder.bind(errorQueue).to(directExchange).with("error");
    }

    @Bean
    public Binding confirmationBinding(DirectExchange directExchange, Queue confirmationQueue) {
        return BindingBuilder.bind(confirmationQueue).to(directExchange).with("confirmation");
    }

}
