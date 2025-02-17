package com.example.todolist.service;

import com.example.todolist.dto.request.UserRequest;
import com.example.todolist.dto.response.UserResponse;
import com.example.todolist.exception.DataNotFoundException;
import com.example.todolist.exception.DuplicateDataException;
import com.example.todolist.model.User;
import com.example.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<UserResponse> findAll() {
        try{
            return userRepository.findAll()
                    .stream()
                    .map(this::convertToResponse)
                    .toList();
        } catch (Exception e){
            throw new RuntimeException("Failed to get data users");
        }
    }

    public UserResponse create(UserRequest userRequest) {
        try {
            User user = new User();
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
            user.setPassword(userRequest.getPassword());
            user.setRole(userRequest.getRole());
            user = userRepository.save(user);
            return convertToResponse(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    public UserResponse updateUser(UUID id, UserRequest userRequest) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("User with id " + id + " not found"));
            if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
                throw new DuplicateDataException("Username " + userRequest.getUsername() + " already exists");
            } else {
                user.setUsername(userRequest.getUsername());
                user.setEmail(userRequest.getEmail());
                user.setPassword(userRequest.getPassword());
                user.setRole(userRequest.getRole());
                user = userRepository.save(user);
                return convertToResponse(user);
            }
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    public void deleteUser(UUID id) {
        try {
            if (!userRepository.existsById(id)) {
                throw new RuntimeException("User with id " + id + " not found");
            }
            userRepository.deleteById(id);
        } catch (DataNotFoundException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUuid(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

}
