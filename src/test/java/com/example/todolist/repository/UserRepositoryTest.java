package com.example.todolist.repository;

import com.example.todolist.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class UserRepositoryTest {
    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("admin");
        user.setEmail("admin@mail.com");
        user.setPassword("123");
        user.setRole("ADMIN");
    }

    @Test
    public void testFindByUsername_success() {
        // when : buat ngasih tau mockito kalau kita mau cari data admin dan mengembalikan data user
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        // nampung object yang ditemukan dari findByUsername
        Optional<User> user = this.userRepository.findByUsername("admin");
        assertTrue(user.isPresent());
        assertEquals("admin", user.get().getUsername());
        assertThat(user.get().getUsername().equals("admin"));
    }

    @Test
    public void testFindByUsername_notFound() {
        when(userRepository.findByUsername("dimas")).thenReturn(Optional.empty());

        Optional<User> foundUser = this.userRepository.findByUsername("dimas");
        assertThat(foundUser.isPresent()).isFalse();
    }

    @Test
    public void testFindByEmail_success() {
        when(userRepository.findByEmail("admin@mail.com")).thenReturn(Optional.of(user));

        Optional<User> user = this.userRepository.findByEmail("admin@mail.com");
        assertTrue(user.isPresent());
        assertEquals("admin@mail.com", user.get().getEmail());
        assertThat(user.get().getEmail().equals("admin@mail.com"));
    }

    @Test
    public void testFindByEmail_notFound() {
        when(userRepository.findByEmail("dimas@mail.com")).thenReturn(Optional.empty());

        Optional<User> foundUser = this.userRepository.findByEmail("dimas@mail.com");
        assertThat(foundUser.isPresent()).isFalse();
    }
}
