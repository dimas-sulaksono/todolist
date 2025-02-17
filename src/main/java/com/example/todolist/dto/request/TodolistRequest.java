package com.example.todolist.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class TodolistRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    @Size(max = 120)
    private String username;
    private Long categoryId;
    private Boolean isCompleted;
    private LocalDateTime deletedAt;
    private MultipartFile imagePath;
}
