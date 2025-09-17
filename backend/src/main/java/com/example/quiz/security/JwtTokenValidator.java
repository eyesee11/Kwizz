package com.example.quiz.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtTokenValidator {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);
    
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtTokenValidator(JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public TokenValidationResult validateAccessToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return TokenValidationResult.invalid("Token is null or empty");
        }

        try {
            // Check if token is blacklisted
            if (tokenBlacklistService.isBlacklisted(token)) {
                return TokenValidationResult.invalid("Token has been blacklisted");
            }

            String username = jwtUtil.extractUsername(token);
            String tokenType = jwtUtil.extractTokenType(token);
            String role = jwtUtil.extractRole(token);

            // Validate token type
            if (!"access".equals(tokenType)) {
                return TokenValidationResult.invalid("Invalid token type. Expected access token");
            }

            // Validate token
            if (jwtUtil.validateToken(token, username)) {
                return TokenValidationResult.valid(username, role);
            } else {
                return TokenValidationResult.invalid("Token validation failed");
            }

        } catch (ExpiredJwtException e) {
            logger.debug("JWT token expired: {}", e.getMessage());
            return TokenValidationResult.expired("JWT token has expired");
        } catch (SecurityException | MalformedJwtException e) {
            logger.debug("Invalid JWT token: {}", e.getMessage());
            return TokenValidationResult.invalid("Invalid JWT token format");
        } catch (Exception e) {
            logger.error("JWT validation error: {}", e.getMessage());
            return TokenValidationResult.invalid("JWT validation failed: " + e.getMessage());
        }
    }

    public TokenValidationResult validateRefreshToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return TokenValidationResult.invalid("Refresh token is null or empty");
        }

        try {
            // Check if token is blacklisted
            if (tokenBlacklistService.isBlacklisted(token)) {
                return TokenValidationResult.invalid("Refresh token has been blacklisted");
            }

            String username = jwtUtil.extractUsername(token);
            String tokenType = jwtUtil.extractTokenType(token);
            String role = jwtUtil.extractRole(token);

            // Validate token type
            if (!"refresh".equals(tokenType)) {
                return TokenValidationResult.invalid("Invalid token type. Expected refresh token");
            }

            // Validate refresh token
            if (jwtUtil.validateRefreshToken(token, username)) {
                return TokenValidationResult.valid(username, role);
            } else {
                return TokenValidationResult.invalid("Refresh token validation failed");
            }

        } catch (ExpiredJwtException e) {
            logger.debug("Refresh token expired: {}", e.getMessage());
            return TokenValidationResult.expired("Refresh token has expired");
        } catch (SecurityException | MalformedJwtException e) {
            logger.debug("Invalid refresh token: {}", e.getMessage());
            return TokenValidationResult.invalid("Invalid refresh token format");
        } catch (Exception e) {
            logger.error("Refresh token validation error: {}", e.getMessage());
            return TokenValidationResult.invalid("Refresh token validation failed: " + e.getMessage());
        }
    }

    public static class TokenValidationResult {
        private final boolean valid;
        private final boolean expired;
        private final String username;
        private final String role;
        private final String errorMessage;

        private TokenValidationResult(boolean valid, boolean expired, String username, String role, String errorMessage) {
            this.valid = valid;
            this.expired = expired;
            this.username = username;
            this.role = role;
            this.errorMessage = errorMessage;
        }

        public static TokenValidationResult valid(String username, String role) {
            return new TokenValidationResult(true, false, username, role, null);
        }

        public static TokenValidationResult invalid(String errorMessage) {
            return new TokenValidationResult(false, false, null, null, errorMessage);
        }

        public static TokenValidationResult expired(String errorMessage) {
            return new TokenValidationResult(false, true, null, null, errorMessage);
        }

        public boolean isValid() { return valid; }
        public boolean isExpired() { return expired; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public String getErrorMessage() { return errorMessage; }
    }
}
