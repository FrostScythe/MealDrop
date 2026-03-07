package com.restaurantmanagement.order_api.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemResponse {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private int quantity; // makes sense here
}