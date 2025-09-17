package com.example.quiz.web;

import com.example.quiz.security.JwtTokenValidator;
import com.example.quiz.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/token")
@CrossOrigin(origins = "http://localhost:5173")
public class TokenController {
    
    private final JwtTokenValidator tokenValidator;
    private final JwtUtil jwtUtil;

    public TokenController(JwtTokenValidator tokenValidator, JwtUtil jwtUtil) {
        this.tokenValidator = tokenValidator;
        this.jwtUtil = jwtUtil;
    }

    record TokenValidationRequest(String token) {}

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody TokenValidationRequest request) {
        String token = request.token();
        
        JwtTokenValidator.TokenValidationResult result = tokenValidator.validateAccessToken(token);
        
        if (result.isValid()) {
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", result.getUsername(),
                "role", result.getRole(),
                "tokenType", "access"
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                "valid", false,
                "expired", result.isExpired(),
                "error", result.getErrorMessage()
            ));
        }
    }

    @PostMapping("/validate-refresh")
    public ResponseEntity<?> validateRefreshToken(@RequestBody TokenValidationRequest request) {
        String token = request.token();
        
        JwtTokenValidator.TokenValidationResult result = tokenValidator.validateRefreshToken(token);
        
        if (result.isValid()) {
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", result.getUsername(),
                "role", result.getRole(),
                "tokenType", "refresh"
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                "valid", false,
                "expired", result.isExpired(),
                "error", result.getErrorMessage()
            ));
        }
    }

    @PostMapping("/info")
    public ResponseEntity<?> getTokenInfo(@RequestBody TokenValidationRequest request) {
        String token = request.token();
        
        try {
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            String tokenType = jwtUtil.extractTokenType(token);
            boolean expired = jwtUtil.isTokenExpired(token);
            
            return ResponseEntity.ok(Map.of(
                "username", username,
                "role", role,
                "tokenType", tokenType,
                "expired", expired
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid token format",
                "message", e.getMessage()
            ));
        }
    }
}
