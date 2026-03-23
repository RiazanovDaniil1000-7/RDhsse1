package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentResponseDto {
    private Long id;
    private String fileName;
    private long size;
    private LocalDateTime uploadedAt;
}