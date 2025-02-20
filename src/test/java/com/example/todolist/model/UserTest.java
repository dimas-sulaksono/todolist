package com.example.todolist.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserTest {
    private User user;

    @BeforeEach // method ini akan dijalankan sebelum setiap test case dijalankan
    public void setUp() { // setup data yang akan dipake untuk unit test
        MockitoAnnotations.openMocks(this); // inisialisasi Mockito, untuk bikin data tiruan
        user = new User();
        user.setUsername("admin");
        user.setEmail("admin@mail.com");
        user.setPassword("123");
        user.setRole("ADMIN");
    }

    // unit test untuk create user
    @Test // anotasi untuk membuat unit test
    public void testCreateUser() {
        user.onCreate(); // untuk bikin data tanggal
        user.setId(UUID.randomUUID()); // untuk generate uuid
        // assert buat bikin pernyataan
        // cara pertama pakai assertEquals
        assertNotNull(user.getId()); // assert untuk memastikan id tidak null
        assertEquals("admin", user.getUsername()); // assert untuk memastikan username sama dengan yang disetup
        assertEquals("admin@mail.com", user.getEmail());
        assertEquals("123", user.getPassword());
        assertEquals("ADMIN", user.getRole());
        // cara kedua pakai assertThat
        assertThat(user.getCreatedAt()).isNotNull(); // assert untuk memastikan created at tidak null
        assertThat(user.getUpdatedAt()).isNotNull();
        assertThat(user.getCreatedAt()).isEqualTo(user.getUpdatedAt());
    }


}
