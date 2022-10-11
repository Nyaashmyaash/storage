package com.cdek.storage.extension;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresDBTestContainerExtension implements TestContainerExtension {

    private static final PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>("postgres:12.5-alpine")
                    .withUrlParam("stringtype", "unspecified")
                    .withDatabaseName("storage")
                    .withInitScript("db_init_test.sql");

    static {
        container.start();
    }

    @Override
    public void initProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> 1);
    }
}
