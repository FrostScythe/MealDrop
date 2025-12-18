package com.restaurantmanagement.order_api.controller;

import com.restaurantmanagement.order_api.entity.User;
import com.restaurantmanagement.order_api.service.UserService;
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
    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        String createdUser = userService.registerUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // READ - Get all users (admin endpoint)
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // READ - Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<String> getUserById(@PathVariable Long id) {
        String user = userService.getUserDetails(id);
        return ResponseEntity.ok(user);
    }

    // UPDATE - Update user details
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id); // Ensure ID matches path
        String updatedUser = userService.updateUserDetails(id,user);
        return ResponseEntity.ok(updatedUser);
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