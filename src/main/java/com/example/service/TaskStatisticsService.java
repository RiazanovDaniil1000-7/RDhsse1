package com.example.service;
import com.example.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TaskStatisticsService {

    private final TaskRepository inMemoryRepository;
    private final TaskRepository stubRepository;

    public TaskStatisticsService(TaskRepository inMemoryRepository,
        @Qualifier("stubTaskRepository") TaskRepository stubRepository) {
        this.inMemoryRepository = inMemoryRepository;
        this.stubRepository = stubRepository;
    }

    public String getComparisonReport() {
        int primaryCount = inMemoryRepository.findAll().size();
        int stubCount = stubRepository.findAll().size();

        return String.format(
            """
                Отчет по данным:
                - Основной репозиторий (InMemory): %d задач
                - Заглушка (Stub): %d задач
                Итого в системе доступно источников: 2""",
            primaryCount, stubCount
        );
    }
}

