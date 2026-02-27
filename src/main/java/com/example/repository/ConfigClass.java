package com.example.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigClass {

    @Bean
    public TaskRepository stubTaskRepository() {
        return new StubTaskRepository();
    }
}
