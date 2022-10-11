package com.cdek.storage.config;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Bean для ObjectMapper создается автоматически {@link JacksonAutoConfiguration},
 * при этом стоит условие @ConditionalOnMissingBean, поэтому явно прописывать создание бина не имеет смысла,
 * конфигурироваться бин будет в application.properties.
 * Конфигурация бд, myBatis, flyway через автоконфигурации {@link DataSourceAutoConfiguration},
 * {@link MybatisAutoConfiguration}, {@link FlywayAutoConfiguration}
 */

@Configuration
public class ApplicationConfig {

}
