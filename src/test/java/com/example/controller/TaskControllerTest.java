package com.example.controller;

import com.example.dto.TaskCreateDto;
import com.example.model.Priority;
import com.example.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    private TaskCreateDto validTask;

    @BeforeEach
    void setUp() {
        validTask = new TaskCreateDto();
        validTask.setTitle("Integration Test Task");
        validTask.setDescription("Test Description");
        validTask.setDueDate(LocalDate.now().plusDays(1));
        validTask.setPriority(Priority.MEDIUM);
        validTask.setTags(Set.of("test", "integration"));
    }

    @Test
    void createTask_WithValidData_ShouldReturnCreated() {
        HttpEntity<TaskCreateDto> request = new HttpEntity<>(validTask);
        ResponseEntity<String> response = restTemplate.postForEntity("/tasks", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getFirst("x-API-Version")).isEqualTo("2.0.0");
    }

    @Test
    void createTask_WithInvalidTitle_ShouldReturnBadRequest() {
        TaskCreateDto invalidTask = new TaskCreateDto();
        invalidTask.setTitle("Te"); // Слишком короткий
        invalidTask.setPriority(Priority.MEDIUM);

        HttpEntity<TaskCreateDto> request = new HttpEntity<>(invalidTask);
        ResponseEntity<String> response = restTemplate.postForEntity("/tasks", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getAllTasks_ShouldReturnListWithHeaders() {
        ResponseEntity<String> response = restTemplate.getForEntity("/tasks", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst("x-API-Version")).isEqualTo("2.0.0");
        assertThat(response.getHeaders().getFirst("x-Total-Count")).isNotNull();
    }
}