package com.cdek.storage;

import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class StorageApplication {
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .logStartupInfo(false)
                .bannerMode(Banner.Mode.OFF)
                .sources(StorageApplication.class)
                .web(WebApplicationType.SERVLET)
                .build()
                .run(args);
    }
}
