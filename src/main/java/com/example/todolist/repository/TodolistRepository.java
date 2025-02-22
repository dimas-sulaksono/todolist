package com.example.todolist.repository;

import com.example.todolist.model.Todolist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TodolistRepository extends JpaRepository<Todolist, Long> {

    // by user id
    List<Todolist> findByUserIdAndDeletedAtIsNull(UUID userId);
    List<Todolist> findByUserIdAndDeletedAtIsNullAndCategoryId(UUID userId, Long categoryId);
    List<Todolist> findByUserIdAndDeletedAtIsNullAndTitleContainingIgnoreCase(UUID userId, String title);

    List<Todolist> findByUserIdAndDeletedAtIsNullAndCategoryIdAndTitleContainingIgnoreCase(UUID userId, Long categoryId, String title);
    Page<Todolist> findByUserIdAndDeletedAtIsNotNull(UUID userId, Pageable pageable);

    // by todolist id
    Optional<Todolist> findByIdAndDeletedAtIsNull(Long id);

    List<Todolist> findByTitleContainingIgnoreCase(String title);

    // all
    Page<Todolist> findAllByDeletedAtIsNull(Pageable pageable);

}
