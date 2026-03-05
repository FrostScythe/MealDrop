package com.restaurantmanagement.order_api.entity;

public enum OrderStatus {
    PLACED,           // order received
    PREPARING,        // kitchen accepted
    OUT_FOR_DELIVERY, // driver picked up
    DELIVERED,        // completed
    CANCELLED         // cancelled
}