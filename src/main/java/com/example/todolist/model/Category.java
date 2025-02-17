package com.example.todolist.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data // anotasi lombok buat bikin getter setter otomatis
@AllArgsConstructor // buat contructor yang membutuhkan field (argument)
@NoArgsConstructor // buat constructor tanpa parameter (argument)
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Todolist> todolists;

    @PrePersist // anotasi buat datetime otomatis ketika pertama kali dibuat
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate // anotasi buat datetime otomatis ketika diupdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
