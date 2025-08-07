package com.example.producer;

import jakarta.annotation.PostConstruct;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.logging.Logger;

@EnableRabbit
@SpringBootApplication
public class ProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);
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
	public RabbitTemplate template() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
		rabbitTemplate.setExchange("ex.ha.example");
		rabbitTemplate.setRoutingKey("test");
		return rabbitTemplate;
	}
}

@Component
class Producer {

	private static final Logger logger = Logger.getLogger("Producer");

	private final RabbitTemplate template;

    public Producer(RabbitTemplate template) {
		this.template = template;
	}

    @PostConstruct
	public void send() {
		for (int i = 0; i < 1000; i++) {
			int id = new Random().nextInt(100000);
			template.convertAndSend(id);
		}
		logger.info("Sending completed.");
	}
}
