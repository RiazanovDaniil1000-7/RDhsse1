package com.example.controller;

import com.example.dto.TaskCreateDto;
import com.example.dto.TaskResponseDto;
import com.example.dto.TaskUpdateDto;
import com.example.dto.ErrorResponse;
import com.example.exception.TaskNotFoundException;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import com.example.service.FavoritesService;
import com.example.service.TaskService;
import com.example.service.TaskStatisticsService;
import com.example.validation.OnCreate;
import com.example.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Управление задачами")
public class TaskController {

    private final TaskService taskService;
    private final TaskStatisticsService taskStatisticsService;
    private final TaskMapper taskMapper;
    private final FavoritesService favoritesService;

    @Value("${app.api.version:2.0.0}")
    private String apiVersion;

    @Operation(summary = "Получить список всех задач",
            description = "Возвращает список всех задач. Можно добавить информацию об избранном через параметр withFavorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка задач",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(
            @Parameter(description = "Включить информацию об избранном")
            @RequestParam(required = false) Boolean withFavorites,
            HttpSession session) {

        List<Task> tasks = taskService.getAll();
        List<TaskResponseDto> taskDtos = tasks.stream()
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .header("x-API-Version", apiVersion)
                .header("x-Total-Count", String.valueOf(tasks.size()))
                .body(taskDtos);
    }

    @Operation(summary = "Получить задачу по ID",
            description = "Возвращает задачу с указанным идентификатором")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Task not found with id: 1\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getById(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable Long id) {

        Task task = taskService.getById(id);
        if (task == null) {
            throw new TaskNotFoundException(id);
        }

        return ResponseEntity.ok()
                .header("x-API-Version", apiVersion)
                .body(taskMapper.toResponseDto(task));
    }

    @Operation(summary = "Создать новую задачу",
            description = "Создает задачу с переданными данными")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задача успешно создана",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<TaskResponseDto> addTask(
            @Validated(OnCreate.class) @RequestBody TaskCreateDto createDto) {

        Task task = taskMapper.toEntity(createDto);
        Task savedTask = taskService.addTask(task);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("x-API-Version", apiVersion)
                .body(taskMapper.toResponseDto(savedTask));
    }

    @Operation(summary = "Обновить задачу",
            description = "Обновляет существующую задачу. Можно обновлять как все поля, так и отдельные")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно обновлена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверные данные (например, dueDate раньше createdAt)")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody TaskUpdateDto updateDto) {

        Task existingTask = taskService.getById(id);
        if (existingTask == null) {
            throw new TaskNotFoundException(id);
        }

        if (updateDto.getDueDate() != null &&
                updateDto.getDueDate().isBefore(existingTask.getCreatedAt().toLocalDate())) {
            throw new IllegalArgumentException("Due date cannot be before creation date");
        }

        taskMapper.updateEntity(updateDto, existingTask);
        Task updatedTask = taskService.updateTask(id, existingTask);

        return ResponseEntity.ok()
                .header("x-API-Version", apiVersion)
                .body(taskMapper.toResponseDto(updatedTask));
    }

    @Operation(summary = "Удалить задачу",
            description = "Удаляет задачу с указанным ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable Long id) {

        Task existingTask = taskService.getById(id);
        if (existingTask == null) {
            throw new TaskNotFoundException(id);
        }

        taskService.deleteTask(id);

        return ResponseEntity.noContent()
                .header("x-API-Version", apiVersion)
                .build();
    }

    @Operation(summary = "Получить статистику",
            description = "Возвращает статистику по репозиториям задач")
    @GetMapping("/stats")
    public ResponseEntity<String> getStatistics() {
        return ResponseEntity.ok()
                .header("x-API-Version", apiVersion)
                .body(taskStatisticsService.getComparisonReport());
    }
}