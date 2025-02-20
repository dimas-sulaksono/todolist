package com.example.todolist.service;

import com.example.todolist.dto.request.LoginRequest;
import com.example.todolist.dto.request.UserRequest;
import com.example.todolist.dto.response.UserResponse;
import com.example.todolist.model.User;
import com.example.todolist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserRequest registerRequest;
    private UserResponse userResponse;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        registerRequest = new UserRequest();
        registerRequest.setUsername("admin");
        registerRequest.setEmail("admin@mail.com");
        registerRequest.setPassword("123");
        registerRequest.setRole("ADMIN");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("admin");
        user.setEmail("admin@mail.com");
        user.setPassword("123");
        user.setRole("ADMIN");
    }

    @Test
    public void testRegisterUser_success() {
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("123");
        when(userRepository.save(any(User.class))).thenReturn(user);
        //when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse userResponse = userService.registerUser(registerRequest);

        assertThat(userResponse.getUsername()).isEqualTo(registerRequest.getUsername());
        assertThat(user.getId()).isNotNull();
        assertThat(user.getUsername()).isEqualTo(registerRequest.getUsername());
        assertThat(user.getEmail()).isEqualTo(registerRequest.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testLoginUser_success() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

        UserResponse userResponse = userService.loginUser(loginRequest);

        assertThat(userResponse.getUsername()).isEqualTo(loginRequest.getUsername());
    }

    @Test
    public void testLoginUser2_success() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

        UserResponse userResponse = userService.loginUser2(loginRequest);

        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getUsername()).isEqualTo(user.getUsername());
        assertThat(userResponse.getEmail()).isEqualTo(user.getEmail());
        verify(userRepository, times(1)).findByUsername(loginRequest.getUsername());
    }

}


