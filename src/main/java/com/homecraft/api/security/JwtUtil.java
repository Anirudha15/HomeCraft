package com.homecraft.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.key}") String secret) {
        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Token
    public String generateToken(
            Integer userId,
            String role,
            String email
    ) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000)
                )
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validation
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Authentication
    public Authentication getAuthentication(String token) {

        Claims claims = getClaims(token);

        String userId = claims.getSubject();

        String role = claims.get("role", String.class);
        if (role == null || role.isBlank()) {
            return null;
        }

        String email = claims.get("email", String.class);

        return new UsernamePasswordAuthenticationToken(
                userId,
                email,
                List.of(new SimpleGrantedAuthority(role))
        );
    }

    // Internal
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}