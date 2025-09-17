package com.example.quiz.service;

import com.example.quiz.domain.Role;
import com.example.quiz.domain.User;
import com.example.quiz.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(String name, String email, String rawPassword, Role role) {
        // Check if user already exists
        if (userRepository.findByEmail(email.toLowerCase()).isPresent()) {
            throw new RuntimeException("User with email " + email + " already exists");
        }
        
        User u = new User();
        u.setName(name);
        u.setEmail(email.toLowerCase());
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        return userRepository.save(u);
    }

    public Optional<User> findByEmail(String email) { return userRepository.findByEmail(email.toLowerCase()); }
    public Optional<User> findById(Long id) { return userRepository.findById(id); }
}


