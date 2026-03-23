package com.example.service;
import com.example.repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskStatisticsService {

    private final TaskRepository inMemoryRepository;

    public TaskStatisticsService(TaskRepository inMemoryRepository){
        this.inMemoryRepository = inMemoryRepository;
    }

    public String getComparisonReport() {
        int primaryCount = inMemoryRepository.findAll().size();

        return String.format(
            """
                Отчет по данным:
                - Основной репозиторий (InMemory): %d задач
                - Заглушка (Stub): %d задач
                Итого в системе доступно источников: 2""",
            primaryCount
        );
    }
}

