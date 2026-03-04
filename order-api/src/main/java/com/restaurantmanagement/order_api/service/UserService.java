package com.restaurantmanagement.order_api.service;

import com.restaurantmanagement.order_api.dto.request.UserRegisterRequest;
import com.restaurantmanagement.order_api.dto.response.UserResponse;
import com.restaurantmanagement.order_api.entity.Role;
import com.restaurantmanagement.order_api.entity.User;
import com.restaurantmanagement.order_api.exception.NotFoundException;
import com.restaurantmanagement.order_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        return response;
    }

    public UserResponse registerUser(UserRegisterRequest request, Role role) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setRole(role);  // role comes from the endpoint, not the request body
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
        return toResponse(user);
    }

    public UserResponse updateUserDetails(Long userId, UserRegisterRequest request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
        if (request.getName() != null) existingUser.setName(request.getName());
        if (request.getEmail() != null) existingUser.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) existingUser.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) existingUser.setAddress(request.getAddress());
        return toResponse(userRepository.save(existingUser));
    }

    public String deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return "User deleted successfully";
        } else {
            throw new NotFoundException("User", userId);
        }
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}