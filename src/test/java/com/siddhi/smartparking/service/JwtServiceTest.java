package com.siddhi.smartparking.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtServiceTest {

    private final JwtService jwtService =
            new JwtService();

    @Test
    void shouldGenerateTokenSuccessfully() {

        String token =
                jwtService.generateToken(
                        "siddhi@gmail.com"
                );

        assertNotNull(token);
    }

    @Test
    void shouldGenerateDifferentTokens() {

        String token1 =
                jwtService.generateToken(
                        "siddhi@gmail.com"
                );

        String token2 =
                jwtService.generateToken(
                        "siddhi@gmail.com"
                );

        assertNotNull(token1);
        assertNotNull(token2);
    }
}