package com.restaurantmanagement.order_api.dto.response;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MenuItemResponse {
    private Long id;
    private String name;
    private String description;
    private double price;
    private Integer stockQuantity;
    private boolean available;
    private String imageUrl;
    private Integer quantity; // populated when returned as part of an OrderResponse
    // getters & setters — NO restaurant field to avoid circular serialization
}