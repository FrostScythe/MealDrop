package com.restaurantmanagement.order_api.service;


import com.restaurantmanagement.order_api.model.Order;
import com.restaurantmanagement.order_api.model.OrderStatus;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Order placeOrder(Long userId,
                     Long restaurantId,
                     Map<Long, Integer> itemsWithQuantity);


    Order getOrderById(Long orderId);

    List<Order> getOrdersByUser(Long userId);

    Order updateOrderStatus(Long orderId, OrderStatus newStatus);
}
