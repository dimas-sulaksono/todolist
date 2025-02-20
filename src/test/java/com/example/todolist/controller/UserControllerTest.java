package com.example.todolist.controller;

import com.example.todolist.dto.request.UserRequest;
import com.example.todolist.dto.response.ApiResponse;
import com.example.todolist.dto.response.UserResponse;
import com.example.todolist.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private UserRequest registerRequest;
    private UserResponse userResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        registerRequest = new UserRequest();
        userResponse = new UserResponse();
    }

    @Test
    public void testRegisterUser_success() {
        when(userService.registerUser(any(UserRequest.class))).thenReturn(userResponse);

        ResponseEntity<?> response = userController.registerUser(registerRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getBody()).isEqualTo(new ApiResponse<>(200, userResponse));

        verify(userService, times(1)).registerUser(registerRequest);

    }

}
