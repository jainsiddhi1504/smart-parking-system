package com.siddhi.smartparking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    // Secret key
    private final String SECRET_KEY =
            "smartparkingsecretkey";

    // Generate token
    public String generateToken(
            String username
    ) {

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
                        SignatureAlgorithm.HS256,
                        SECRET_KEY
                )
                .compact();
    }

    // Extract username
    public String extractUsername(
            String token
    ) {

        Claims claims =
                Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .parseClaimsJws(token)
                        .getBody();

        return claims.getSubject();
    }

    // Validate token
    public boolean validateToken(
            String token,
            String username
    ) {

        String extractedUsername =
                extractUsername(token);

        return extractedUsername
                .equals(username);
    }
}