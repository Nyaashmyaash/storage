package com.cdek.storage.extension;

import org.junit.jupiter.api.extension.Extension;
import org.springframework.test.context.DynamicPropertyRegistry;

public interface TestContainerExtension extends Extension {
    void initProperties(DynamicPropertyRegistry registry);
}
