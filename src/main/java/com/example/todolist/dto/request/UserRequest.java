package com.example.todolist.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    private String username;
    private String email;
    private String password;
    private String role;
}
