package com.example.dto;

import com.example.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для ответа с данными задачи")
public class TaskResponseDto {

    @Schema(description = "Уникальный идентификатор задачи",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Заголовок задачи",
            example = "Купить продукты")
    private String title;

    @Schema(description = "Описание задачи",
            example = "Купить молоко, хлеб, яйца")
    private String description;

    @Schema(description = "Статус выполнения",
            example = "false")
    private boolean completed;

    @Schema(description = "Дата и время создания",
            example = "2026-03-25T10:00:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Дата и время выполнения",
            example = "2025-12-31T23:59:59")
    private LocalDateTime dueDate;

    @Schema(description = "Приоритет задачи",
            example = "HIGH",
            allowableValues = {"LOW", "MEDIUM", "HIGH"})
    private Priority priority;

    @Schema(description = "Теги задачи",
            example = "[\"shopping\", \"home\"]")
    private Set<String> tags;
}