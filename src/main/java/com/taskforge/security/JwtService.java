package com.taskforge.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final Key key;
    private final long expirationSeconds;

    public JwtService(
            @Value("${taskforge.security.jwtSecret:auto}") String secret,
            @Value("${taskforge.security.jwtExpirationSeconds}") long expirationSeconds
    ) {
        Key useKey;
        if (secret == null || secret.isBlank() || "auto".equalsIgnoreCase(secret)) {
            // Generate a secure random key for HS256
            useKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            // Derive a 256-bit key from the provided secret using SHA-256 to satisfy HS256 requirements
            byte[] keyBytes;
            try {
                java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
                keyBytes = digest.digest(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            } catch (java.security.NoSuchAlgorithmException e) {
                throw new IllegalStateException("SHA-256 not available", e);
            }
            useKey = Keys.hmacShaKeyFor(keyBytes);
        }
        this.key = useKey;
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}


