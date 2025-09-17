package com.example.quiz.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.ttlSeconds:86400}")
    private int tokenValiditySeconds; // 24 hours by default
    
    @Value("${app.jwt.refreshTtlSeconds:604800}")
    private int refreshTokenValiditySeconds; // 7 days by default

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("type", "access");
        return createToken(claims, username, tokenValiditySeconds);
    }
    
    public String generateRefreshToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("type", "refresh");
        return createToken(claims, username, refreshTokenValiditySeconds);
    }

    private String createToken(Map<String, Object> claims, String subject, int validitySeconds) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (long) validitySeconds * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKey getSigningKey() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret key is not properly configured. Please set app.jwt.secret property.");
        }
        // Ensure key is at least 256 bits (32 bytes) for HS256
        if (secretKey.length() < 32) {
            throw new IllegalStateException("JWT secret key must be at least 32 characters long for HS256 algorithm.");
        }
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Boolean validateToken(String token, String username) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        try {
            final String extractedUsername = extractUsername(token);
            String tokenType = extractTokenType(token);
            
            // Ensure it's an access token, not a refresh token
            if (!"access".equals(tokenType)) {
                return false;
            }
            
            return (extractedUsername != null && extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
    
    public Boolean validateRefreshToken(String token, String username) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        try {
            final String extractedUsername = extractUsername(token);
            String tokenType = extractTokenType(token);
            
            // Ensure it's a refresh token
            if (!"refresh".equals(tokenType)) {
                return false;
            }
            
            return (extractedUsername != null && extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }
    
    public boolean isRefreshToken(String token) {
        try {
            return "refresh".equals(extractTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }
}