package com.restaurantmanagement.order_api.controller;

import com.restaurantmanagement.order_api.dto.request.UserRegisterRequest;
import com.restaurantmanagement.order_api.dto.response.UserResponse;
import com.restaurantmanagement.order_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            // @Valid triggers validation on the request fields.
            // UserRegisterRequest has @NotBlank, @Email, @Size annotations.
            @Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(userService.updateUserDetails(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<?> getUserOrders(@PathVariable Long id) {
        return ResponseEntity.ok("Orders for user " + id + " — implement me");
    }
}