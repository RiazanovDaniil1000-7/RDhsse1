package com.example.service;

import com.example.dto.PriorityStatisticsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskStatisticsJdbcService {

    private final JdbcTemplate jdbcTemplate;

    public List<PriorityStatisticsDto> getTasksCountByPriority() {
        String sql = "SELECT priority, COUNT(*) as task_count " +
                "FROM tasks " +
                "GROUP BY priority";

        RowMapper<PriorityStatisticsDto> rowMapper = (rs, rowNum) -> new PriorityStatisticsDto(
                rs.getString("priority"),
                rs.getInt("task_count")
        );

        return jdbcTemplate.query(sql, rowMapper);
    }
}