package com.restaurantmanagement.order_api.service;

import com.restaurantmanagement.order_api.entity.User;
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

    public String registerUser(@RequestBody User user) {
        userRepository.save(user);
        // Logic to register a new user
        return "User registered successfully";
    }

    public String getUserDetails(Long userId){
        Optional<User> foundUser = userRepository.findById(userId);
        if(foundUser.isPresent()){
            return "User Details: Name - " + foundUser.get().getName() + ", Email - " + foundUser.get().getEmail();
        } else {
            return "User not found";
        }
    }

    public String updateUserDetails(Long userId, @RequestBody User updatedUser) {
        Optional<User> existingUserOptional = userRepository.findById(userId);

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();

            // Consider partial updates and validation
            if (updatedUser.getName() != null) {
                existingUser.setName(updatedUser.getName());
            }
            if (updatedUser.getEmail() != null) {
                existingUser.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            }
            if (updatedUser.getAddress() != null) {
                existingUser.setAddress(updatedUser.getAddress());
            }

            userRepository.save(existingUser);
            return "User details updated successfully";
        } else {
            return "User not found";
        }
    }

    public String deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return "User deleted successfully";
        } else {
            return "User not found";
        }
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
