package com.siddhi.smartparking.service;

import com.siddhi.smartparking.dto.LoginRequest;
import com.siddhi.smartparking.dto.RegisterRequest;
import com.siddhi.smartparking.entity.User;
import com.siddhi.smartparking.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterRequest request = new RegisterRequest();
        request.setName("Siddhi");
        request.setEmail("siddhi@gmail.com");
        request.setPhone("9876543210");
        request.setPassword("password");

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.register(request);

        assertEquals("Siddhi", user.getName());
        assertEquals("siddhi@gmail.com", user.getEmail());
        assertEquals("USER", user.getRole());
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request = new LoginRequest();
        request.setEmail("siddhi@gmail.com");
        request.setPassword("password");

        User user = new User();
        user.setEmail("siddhi@gmail.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("siddhi@gmail.com"))
                .thenReturn(java.util.Optional.of(user));

        when(passwordEncoder.matches(
                "password",
                "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken("siddhi@gmail.com"))
                .thenReturn("jwt-token");

        String token = userService.login(request);

        assertEquals("jwt-token", token);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(java.util.Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.login(request)
                );

        assertEquals(
                "User not found",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {

        LoginRequest request = new LoginRequest();
        request.setEmail("siddhi@gmail.com");
        request.setPassword("wrong");

        User user = new User();
        user.setEmail("siddhi@gmail.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("siddhi@gmail.com"))
                .thenReturn(java.util.Optional.of(user));

        when(passwordEncoder.matches(
                "wrong",
                "encodedPassword"))
                .thenReturn(false);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.login(request)
                );

        assertEquals(
                "Invalid password",
                exception.getMessage()
        );
    }

}
