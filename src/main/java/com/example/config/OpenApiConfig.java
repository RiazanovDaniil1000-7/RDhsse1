package com.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.api.version:2.0.0}")
    private String apiVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("To-Do List API")
                        .version(apiVersion)
                        .description("""
                                API для управления задачами в To-Do List приложении.
                                
                                ## Возможности:
                                * Создание, чтение, обновление, удаление задач
                                * Загрузка и скачивание файлов к задачам
                                * Избранные задачи (на основе сессии)
                                * Настройки отображения (на основе cookies)
                                * Полная валидация входных данных
                                * Единый формат ошибок
                                """)
                        .contact(new Contact()
                                .name("To-Do List Team")
                                .email("support@todo-app.com")
                                .url("https://github.com/todo-app"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.todo-app.com")
                                .description("Production Server")
                ));
    }
}