package com.example.producer.producer;

import com.example.producer.config.RabbitProperties;
import jakarta.annotation.PostConstruct;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

@Component
public class IdsProducer {

    private static final Logger logger = Logger.getLogger("Producer");
    private static final AtomicBoolean running = new AtomicBoolean(true);

    private final RabbitTemplate template;
    private final RabbitProperties properties;

    public IdsProducer(RabbitTemplate template, RabbitProperties properties) {
        this.template = template;
        this.properties = properties;
    }

    @PostConstruct
    public void send() throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown signal received");
            running.set(false);
        }));
        Random rnd = new Random();
        int delay = 1000 / properties.producer().messagesPerSecond();
        while (running.get()) {
            int id = rnd.nextInt(100000);
            template.convertAndSend(id);
            logger.info("Message " + id + " was sent");
            Thread.sleep(delay);
        }
    }
}
