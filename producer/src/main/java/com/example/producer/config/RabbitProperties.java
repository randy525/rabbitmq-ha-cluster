package com.example.producer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq")
public record RabbitProperties(Connection connection, Producer producer) {

    public record Connection(
            String username,
            String password,
            String addresses,
            Integer channelCacheSize
    ) {}

    public record Producer(
            String exchange,
            String routingKey,
            Integer messagesPerSecond
    ) {}

}
