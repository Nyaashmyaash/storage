package com.cdek.storage.extension;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.RabbitMQContainer;

public class RabbitTestContainerExtension implements TestContainerExtension {

    private static final RabbitMQContainer container = new RabbitMQContainer("rabbitmq:3.8.9-alpine")
            .withExposedPorts(5672, 15672);

    static {
        container.start();
    }

    @Override
    public void initProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.addresses", container::getAmqpUrl);
        registry.add("spring.rabbitmq.username", container::getAdminUsername);
        registry.add("spring.rabbitmq.password", container::getAdminPassword);
    }
}
