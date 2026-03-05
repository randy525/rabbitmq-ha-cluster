package com.example.consumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(RabbitProperties props) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory(props));
        factory.setConcurrentConsumers(props.connection().concurrentConsumers());
        factory.setMaxConcurrentConsumers(props.connection().maxConcurrentConsumers());
        return factory;
    }

    @Bean
    public DirectExchange exchange(RabbitProperties props) {
        RabbitProperties.ExchangeProps eProps = props.exchange();
        return ExchangeBuilder.directExchange(eProps.name())
                .durable(eProps.durable())
                .build();
    }

    @Bean
    public Queue queue(RabbitProperties props) {
        RabbitProperties.QueueProps qProps = props.queue();
        return new Queue(qProps.name(),
                         qProps.durable(),
                         qProps.exclusive(),
                         qProps.autoDelete(),
                         qProps.arguments()
        );
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange, RabbitProperties props) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(props.exchange().routingKey());
    }

}
