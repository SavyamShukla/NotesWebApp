
/*
package com.notes.notesplatform.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private static final String SECRET = "A_SECRET_KEY_MUST_BE_AT_LEAST_32_BYTES_LONG!";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .signWith(key)
                .compact();
    }
}*/