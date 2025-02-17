package com.example.todolist.repository;

import com.example.todolist.model.Todolist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TodolistRepository extends JpaRepository<Todolist, Long> {
    List<Todolist> findByTitleContainingIgnoreCase(String title);
    List<Todolist> findByCategoryId(Long categoryId);
    List<Todolist> findByUserId(UUID userId);
}
