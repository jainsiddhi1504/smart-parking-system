package com.siddhi.smartparking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET =
            "SmartParkingSuperSecureJwtSecretKey2026ForInternshipProjectAndRedisIntegration12345";

    private final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(
                    SECRET.getBytes(StandardCharsets.UTF_8)
            );

    public String generateToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000 * 60 * 60
                        )
                )
                .signWith(
                        SECRET_KEY,
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    public String extractUsername(String token) {

        Claims claims =
                Jwts.parserBuilder()
                        .setSigningKey(SECRET_KEY)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(
            String token,
            String username
    ) {

        return extractUsername(token)
                .equals(username);
    }
}