package com.example.todolist.service;

import com.example.todolist.dto.request.LoginRequest;
import com.example.todolist.dto.request.UserRequest;
import com.example.todolist.dto.response.UserResponse;
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
        if(userRepository.findByUsername(userRequest.getUsername()).isPresent()){
            throw new RuntimeException("username already eexists!");
        }

        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists!");
        }

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
