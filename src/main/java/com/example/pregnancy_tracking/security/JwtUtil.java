package com.example.pregnancy_tracking.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "aGhpZGphZGthZG1rYWRmMTIzMTIzMTIzMTIzMTIzMTIzMTIzMTIzMTIz"; 
    private final long EXPIRATION_TIME = 86400000; // 1 ngày

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email) { 
        return Jwts.builder()
                .setSubject(email) // Lưu email vào subject
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
