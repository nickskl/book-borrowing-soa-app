package ru.nikolay.service.statistics.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    Queue bookBorrowedQueue() {
        return new Queue("bookBorrowedQueue");
    }

    @Bean
    Queue bookViewedQueue() {
        return new Queue("bookViewedQueue");
    }

    @Bean
    Queue storageViewedQueue() {
        return new Queue("storageViewedQueue");
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("rsoi.statistics.message");
    }

    @Bean
    public Binding bookBorrowedBinding(DirectExchange directExchange, Queue bookBorrowedQueue) {
        return BindingBuilder.bind(bookBorrowedQueue).to(directExchange).with("bookBorrowed");
    }

    @Bean
    public Binding bookViewedBinding(DirectExchange directExchange, Queue bookViewedQueue) {
        return BindingBuilder.bind(bookViewedQueue).to(directExchange).with("bookViewed");
    }

    @Bean
    public Binding storageViewedBinding(DirectExchange directExchange, Queue storageViewedQueue) {
        return BindingBuilder.bind(storageViewedQueue).to(directExchange).with("storageViewed");
    }
}

