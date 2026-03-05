package com.restaurantmanagement.order_api.controller;

import com.restaurantmanagement.order_api.dto.request.PlaceOrderRequest;
import com.restaurantmanagement.order_api.dto.response.OrderResponse;
import com.restaurantmanagement.order_api.entity.OrderStatus;
import com.restaurantmanagement.order_api.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place-order")
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestParam Long userId,
            @RequestParam Long restaurantId,
            @Valid @RequestBody PlaceOrderRequest request) {
        return new ResponseEntity<>(
                orderService.placeOrder(userId, restaurantId, request),
                HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }
}
