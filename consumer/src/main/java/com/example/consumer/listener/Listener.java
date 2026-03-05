package com.example.consumer.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class Listener {

    private static final Logger logger = Logger.getLogger("Consumer");

    private Long timestamp;

    @RabbitListener(queues = "q.ha.example")
    public void onMessage(String id) {
        if (timestamp == null) {
            timestamp = System.currentTimeMillis();
        }
        logger.info((System.currentTimeMillis() - timestamp) + " : " + id);
    }
}