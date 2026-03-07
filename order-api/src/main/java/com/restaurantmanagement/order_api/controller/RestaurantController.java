package com.restaurantmanagement.order_api.controller;

import com.restaurantmanagement.order_api.dto.request.MenuItemRequest;
import com.restaurantmanagement.order_api.dto.request.RestaurantRequest;
import com.restaurantmanagement.order_api.dto.response.MenuItemResponse;
import com.restaurantmanagement.order_api.dto.response.RestaurantResponse;
import com.restaurantmanagement.order_api.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    // CREATE - Register new restaurant
    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantRequest request) {
        RestaurantResponse createdRestaurant = restaurantService.registerRestaurant(request);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    // READ - Get all restaurants
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        List<RestaurantResponse> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    // READ - Get restaurant by ID
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        RestaurantResponse restaurant = restaurantService.getRestaurantDetails(id);
        return ResponseEntity.ok(restaurant);
    }

    // UPDATE - Update restaurant details
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(@PathVariable Long id, @Valid @RequestBody RestaurantRequest request) {
        RestaurantResponse updatedRestaurant = restaurantService.updateRestaurantDetails(id, request);
        return ResponseEntity.ok(updatedRestaurant);
    }

    // DELETE - Delete restaurant
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    // ========== MENU ITEM OPERATIONS ==========

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItemResponse>> getRestaurantMenu(
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getRestaurantMenu(restaurantId));
    }

    @PostMapping(value = "/{restaurantId}/menu",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuItemResponse> addMenuItemToRestaurant(
            @PathVariable Long restaurantId,
            @Valid @ModelAttribute MenuItemRequest request,         // ← @ModelAttribute
            @RequestParam(required = false) MultipartFile image) { // ← separate param
        return new ResponseEntity<>(
                restaurantService.addMenuItemToRestaurant(restaurantId, request, image),
                HttpStatus.CREATED);
    }

    @GetMapping("/{restaurantId}/menu/{menuItemId}")
    public ResponseEntity<MenuItemResponse> getMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId) {
        return ResponseEntity.ok(restaurantService.getMenuItem(restaurantId, menuItemId));
    }

    @PutMapping(value = "/{restaurantId}/menu/{menuItemId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId,
            @Valid @ModelAttribute MenuItemRequest request,
            @RequestParam(required = false) MultipartFile image) {
        return new ResponseEntity<>(
                restaurantService.updateMenuItem(restaurantId, menuItemId, request, image),
                HttpStatus.OK);
    }

    @DeleteMapping("/{restaurantId}/menu/{menuItemId}")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId) {
        restaurantService.deleteMenuItem(restaurantId, menuItemId);
        return ResponseEntity.noContent().build();
    }
}