package com.example.dto;

import com.example.model.Priority;
import com.example.validation.OnCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@Schema(description = "DTO для создания новой задачи")
public class TaskCreateDto {

    @Schema(description = "Заголовок задачи",
            example = "Купить продукты",
            minLength = 3,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Title cannot be null", groups = OnCreate.class)
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters", groups = OnCreate.class)
    private String title;

    @Schema(description = "Подробное описание задачи",
            example = "Купить молоко, хлеб, яйца",
            maxLength = 500)
    @Size(max = 500, message = "Description cannot exceed 500 characters", groups = OnCreate.class)
    private String description;

    @Schema(description = "Дата и время выполнения задачи",
            example = "2025-12-31T23:59:59",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @FutureOrPresent(message = "Due date cannot be in the past", groups = OnCreate.class)
    private LocalDateTime dueDate;

    @Schema(description = "Приоритет задачи",
            example = "HIGH",
            allowableValues = {"LOW", "MEDIUM", "HIGH"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Priority cannot be null", groups = OnCreate.class)
    private Priority priority;

    @Schema(description = "Теги для категоризации задачи",
            example = "[\"shopping\", \"home\"]",
            maxLength = 5)
    @Size(max = 5, message = "Cannot have more than 5 tags", groups = OnCreate.class)
    private Set<String> tags;
}