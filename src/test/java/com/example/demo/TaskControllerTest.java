package com.example.demo;

import com.example.controller.TaskController;
import com.example.model.Task;
import com.example.service.TaskService;
import com.example.service.TaskStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = com.example.demo.DemoApplication.class  // Явно указываем главный класс
)
public class TaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private TaskStatisticsService taskStatisticsService;

    private Task testTask;
    private Task testTask2;
    private final String baseUrl = "/tasks";

    @BeforeEach
    void setUp() {
        testTask = new Task(1, "Test Task", "Test Description", false);
        testTask2 = new Task(2, "Another Task", "Another Description", true);

        // Сброс моков перед каждым тестом
        reset(taskService, taskStatisticsService);
    }

    // ==================== GET ALL TASKS ====================
    @Test
    void getAllTasks_ShouldReturnListOfTasks() {
        // Позитивный тест
        List<Task> tasks = Arrays.asList(testTask, testTask2);
        when(taskService.getAll()).thenReturn(tasks);

        ResponseEntity<Task[]> response = restTemplate.getForEntity(baseUrl, Task[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        verify(taskService, times(1)).getAll();
    }

    @Test
    void getAllTasks_WhenNoTasks_ShouldReturnEmptyList() {
        // Негативный тест
        when(taskService.getAll()).thenReturn(Arrays.asList());

        ResponseEntity<Task[]> response = restTemplate.getForEntity(baseUrl, Task[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(0);
        verify(taskService, times(1)).getAll();
    }

    // ==================== GET TASK BY ID ====================
    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        // Позитивный тест
        when(taskService.getById(1)).thenReturn(testTask);

        ResponseEntity<Task> response = restTemplate.getForEntity(baseUrl + "/1", Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1);
        verify(taskService, times(1)).getById(1);
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldReturnNotFound() {
        // Негативный тест
        when(taskService.getById(999)).thenReturn(null);

        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(taskService, times(1)).getById(999);
    }

    // ==================== CREATE TASK ====================
    @Test
    void createTask_WithValidData_ShouldReturnCreated() {
        // Позитивный тест
        Task newTask = new Task(0, "New Task", "New Description", false);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, newTask,
            String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("Задача успешно добавлена");
        verify(taskService, times(1)).addTask(any(Task.class));
    }

    @Test
    void createTask_WithEmptyTitle_ShouldReturnBadRequest() {
        // Негативный тест
        Task invalidTask = new Task(0, "", "Description", false);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, invalidTask,
            String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Title cannot be empty");
        verify(taskService, never()).addTask(any(Task.class));
    }

    // ==================== UPDATE TASK ====================
    @Test
    void updateTask_WhenTaskExists_ShouldReturnUpdatedTask() {
        // Позитивный тест
        Task updatedTask = new Task(1, "Updated Title", "Updated Description", true);
        when(taskService.getById(1)).thenReturn(testTask);
        when(taskService.updateTask(eq(1), any(Task.class))).thenReturn(updatedTask);

        HttpEntity<Task> requestEntity = new HttpEntity<>(updatedTask);
        ResponseEntity<Task> response = restTemplate.exchange(
            baseUrl + "/1",
            HttpMethod.PUT,
            requestEntity,
            Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Updated Title");
        verify(taskService, times(1)).updateTask(eq(1), any(Task.class));
    }

    @Test
    void updateTask_WhenTaskDoesNotExist_ShouldReturnNotFound() {
        // Негативный тест
        Task updatedTask = new Task(999, "Updated", "Description", true);
        when(taskService.getById(999)).thenReturn(null);

        HttpEntity<Task> requestEntity = new HttpEntity<>(updatedTask);
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/999",
            HttpMethod.PUT,
            requestEntity,
            String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(taskService, never()).updateTask(anyInt(), any(Task.class));
    }

    // ==================== DELETE TASK ====================
    @Test
    void deleteTask_WhenTaskExists_ShouldReturnSuccessMessage() {
        // Позитивный тест
        when(taskService.getById(1)).thenReturn(testTask);
        doNothing().when(taskService).deleteTask(1);

        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/1",
            HttpMethod.DELETE,
            null,
            String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Задача удалена.");
        verify(taskService, times(1)).deleteTask(1);
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ShouldReturnNotFound() {
        // Негативный тест
        when(taskService.getById(999)).thenReturn(null);

        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/999",
            HttpMethod.DELETE,
            null,
            String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(taskService, never()).deleteTask(anyInt());
    }

    // ==================== STATISTICS ====================
    @Test
    void getStatistics_ShouldReturnReport() {
        // Позитивный тест
        String expectedReport = """
            Отчет по данным:
            - Основной репозиторий (InMemory): 2 задач
            - Заглушка (Stub): 2 задач
            Итого в системе доступно источников: 2""";

        when(taskStatisticsService.getComparisonReport()).thenReturn(expectedReport);

        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/stats",
            String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedReport);
        verify(taskStatisticsService, times(1)).getComparisonReport();
    }
}