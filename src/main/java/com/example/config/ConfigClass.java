package com.example.config;

import com.example.repository.StubTaskRepository;
import com.example.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigClass {

    @Bean
    public TaskRepository stubTaskRepository() {
        return new StubTaskRepository();
    }
}
