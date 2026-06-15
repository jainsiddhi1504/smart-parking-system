package com.siddhi.smartparking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            ResourceNotFoundException.class
    )
    public ResponseEntity<?> handleNotFound(
            ResourceNotFoundException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of(
                                "status", 404,
                                "message", ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<?> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        String errorMessage =
                ex.getBindingResult()
                        .getFieldError()
                        .getDefaultMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of(
                                "status", 400,
                                "message", errorMessage
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(
            Exception ex
    ) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        Map.of(
                                "status", 500,
                                "message", ex.getMessage()
                        )
                );
    }
}