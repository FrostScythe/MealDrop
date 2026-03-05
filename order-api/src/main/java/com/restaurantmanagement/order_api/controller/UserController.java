package com.restaurantmanagement.order_api.controller;

import com.restaurantmanagement.order_api.dto.request.UserRegisterRequest;
import com.restaurantmanagement.order_api.dto.response.UserResponse;
import com.restaurantmanagement.order_api.entity.Role;
import com.restaurantmanagement.order_api.entity.User;
import com.restaurantmanagement.order_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // CREATE - Register new user
    // Public endpoint — no auth needed
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(userService.registerUser(request, Role.CUSTOMER));
    }

    // Protected endpoint — ADMIN only (after Security is added)
    @PostMapping("/register/owner")
    // @PreAuthorize("hasRole('ADMIN')")  ← uncomment after Security
    public ResponseEntity<UserResponse> registerOwner(
            @Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(userService.registerUser(request, Role.OWNER));
    }

    // READ - Get all users (admin endpoint)
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // READ - Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // UPDATE - Update user details
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(userService.updateUserDetails(id, request));
    }

    // DELETE - Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        String user= userService.deleteUser(id);
        return ResponseEntity.ok(user);
    }

    // GET user's orders
    @GetMapping("/{id}/orders")
    public ResponseEntity<?> getUserOrders(@PathVariable Long id) {
        // This depends on your UserService method
        // If you have: userService.getUserOrders(id)
        // return ResponseEntity.ok(orders);

        // For now, return message
        return ResponseEntity.ok("Orders for user " + id + " (implement in service)");
    }
}