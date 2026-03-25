package com.example.controller;

import com.example.dto.TaskCreateDto;
import com.example.dto.TaskResponseDto;
import com.example.dto.TaskUpdateDto;
import com.example.exception.TaskNotFoundException;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import com.example.service.FavoritesService;
import com.example.service.TaskService;
import com.example.service.TaskStatisticsService;
import com.example.validation.OnCreate;
import com.example.validation.OnUpdate;
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
public class TaskController {

    private final TaskService taskService;
    private final TaskStatisticsService taskStatisticsService;
    private final TaskMapper taskMapper;
    private final FavoritesService favoritesService;

    @Value("${app.api.version:2.0.0}")
    private String apiVersion;

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(
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

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getById(@PathVariable Long id) {
        Task task = taskService.getById(id);
        if (task == null) {
            throw new TaskNotFoundException(id);
        }

        return ResponseEntity.ok()
                .header("x-API-Version", apiVersion)
                .body(taskMapper.toResponseDto(task));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> addTask(
            @Validated(OnCreate.class) @RequestBody TaskCreateDto createDto) {

        Task task = taskMapper.toEntity(createDto);
        Task savedTask = taskService.addTask(task);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("x-API-Version", apiVersion)
                .body(taskMapper.toResponseDto(savedTask));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Task existingTask = taskService.getById(id);
        if (existingTask == null) {
            throw new TaskNotFoundException(id);
        }

        taskService.deleteTask(id);

        return ResponseEntity.noContent()
                .header("x-API-Version", apiVersion)
                .build();
    }

    @GetMapping("/stats")
    public ResponseEntity<String> getStatistics() {
        return ResponseEntity.ok()
                .header("x-API-Version", apiVersion)
                .body(taskStatisticsService.getComparisonReport());
    }
}