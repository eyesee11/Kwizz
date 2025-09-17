package com.example.quiz.security;

import com.example.quiz.domain.Role;
import com.example.quiz.domain.User;

public record SessionUser(Long id, String name, String email, Role role) {
    public static SessionUser from(User u) { return new SessionUser(u.getId(), u.getName(), u.getEmail(), u.getRole()); }
}


