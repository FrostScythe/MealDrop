package com.restaurantmanagement.order_api.dto.response;

import com.restaurantmanagement.order_api.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long restaurantId;
    private String restaurantName;
    private List<MenuItemResponse> orderedItems;
    private int itemCount;
    private double totalPrice;
    private OrderStatus status;
    private LocalDateTime orderAt;
    private LocalDateTime deliveryAt;
    // getters & setters
}