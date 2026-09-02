package com.practice.student_management.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ErrorResponse response =
        		new ErrorResponse(
        		        HttpStatus.BAD_REQUEST.value(),
        		        "Validation failed",
        		        errors
        		);

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFoundException(
            StudentNotFoundException ex) {

    	ErrorResponse response =
    	        new ErrorResponse(
    	                HttpStatus.NOT_FOUND.value(),
    	                ex.getMessage()
    	        );

    	return ResponseEntity
    	        .status(HttpStatus.NOT_FOUND)
    	        .body(response);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex) {

    	ErrorResponse response =
    	        new ErrorResponse(
    	                HttpStatus.UNAUTHORIZED.value(),
    	                "Invalid username or password"
    	        );

    	return ResponseEntity
    	        .status(HttpStatus.UNAUTHORIZED)
    	        .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {

    	ErrorResponse response =
    	        new ErrorResponse(
    	                HttpStatus.INTERNAL_SERVER_ERROR.value(),
    	                "An unexpected error occurred"
    	        );

    	return ResponseEntity
    	        .status(HttpStatus.INTERNAL_SERVER_ERROR)
    	        .body(response);
    }
}