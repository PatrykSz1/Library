package com.testlibrary.testlibrary.configuration;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    public static String QUEUE_NAME = "library_books";
    public static String LOG_QUEUE_NAME = "log_queue";
    public static String EXCHANGE = "library_exchange";
    public static String ROUTING_KEY = "library_routingKey";

    @Bean
    public Queue createQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Queue logQueue() {
        return new Queue(LOG_QUEUE_NAME);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(@Qualifier("createQueue") Queue queue, TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding logBinding(@Qualifier("logQueue") Queue logQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(logQueue)
                .to(exchange)
                .with("log_routing_key");
    }
}