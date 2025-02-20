package com.example.todolist.controller;

import com.example.todolist.Util.JwtUtil;
import com.example.todolist.dto.request.LoginRequest;
import com.example.todolist.dto.request.UserRequest;
import com.example.todolist.dto.response.ApiResponse;
import com.example.todolist.dto.response.UserResponse;
import com.example.todolist.exception.DuplicateDataException;
import com.example.todolist.model.User;
import com.example.todolist.repository.UserRepository;
import com.example.todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest){
        try {
            UserResponse userResponse = userService.registerUser(userRequest);
            return ResponseEntity.ok(new ApiResponse<>(200, userResponse));
        } catch (DuplicateDataException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(409, e.getMessage()));
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){
        try {
            UserResponse userResponse = userService.loginUser(loginRequest);
            String token = jwtUtil.generateToken(userResponse.getUsername());
            return ResponseEntity.ok(new ApiResponse<String>(200, token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(401, "invalid username or password"));
        }
    }

    @PostMapping("/login2")
    public ResponseEntity<?> loginUser2(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(401, "Invalid username or password"));
            }

            User user = userOptional.get();

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(401, "Invalid username or password"));
            }

            UserResponse userResponse = userService.loginUser(loginRequest);
            String token = jwtUtil.generateToken(userResponse.getUsername());

            return ResponseEntity.ok(new ApiResponse<>(200, token));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(401, "Invalid username or password"));
        }
    }

    @GetMapping("/id/{username}")
    public ResponseEntity<?> getUserId(@PathVariable String username) {
        try {
            String userId = String.valueOf(userService.getUserIdByUsername(username));
            return ResponseEntity.ok().body(new ApiResponse<>(200, userId));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new ApiResponse<>(404, "User not found"));
        }
    }

}
