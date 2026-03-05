package com.restaurantmanagement.order_api.service;


import com.restaurantmanagement.order_api.dto.request.PlaceOrderRequest;
import com.restaurantmanagement.order_api.dto.response.OrderResponse;
import com.restaurantmanagement.order_api.entity.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(Long userId, Long restaurantId,
                             PlaceOrderRequest request);
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getOrdersByUser(Long userId);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);
}