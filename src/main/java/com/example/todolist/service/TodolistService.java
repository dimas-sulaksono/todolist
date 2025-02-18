package com.example.todolist.service;

import com.example.todolist.dto.request.TodolistRequest;
import com.example.todolist.dto.response.TodolistResponse;
import com.example.todolist.exception.DataNotFoundException;
import com.example.todolist.model.Category;
import com.example.todolist.model.Todolist;
import com.example.todolist.model.User;
import com.example.todolist.repository.CategoryRepository;
import com.example.todolist.repository.TodolistRepository;
import com.example.todolist.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TodolistService {
    @Autowired
    private TodolistRepository todolistRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${file.IMAGE_DIR}")
    private String imageDirectory;

    private static long maxFileSize = 5 * 1024 * 1024; //5MB

    private static String[] allowedImageTypes = {"image/jpeg", "image/png", ".image/jpg"}; //format yang diizinkan

    // fungsi untuk validasi file
    public String saveImageFile(MultipartFile file) throws IOException{
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty or not provided");
        }

        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("File size exceeds the limit of " + maxFileSize / (1024 * 1024) + " MB");
        }

        String fileType = file.getContentType();
        //boolean isValidType = allowedImageTypes.contains(fileType);
        boolean isValidType = false;
        for (String allowedType: allowedImageTypes){
            if(fileType.equals(allowedType)){
                isValidType = true;
                break;
            }
        }

        if (!isValidType) {
            throw new RuntimeException("Invalid file type! Only JPEG, PNG, JPG allowed");
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String originalFileName = file.getOriginalFilename();
        String customFileName = timeStamp + "_todolist_" + originalFileName;

        Path path = Path.of(imageDirectory, customFileName);
        Files.copy(file.getInputStream(), path);

        return customFileName;
    }
    //

    @Transactional
    public TodolistResponse createTodoList(TodolistRequest todolistRequest) {
        try {
            Todolist todoList = new Todolist();
            todoList.setTitle(todolistRequest.getTitle());
            todoList.setDescription(todolistRequest.getDescription());

            User user = userRepository.findByUsername(todolistRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            todoList.setUser(user);
            Category category = categoryRepository.findById(todolistRequest.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            todoList.setCategory(category);

            todoList.setCompleted(todolistRequest.getIsCompleted());

            if (todolistRequest.getImagePath() != null && !todolistRequest.getImagePath().isEmpty()) {
                String imagePath = saveImageFile(todolistRequest.getImagePath());
                todoList.setImagePath(imagePath);
            }

            Todolist savedTodoList = todolistRepository.save(todoList);
            return convertToResponse(savedTodoList);
        } catch (DataNotFoundException e){
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create todolist: " + e.getMessage(), e);
        }
    }

    public Optional<TodolistResponse> findById(Long id) {
        try {
            return todolistRepository.findByIdAndDeletedAtIsNull(id).map(this::convertToResponse);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find todolist by id: " + e.getMessage(), e);
        }
    }

    public Page<TodolistResponse> findAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Todolist> todolists = todolistRepository.findAllByDeletedAtIsNull(pageable);
            return todolists.map(this::convertToResponse);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find all todolists: " + e.getMessage(), e);
        }
    }

    @Transactional
    public TodolistResponse updateTodoList(Long id, TodolistRequest todolistRequest) {
        try{
            Todolist todolist = todolistRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Todolist with ID " + id + " not found"));
            todolist.setTitle(todolistRequest.getTitle());
            todolist.setDescription(todolistRequest.getDescription());

            User user = userRepository.findByUsername(todolistRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            todolist.setUser(user);
            Category category = categoryRepository.findById(todolistRequest.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            todolist.setCategory(category);

            todolist.setCompleted(todolistRequest.getIsCompleted());

            // validasi file
            if (todolistRequest.getImagePath() != null && !todolistRequest.getImagePath().isEmpty()) {
                String imagePath = saveImageFile(todolistRequest.getImagePath());
                todolist.setImagePath(imagePath);
            }

            Todolist updateTodolist = todolistRepository.save(todolist);
            return convertToResponse(updateTodolist);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update todolist: "+ e.getMessage(), e);
        }
    }

    // hard delete
    @Transactional
    public void deleteTodoList(Long id) {
        try {
            if(!todolistRepository.existsById(id)) {
                throw new DataNotFoundException("Todolist with id " + id + " not found");
            }
            todolistRepository.deleteById(id);
        } catch (DataNotFoundException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("Failed to delete todolist" + e.getMessage(), e);
        }
    }

    // softDelete
    @Transactional
    public TodolistResponse softDelete(Long id){
        try{
            Todolist todolist = todolistRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Todolist with ID " + id + " not found"));

            if (todolist.getDeletedAt() != null) {
                throw new RuntimeException("Todolist with ID " + id + " is already deleted");
            } else {
                todolist.setDeletedAt(LocalDateTime.now());
                Todolist updateTodolist = todolistRepository.save(todolist);
                return convertToResponse(updateTodolist);
            }
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update todolist: "+ e.getMessage(), e);
        }
    }


    public List<TodolistResponse> searchByTitle(String title) {
        try {
            return todolistRepository.findByTitleContainingIgnoreCase(title)
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to search by title: " + e.getMessage(), e);
        }
    }

    public List<TodolistResponse> filterByCategory(Long categoryId) {
        try {
            return todolistRepository.findByCategoryId(categoryId)
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to filter by category: " + e.getMessage(), e);
        }
    }

    public List<TodolistResponse> findByUserId(UUID userId) {
        try {
            return todolistRepository.findByUserId(userId)
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find by user id: " + e.getMessage(), e);
        }
    }

    private TodolistResponse convertToResponse(Todolist todolist) {
        TodolistResponse response = new TodolistResponse();

        response.setId(todolist.getId());
        response.setTitle(todolist.getTitle());
        response.setDescription(todolist.getDescription());
        response.setUsername(todolist.getUser().getUsername());
        response.setCategoryId(todolist.getCategory().getId());
        response.setCategoryName(todolist.getCategory().getName());
        response.setCompleted(todolist.isCompleted());
        response.setDeletedAt(todolist.getDeletedAt());
        response.setImagePath(todolist.getImagePath());
        response.setCreatedAt(todolist.getCreatedAt());
        response.setUpdatedAt(todolist.getUpdatedAt());

        return response;
    }
}
