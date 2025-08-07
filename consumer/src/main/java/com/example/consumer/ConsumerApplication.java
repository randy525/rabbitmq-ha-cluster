package com.example.consumer;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Logger;

@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}

@Component
class Consumer {

    private static Logger logger = Logger.getLogger("Consumer");

    private Long timestamp;

    @RabbitListener(queues = "q.ha.example")
    public void onMessage(String id) {
        if (timestamp == null)
            timestamp = System.currentTimeMillis();
        logger.info((System.currentTimeMillis() - timestamp) + " : " + id);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setAddresses("localhost:5672,localhost:5673,localhost:5674");
        connectionFactory.setChannelCacheSize(10);
        return connectionFactory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrentConsumers(10);
        factory.setMaxConcurrentConsumers(20);
        return factory;
    }

    @Bean
    public Queue queue() {
        return new Queue("q.ha.example",
                         true,
                         false,
                         false,
                         Map.of("x-queue-type", "quorum")
        );
    }
}
