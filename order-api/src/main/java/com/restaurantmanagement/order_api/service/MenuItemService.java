package com.restaurantmanagement.order_api.service;

import com.restaurantmanagement.order_api.dto.request.MenuItemRequest;
import com.restaurantmanagement.order_api.dto.response.MenuItemResponse;
import com.restaurantmanagement.order_api.entity.MenuItem;
import com.restaurantmanagement.order_api.entity.Restaurant;
import com.restaurantmanagement.order_api.exception.BadRequestException;
import com.restaurantmanagement.order_api.exception.ForbiddenRequestException;
import com.restaurantmanagement.order_api.exception.NotFoundException;
import com.restaurantmanagement.order_api.repository.MenuItemRepository;
import com.restaurantmanagement.order_api.repository.RestaurantRepository;
import com.restaurantmanagement.order_api.service.storage.StorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

// In MenuItemService.java
@Service
@Transactional
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private StorageService storageService;

    // ─── Mappers ───────────────────────────────────────────
    private MenuItemResponse toResponse(MenuItem item) {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setPrice(item.getPrice());
        response.setStockQuantity(item.getStockQuantity());
        response.setAvailable(item.isAvailable());
        response.setImageUrl(item.getImageUrl());  // ← include image URL in response
        return response;
    }

    private MenuItem toEntity(MenuItemRequest request) {
        MenuItem item = new MenuItem();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setStockQuantity(request.getStockQuantity());
        item.setAvailable(request.isAvailable());
        // imageUrl set separately after upload
        return item;
    }

    // ─── CRUD ──────────────────────────────────────────────
    public MenuItemResponse createMenuItem(Long restaurantId,
                                           MenuItemRequest request,
                                           MultipartFile image) {  // ← new param
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant", restaurantId));

        MenuItem item = toEntity(request);
        item.setRestaurant(restaurant);

        // Upload image if provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = storageService.uploadFile(image, "menu-items");
            item.setImageUrl(imageUrl);
        }

        return toResponse(menuItemRepository.save(item));
    }


    public MenuItemResponse getMenuItem(Long restaurantId, Long menuItemId) {
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new NotFoundException("MenuItem", menuItemId));
        if (!item.getRestaurant().getId().equals(restaurantId)) {
            throw new ForbiddenRequestException("Access denied: Menu item " + menuItemId +
                    " does not belong to restaurant " + restaurantId);
        }
        return toResponse(item);
    }

    public List<MenuItemResponse> getMenuByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MenuItemResponse updateMenuItem(Long restaurantId, Long menuItemId,
                                           MenuItemRequest request,
                                           MultipartFile image) {  // ← new param
        MenuItem existing = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new NotFoundException("MenuItem", menuItemId));
        if (!existing.getRestaurant().getId().equals(restaurantId))
            throw new ForbiddenRequestException("Access denied: Menu item " + menuItemId +
                    " does not belong to restaurant " + restaurantId);

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());

        if (request.getPrice() < 0)
            throw new BadRequestException("Price cannot be negative");
        existing.setPrice(request.getPrice());

        Integer stock = request.getStockQuantity();
        if (stock != null && stock < 0)
            throw new BadRequestException("Stock cannot be negative");
        existing.setStockQuantity(stock);
        existing.setAvailable(stock != null && stock > 0 && request.isAvailable());

        // If a new image is provided, delete old one and upload new
        if (image != null && !image.isEmpty()) {
            storageService.deleteFile(existing.getImageUrl()); // delete old
            String newImageUrl = storageService.uploadFile(image, "menu-items");
            existing.setImageUrl(newImageUrl);
        }

        return toResponse(menuItemRepository.save(existing));
    }

    public void deleteMenuItem(Long restaurantId, Long menuItemId) {
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new NotFoundException("MenuItem", menuItemId));
        if (!item.getRestaurant().getId().equals(restaurantId)) {
            throw new ForbiddenRequestException("Access denied: Menu item " + menuItemId +
                    " does not belong to restaurant " + restaurantId);
        }
        storageService.deleteFile(item.getImageUrl());
        menuItemRepository.delete(item);
    }

    public MenuItem getMenuItemEntity(Long menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new NotFoundException("MenuItem", menuItemId));
    }
}