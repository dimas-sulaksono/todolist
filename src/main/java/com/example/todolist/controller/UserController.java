package com.example.todolist.controller;

import com.example.todolist.dto.request.UserRequest;
import com.example.todolist.dto.response.ApiResponse;
import com.example.todolist.dto.response.UserResponse;
import com.example.todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/todolist/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUser() {
        try {
            List<UserResponse> response = userService.findAll();
            return ResponseEntity
                    .status(HttpStatus.OK.value())
                    .body(new ApiResponse<>(HttpStatus.OK.value(), response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody UserRequest request) {
        try {
            UserResponse response = userService.create(request);
            return ResponseEntity
                    .status(HttpStatus.OK.value())
                    .body(new ApiResponse<>(HttpStatus.OK.value(), response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") UUID id, @RequestBody UserRequest request) {
        try {
            UserResponse response = userService.updateUser(id, request);
            return ResponseEntity
                    .status(HttpStatus.OK.value())
                    .body(new ApiResponse<>(HttpStatus.OK.value(), response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity
                    .status(HttpStatus.OK.value())
                    .body(new ApiResponse<>(HttpStatus.OK.value(), "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to delete user: " + e.getMessage()));
        }
    }



}
