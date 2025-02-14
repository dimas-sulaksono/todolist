package com.example.todolist.repository;

import com.example.todolist.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // JpaRepository : dependency yang nyediain query ototomatis

    Optional<Category> findByName(String name); // buat find by name>

    @Query(value = "SELECT * from users where name = ?1", nativeQuery = true)
    Optional<Category> findByCustomQuery(String name);


}
