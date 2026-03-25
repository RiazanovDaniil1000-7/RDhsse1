package com.example.controller;

import com.example.dto.TaskResponseDto;
import com.example.service.FavoritesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoritesController {

    private final FavoritesService favoritesService;

    @Value("${app.api.version:2.0.0}")
    private String apiVersion;

    @PostMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> addToFavorites(
            @PathVariable Long taskId,
            HttpSession session) {

        boolean added = favoritesService.addToFavorites(taskId, session);

        if (!added) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("x-API-Version", apiVersion)
                    .body(Map.of("error", "Task not found with id: " + taskId));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Task added to favorites");
        response.put("taskId", taskId);
        response.put("favoritesCount", favoritesService.getFavoritesCount(session));

        return ResponseEntity.ok()
                .header("x-API-Version", apiVersion)
                .body(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> removeFromFavorites(
            @PathVariable Long taskId,
            HttpSession session) {

        boolean removed = favoritesService.removeFromFavorites(taskId, session);

        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("x-API-Version", apiVersion)
                    .body(Map.of("error", "Task not found in favorites or task doesn't exist"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Task removed from favorites");
        response.put("taskId", taskId);
        response.put("favoritesCount", favoritesService.getFavoritesCount(session));

        return ResponseEntity.ok()
                .header("x-API-Version", apiVersion)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
        List<TaskResponseDto> favorites = favoritesService.getFavoriteTasks(session);

        return ResponseEntity.ok()
                .header("x-API-Version", apiVersion)
                .header("x-Total-Count", String.valueOf(favorites.size()))
                .body(favorites);
    }

    @GetMapping("/check/{taskId}")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @PathVariable Long taskId,
            HttpSession session) {

        boolean isFavorite = favoritesService.isFavorite(taskId, session);

        Map<String, Object> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("isFavorite", isFavorite);

        return ResponseEntity.ok()
                .header("x-API-Version", apiVersion)
                .body(response);
    }
}