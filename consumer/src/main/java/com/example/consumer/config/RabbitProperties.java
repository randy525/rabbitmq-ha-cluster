package com.example.consumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "rabbitmq")
public record RabbitProperties(Connection connection, QueueProps queue, ExchangeProps exchange) {

    public record Connection(
            String username,
            String password,
            String addresses,
            Integer channelCacheSize,
            Integer concurrentConsumers,
            Integer maxConcurrentConsumers
    ) {}

    public record QueueProps(
            String name,
            Boolean durable,
            Boolean exclusive,
            Boolean autoDelete,
            Map<String, Object> arguments
    ) {}

    public record ExchangeProps(
            String name,
            Boolean durable,
            String routingKey
    ) {}

}
