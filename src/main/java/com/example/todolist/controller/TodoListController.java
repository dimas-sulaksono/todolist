package com.example.todolist.controller;

import com.example.todolist.dto.request.TodolistRequest;
import com.example.todolist.dto.response.ApiResponse;
import com.example.todolist.dto.response.PaginatedResponse;
import com.example.todolist.dto.response.TodolistResponse;
import com.example.todolist.exception.DataNotFoundException;
import com.example.todolist.exception.DuplicateDataException;
import com.example.todolist.service.TodolistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/todolist")
public class TodoListController {
    @Autowired
    private TodolistService todolistService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createTodoList(@ModelAttribute @Valid @RequestBody TodolistRequest request) {
        try {
            TodolistResponse response = todolistService.createTodoList(request);
            return ResponseEntity
                    .ok(new ApiResponse<>(200, response));
        } catch (DuplicateDataException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(409, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTodoListById(@PathVariable("id") Long id) {
        try {
            TodolistResponse todolistResponse = todolistService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Todolist with id " + id + " not found"));
            return ResponseEntity
                    .ok(new ApiResponse<>(200, todolistResponse));
        } catch (DataNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Failed to find todolist: "+ e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTodoList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<TodolistResponse> todolists = todolistService.findAll(page, size);
            return ResponseEntity
                    .ok(new PaginatedResponse<>(200, todolists));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to retrieve todolist: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateTodoList(@Valid @PathVariable("id") Long id, @ModelAttribute @RequestBody TodolistRequest request) {
        try {
            TodolistResponse todolistResponse = todolistService.updateTodoList(id, request);
            return ResponseEntity
                    .ok(new ApiResponse<>(200, todolistResponse));
        } catch (DataNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update todolist: "+ e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodoList(@PathVariable("id") Long id) {
        try {
            todolistService.deleteTodoList(id);
            return ResponseEntity
                    .ok(new ApiResponse<>(HttpStatus.OK.value(), "Todolist deleted successfully"));
        } catch (DataNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to delete todolist: "+ e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TodolistResponse>>> searchByTitle(@RequestParam String title) {
        List<TodolistResponse> todolists = todolistService.searchByTitle(title);
        return ResponseEntity.ok(new ApiResponse<>(200, todolists));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<TodolistResponse>>> filterByCategory(@RequestParam Long categoryId) {
        List<TodolistResponse> todolists = todolistService.filterByCategory(categoryId);
        return ResponseEntity.ok(new ApiResponse<>(200, todolists));
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<ApiResponse<List<TodolistResponse>>> getTodolistByUserId(@PathVariable UUID userId) {
        List<TodolistResponse> todolists = todolistService.findByUserId(userId);
        return ResponseEntity
                .ok(new ApiResponse<>(200, todolists));
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename){
        try {
            Path filePath = Paths.get("src/main/resources/static/images/", filename);
            byte[] imageContent = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageContent);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
