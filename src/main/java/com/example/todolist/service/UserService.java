package com.example.todolist.service;

import com.example.todolist.dto.request.LoginRequest;
import com.example.todolist.dto.request.UserRequest;
import com.example.todolist.dto.response.UserResponse;
import com.example.todolist.exception.DuplicateDataException;
import com.example.todolist.model.User;
import com.example.todolist.repository.UserRepository;
import com.example.todolist.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with username: "+ username));
        return new CustomUserDetails(user);
    }

    @Transactional
    public UserResponse registerUser(UserRequest userRequest){
        Optional<User> existingUser = userRepository.findByUsername(userRequest.getUsername());
        if (existingUser.isPresent()) {
            throw new DuplicateDataException("User already exists with username: " + userRequest.getUsername());}

        Optional<User> existingEmail = userRepository.findByEmail(userRequest.getEmail());
        if (existingEmail.isPresent()) {
            throw new DuplicateDataException("Email already registered: " + userRequest.getEmail());}

        if (userRequest.getPassword().length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");}

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(Optional.ofNullable(userRequest.getRole()).orElse("USER"));
        user = userRepository.save(user);
        User register = userRepository.save(user);
        return convertToResponse(register);
    }

    public UserResponse loginUser2(LoginRequest loginRequest){
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (userOptional.isEmpty()){
            throw new RuntimeException("User not found with username: "+ loginRequest.getUsername());}
        User user = userOptional.get();
        if (!user.getPassword().equals(loginRequest.getPassword())) {
                throw new RuntimeException("Invalid password");}
        return convertToResponse(user);
    }

    public UserResponse loginUser(LoginRequest loginRequest){
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (userOptional.isEmpty()){
            throw new RuntimeException("User not found with username: "+ loginRequest.getUsername());}
        User user = userOptional.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");}
        return convertToResponse(user);
    }

    public UUID getUserIdByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get().getId(); // Mengembalikan userId
        } else {
            throw new RuntimeException("User not found with username: " + username);
        }
    }

    private UserResponse convertToResponse(User user){
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        return userResponse;
    }
}
