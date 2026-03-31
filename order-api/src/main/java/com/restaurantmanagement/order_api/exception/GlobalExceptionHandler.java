package com.restaurantmanagement.order_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<String> handleInvalidOrderState(InvalidOrderStateException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ForbiddenRequestException.class)
    public ResponseEntity<String> handleForbiddenRequest(ForbiddenRequestException ex){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }

    // NEW: Handle restaurant closed exception with detailed response
    @ExceptionHandler(RestaurantClosedException.class)
    public ResponseEntity<Map<String, Object>> handleRestaurantClosed(RestaurantClosedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Restaurant Closed");
        response.put("message", ex.getMessage());
        response.put("restaurantName", ex.getRestaurantName());
        response.put("reason", ex.getReason());

        // Include timing information if available
        if (ex.getOpeningTime() != null) {
            response.put("openingTime", ex.getOpeningTime().toString());
        }
        if (ex.getClosingTime() != null) {
            response.put("closingTime", ex.getClosingTime().toString());
        }
        if (ex.getLastOrderTime() != null) {
            response.put("lastOrderTime", ex.getLastOrderTime().toString());
        }

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)  // 503 status
                .body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
        // Note: deliberately vague — don't reveal which one was wrong
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Access denied: you don't have permission to perform this action");
    }
}