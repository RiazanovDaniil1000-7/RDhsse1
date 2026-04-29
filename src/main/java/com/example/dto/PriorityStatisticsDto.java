package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriorityStatisticsDto {
    private String priority;
    private Integer count;
}
