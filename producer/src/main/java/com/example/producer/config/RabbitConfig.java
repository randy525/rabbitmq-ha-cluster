package com.example.producer.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public ConnectionFactory connectionFactory(RabbitProperties props) {
        RabbitProperties.Connection cProps = props.connection();
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername(cProps.username());
        connectionFactory.setPassword(cProps.password());
        connectionFactory.setAddresses(cProps.addresses());
        connectionFactory.setChannelCacheSize(cProps.channelCacheSize());
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate template(RabbitProperties props) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory(props));
        rabbitTemplate.setExchange(props.producer().exchange());
        rabbitTemplate.setRoutingKey(props.producer().routingKey());
        return rabbitTemplate;
    }

}
