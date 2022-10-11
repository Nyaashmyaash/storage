package com.cdek.storage;

import com.cdek.storage.extension.PostgresDBTestContainerExtension;
import com.cdek.storage.extension.RabbitTestContainerExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.stream.Stream;

@ActiveProfiles("test")
@SpringBootTest
class StorageApplicationTest {
    @RegisterExtension
    static final PostgresDBTestContainerExtension PG_EXTENSION = new PostgresDBTestContainerExtension();
    @RegisterExtension
    static final RabbitTestContainerExtension RABBIT_MQ_EXTENSION = new RabbitTestContainerExtension();

    @DynamicPropertySource
    static void initProperties(DynamicPropertyRegistry registry) {
        Stream.of(PG_EXTENSION, RABBIT_MQ_EXTENSION)
                .forEach(extension -> extension.initProperties(registry));
    }

    @Test
    void test_contextLoads_noException(@Autowired ApplicationContext applicationContext) {
        Assertions.assertThat(applicationContext).isNotNull();
    }
}
