package com.restaurantmanagement.order_api.service;


import com.restaurantmanagement.order_api.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Order placeOrder(Long userId,
                     Long restaurantId,
                     Map<Long, Integer> itemsWithQuantity);


    Order getOrderById(Long orderId);

    List<Order> getOrdersByUser(Long userId);
}
