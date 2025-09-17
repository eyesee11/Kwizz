package com.example.quiz.web;

import com.example.quiz.domain.Role;
import com.example.quiz.domain.User;
import com.example.quiz.security.JwtUtil;
import com.example.quiz.security.JwtTokenValidator;
import com.example.quiz.security.SessionUser;
import com.example.quiz.security.TokenBlacklistService;
import com.example.quiz.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtTokenValidator tokenValidator;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService, JwtTokenValidator tokenValidator) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.tokenValidator = tokenValidator;
    }

    record SignupRequest(@NotBlank String name, @Email String email, @NotBlank String password, Role role) {}
    record LoginRequest(@Email String email, @NotBlank String password) {}
    record RefreshTokenRequest(String token) {}
    record AuthResponse(String token, String refreshToken, SessionUser user) {}

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        try {
            User u = userService.signup(req.name(), req.email(), req.password(), req.role());
            String token = jwtUtil.generateToken(u.getEmail(), u.getRole().toString());
            String refreshToken = jwtUtil.generateRefreshToken(u.getEmail(), u.getRole().toString());
            return ResponseEntity.ok(new AuthResponse(token, refreshToken, SessionUser.from(u)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            User u = userService.findByEmail(req.email()).orElseThrow(() ->
                new RuntimeException("User not found"));

            if (!passwordEncoder.matches(req.password(), u.getPasswordHash())) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
            }

            String token = jwtUtil.generateToken(u.getEmail(), u.getRole().toString());
            String refreshToken = jwtUtil.generateRefreshToken(u.getEmail(), u.getRole().toString());

            return ResponseEntity.ok(new AuthResponse(token, refreshToken, SessionUser.from(u)));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
        }
        return ResponseEntity.ok(Map.of("status", "ok", "message", "Successfully logged out"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        String email = authentication.getName();
        User u = userService.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(SessionUser.from(u));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest req) {
        String refreshToken = req.token();

        // Validate the refresh token using the new validator
        JwtTokenValidator.TokenValidationResult validationResult = tokenValidator.validateRefreshToken(refreshToken);
        
        if (!validationResult.isValid()) {
            if (validationResult.isExpired()) {
                return ResponseEntity.status(401).body(Map.of(
                    "message", validationResult.getErrorMessage(),
                    "code", "REFRESH_TOKEN_EXPIRED"
                ));
            } else {
                return ResponseEntity.status(401).body(Map.of(
                    "message", validationResult.getErrorMessage(),
                    "code", "INVALID_REFRESH_TOKEN"
                ));
            }
        }

        try {
            String username = validationResult.getUsername();
            String role = validationResult.getRole();

            // Blacklist the old refresh token to prevent reuse
            tokenBlacklistService.blacklistToken(refreshToken);
            
            // Generate new tokens
            String newToken = jwtUtil.generateToken(username, role);
            String newRefreshToken = jwtUtil.generateRefreshToken(username, role);

            User u = userService.findByEmail(username).orElseThrow();

            return ResponseEntity.ok(new AuthResponse(newToken, newRefreshToken, SessionUser.from(u)));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "Failed to refresh token: " + e.getMessage(),
                "code", "REFRESH_FAILED"
            ));
        }
    }
}
