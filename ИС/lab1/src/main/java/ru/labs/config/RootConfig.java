package ru.labs.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "ru.labs.service",
        "ru.labs.dao"
})
public class RootConfig {
    // Позже здесь появятся бины DataSource, EntityManagerFactory,
    // TransactionManager
}
