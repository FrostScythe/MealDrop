package com.restaurantmanagement.order_api.service;

import com.restaurantmanagement.order_api.model.MenuItem;
import com.restaurantmanagement.order_api.model.Restaurant;
import com.restaurantmanagement.order_api.exception.BadRequestException;
import com.restaurantmanagement.order_api.exception.NotFoundException;
import com.restaurantmanagement.order_api.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemService menuItemService;  // Add this dependency

    // Register Restaurant
    public Restaurant registerRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    // Get Restaurant
    public Restaurant getRestaurantDetails(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant",restaurantId));
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    // Update restaurant details
    public Restaurant updateRestaurantDetails(Long restaurantId, Restaurant updatedRestaurant) {
        Restaurant existingRestaurant = getRestaurantDetails(restaurantId);

        if (updatedRestaurant.getName() != null) {
            existingRestaurant.setName(updatedRestaurant.getName());
        }
        if (updatedRestaurant.getAddress() != null) {
            existingRestaurant.setAddress(updatedRestaurant.getAddress());
        }

        String phone = updatedRestaurant.getPhoneNumber();
        if (phone == null || phone.trim().isEmpty() || phone.equals("0")) {
            throw new BadRequestException("Phone number cannot be empty or zero");
        } else existingRestaurant.setPhoneNumber(phone);

        return restaurantRepository.save(existingRestaurant);
    }

    // Delete restaurant
    public void deleteRestaurant(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new NotFoundException("Restaurant", restaurantId);
        }
        restaurantRepository.deleteById(restaurantId);
    }

    // ========== MENU OPERATIONS (Delegate to MenuItemService) ==========

    public List<MenuItem> getRestaurantMenu(Long restaurantId) {
        // Verify restaurant exists first
        getRestaurantDetails(restaurantId);
        // Delegate to MenuItemService
        return menuItemService.getMenuByRestaurant(restaurantId);
    }

    public MenuItem addMenuItemToRestaurant(Long restaurantId, MenuItem menuItem) {
        // Verify restaurant exists
        getRestaurantDetails(restaurantId);
        // Delegate to MenuItemService
        return menuItemService.createMenuItem(restaurantId, menuItem);
    }

    public MenuItem getMenuItem(Long restaurantId, Long menuItemId) {
        // Delegate to MenuItemService
        return menuItemService.getMenuItem(restaurantId, menuItemId);
    }

    public MenuItem updateMenuItem(Long restaurantId, Long menuItemId, MenuItem menuItem) {
        // Delegate to MenuItemService
        return menuItemService.updateMenuItem(restaurantId, menuItemId, menuItem);
    }

    public void deleteMenuItem(Long restaurantId, Long menuItemId) {
        // Delegate to MenuItemService
        menuItemService.deleteMenuItem(restaurantId, menuItemId);
    }
}