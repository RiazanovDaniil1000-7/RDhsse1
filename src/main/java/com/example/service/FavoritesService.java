package com.example.service;

import com.example.dto.TaskResponseDto;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritesService {

    private static final String FAVORITES_SESSION_KEY = "favoriteTaskIds";

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @SuppressWarnings("unchecked")
    private Set<Long> getFavoriteIdsFromSession(HttpSession session) {
        Set<Long> favorites = (Set<Long>) session.getAttribute(FAVORITES_SESSION_KEY);
        if (favorites == null) {
            favorites = new HashSet<>();
            session.setAttribute(FAVORITES_SESSION_KEY, favorites);
            log.debug("Created new favorites set in session");
        }
        return favorites;
    }

    public boolean addToFavorites(Long taskId, HttpSession session) {
        // Проверяем, существует ли задача
        Task task = taskService.getById(taskId);
        if (task == null) {
            log.warn("Task with id {} not found", taskId);
            return false;
        }

        Set<Long> favorites = getFavoriteIdsFromSession(session);
        boolean added = favorites.add(taskId);

        if (added) {
            log.info("Task {} added to favorites. Total favorites: {}", taskId, favorites.size());
        }

        return added;
    }

    public boolean removeFromFavorites(Long taskId, HttpSession session) {
        Set<Long> favorites = getFavoriteIdsFromSession(session);
        boolean removed = favorites.remove(taskId);

        if (removed) {
            log.info("Task {} removed from favorites. Total favorites: {}", taskId,
                    favorites.size());
        }

        return removed;
    }

    public List<TaskResponseDto> getFavoriteTasks(HttpSession session) {
        Set<Long> favoriteIds = getFavoriteIdsFromSession(session);

        return favoriteIds.stream()
                .map(taskService::getById)
                .filter(Objects::nonNull)
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public boolean isFavorite(Long taskId, HttpSession session) {
        Set<Long> favorites = getFavoriteIdsFromSession(session);
        return favorites.contains(taskId);
    }

    public int getFavoritesCount(HttpSession session) {
        Set<Long> favorites = getFavoriteIdsFromSession(session);
        return favorites.size();
    }

    public void clearFavorites(HttpSession session) {
        session.removeAttribute(FAVORITES_SESSION_KEY);
        log.info("All favorites cleared from session");
    }
}