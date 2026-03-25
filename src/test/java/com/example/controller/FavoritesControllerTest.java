package com.example.controller;

import com.example.dto.TaskResponseDto;
import com.example.model.Priority;
import com.example.model.Task;
import com.example.service.FavoritesService;
import com.example.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoritesController.class)
class FavoritesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FavoritesService favoritesService;

    @MockitoBean
    private TaskService taskService;

    private MockHttpSession session;
    private Task testTask;
    private TaskResponseDto testTaskDto;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();

        testTask = new Task(1L, "Test Task", "Description", false,
                LocalDateTime.now(), null, Priority.MEDIUM, null);

        testTaskDto = new TaskResponseDto(1L, "Test Task", "Description",
                false, LocalDateTime.now(), null, Priority.MEDIUM, null);
    }

    @Test
    void addToFavorites_WhenTaskExists_ShouldReturnSuccess() throws Exception {
        when(favoritesService.addToFavorites(eq(1L), any())).thenReturn(true);
        when(favoritesService.getFavoritesCount(any())).thenReturn(1);

        mockMvc.perform(post("/api/favorites/1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Task added to favorites")))
                .andExpect(jsonPath("$.taskId", is(1)))
                .andExpect(jsonPath("$.favoritesCount", is(1)));

        verify(favoritesService).addToFavorites(eq(1L), any());
    }

    @Test
    void addToFavorites_WhenTaskDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(favoritesService.addToFavorites(eq(999L), any())).thenReturn(false);

        mockMvc.perform(post("/api/favorites/999")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("Task not found")));

        verify(favoritesService).addToFavorites(eq(999L), any());
    }

    @Test
    void removeFromFavorites_WhenTaskInFavorites_ShouldReturnSuccess() throws Exception {
        when(favoritesService.removeFromFavorites(eq(1L), any())).thenReturn(true);
        when(favoritesService.getFavoritesCount(any())).thenReturn(0);

        mockMvc.perform(delete("/api/favorites/1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Task removed from favorites")))
                .andExpect(jsonPath("$.taskId", is(1)))
                .andExpect(jsonPath("$.favoritesCount", is(0)));

        verify(favoritesService).removeFromFavorites(eq(1L), any());
    }

    @Test
    void removeFromFavorites_WhenTaskNotInFavorites_ShouldReturnNotFound() throws Exception {
        when(favoritesService.removeFromFavorites(eq(999L), any())).thenReturn(false);

        mockMvc.perform(delete("/api/favorites/999")
                        .session(session))
                .andExpect(status().isNotFound());

        verify(favoritesService).removeFromFavorites(eq(999L), any());
    }

    @Test
    void getFavorites_ShouldReturnListOfFavoriteTasks() throws Exception {
        List<TaskResponseDto> favorites = Arrays.asList(testTaskDto);
        when(favoritesService.getFavoriteTasks(any())).thenReturn(favorites);

        mockMvc.perform(get("/api/favorites")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Task")));

        verify(favoritesService).getFavoriteTasks(any());
    }

    @Test
    void getFavorites_WhenNoFavorites_ShouldReturnEmptyList() throws Exception {
        when(favoritesService.getFavoriteTasks(any())).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/favorites")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(favoritesService).getFavoriteTasks(any());
    }

    @Test
    void checkFavorite_ShouldReturnIsFavoriteStatus() throws Exception {
        when(favoritesService.isFavorite(eq(1L), any())).thenReturn(true);

        mockMvc.perform(get("/api/favorites/check/1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId", is(1)))
                .andExpect(jsonPath("$.isFavorite", is(true)));

        verify(favoritesService).isFavorite(eq(1L), any());
    }
}