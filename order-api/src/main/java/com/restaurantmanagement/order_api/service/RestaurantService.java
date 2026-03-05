package com.restaurantmanagement.order_api.service;

import com.restaurantmanagement.order_api.dto.request.MenuItemRequest;
import com.restaurantmanagement.order_api.dto.request.RestaurantRequest;
import com.restaurantmanagement.order_api.dto.response.MenuItemResponse;
import com.restaurantmanagement.order_api.dto.response.RestaurantResponse;
import com.restaurantmanagement.order_api.entity.Restaurant;
import com.restaurantmanagement.order_api.exception.BadRequestException;
import com.restaurantmanagement.order_api.exception.NotFoundException;
import com.restaurantmanagement.order_api.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemService menuItemService;

    // Map Restaurant entity -> RestaurantResponse DTO
    private RestaurantResponse toResponse(Restaurant restaurant) {
        RestaurantResponse response = new RestaurantResponse();
        response.setId(restaurant.getId());
        response.setName(restaurant.getName());
        response.setPhoneNumber(restaurant.getPhoneNumber());
        response.setAddress(restaurant.getAddress());
        response.setOpeningTime(restaurant.getOpeningTime());
        response.setClosingTime(restaurant.getClosingTime());
        response.setPreparationTimeMinutes(restaurant.getPreparationTimeMinutes());
        response.setOpen(restaurant.isOpen());
        response.setCurrentStatus(restaurant.getOperatingStatus());
        return response;
    }

    // Map RestaurantRequest DTO -> Restaurant entity
    private Restaurant toEntity(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhoneNumber(request.getPhoneNumber());
        restaurant.setOpeningTime(request.getOpeningTime());
        restaurant.setClosingTime(request.getClosingTime());
        restaurant.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        restaurant.setOpen(request.isOpen());
        return restaurant;
    }

    public RestaurantResponse registerRestaurant(RestaurantRequest request) {
        Restaurant restaurant = toEntity(request);
        return toResponse(restaurantRepository.save(restaurant));
    }

    public RestaurantResponse getRestaurantDetails(Long restaurantId) {
        Restaurant restaurant = getRestaurantEntity(restaurantId);
        return toResponse(restaurant);
    }

    // Internal helper used by other services (e.g. OrderService)
    public Restaurant getRestaurantEntity(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant", restaurantId));
    }

    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RestaurantResponse updateRestaurantDetails(Long restaurantId, RestaurantRequest request) {
        Restaurant existingRestaurant = getRestaurantEntity(restaurantId);

        if (request.getName() != null)
            existingRestaurant.setName(request.getName());

        if (request.getAddress() != null)
            existingRestaurant.setAddress(request.getAddress());


        String phone = request.getPhoneNumber();
        if (phone == null || phone.trim().isEmpty() || phone.equals("0"))
            throw new BadRequestException("Phone number cannot be empty or zero");

        existingRestaurant.setPhoneNumber(phone);

        if (request.getOpeningTime() != null)
            existingRestaurant.setOpeningTime(request.getOpeningTime());

        if (request.getClosingTime() != null)
            existingRestaurant.setClosingTime(request.getClosingTime());

        if (request.getPreparationTimeMinutes() != null)
            existingRestaurant.setPreparationTimeMinutes(request.getPreparationTimeMinutes());

        existingRestaurant.setOpen(request.isOpen());

        return toResponse(restaurantRepository.save(existingRestaurant));
    }

    public void deleteRestaurant(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new NotFoundException("Restaurant", restaurantId);
        }
        restaurantRepository.deleteById(restaurantId);
    }

    // ========== MENU OPERATIONS (Delegate to MenuItemService) ==========

    public List<MenuItemResponse> getRestaurantMenu(Long restaurantId) {
        // Verify restaurant exists first
        getRestaurantDetails(restaurantId);
        // Delegate to MenuItemService
        return menuItemService.getMenuByRestaurant(restaurantId);
    }

    public MenuItemResponse addMenuItemToRestaurant(Long restaurantId, MenuItemRequest menuItemRequest) {
        // Verify restaurant exists
        getRestaurantDetails(restaurantId);
        // Delegate to MenuItemService
        return menuItemService.createMenuItem(restaurantId, menuItemRequest);
    }

    public MenuItemResponse getMenuItem(Long restaurantId, Long menuItemId) {
        // Delegate to MenuItemService
        return menuItemService.getMenuItem(restaurantId, menuItemId);
    }

    public MenuItemResponse updateMenuItem(Long restaurantId, Long menuItemId, MenuItemRequest menuItemRequest) {
        // Delegate to MenuItemService
        return menuItemService.updateMenuItem(restaurantId, menuItemId, menuItemRequest);
    }

    public void deleteMenuItem(Long restaurantId, Long menuItemId) {
        // Delegate to MenuItemService
        menuItemService.deleteMenuItem(restaurantId, menuItemId);
    }
}