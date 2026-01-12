package com.restaurantmanagement.order_api.service.imp;

import com.restaurantmanagement.order_api.entity.*;
import com.restaurantmanagement.order_api.entity.MenuItem;
import com.restaurantmanagement.order_api.exception.BadRequestException;
import com.restaurantmanagement.order_api.exception.InvalidOrderStateException;
import com.restaurantmanagement.order_api.exception.NotFoundException;
import com.restaurantmanagement.order_api.repository.MenuItemRepository;
import com.restaurantmanagement.order_api.repository.OrderRepository;
import com.restaurantmanagement.order_api.repository.RestaurantRepository;
import com.restaurantmanagement.order_api.repository.UserRepository;
import com.restaurantmanagement.order_api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImp implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Override
    public Order placeOrder(Long userId, Long restaurantId, Map<Long, Integer> itemsWithQuantity) {

        // Fetch User - use NotFoundException
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        // Fetch Restaurant - use NotFoundException
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant", restaurantId));

        // Validate input - add this at the start
        if (itemsWithQuantity == null || itemsWithQuantity.isEmpty()) {
            throw new BadRequestException("Order must contain at least one item");
        }

        double totalPrice = 0;
        int totalItemCount = 0;
        List<MenuItem> orderedItems = new ArrayList<>();

        // Calculate price using quantity
        for (Map.Entry<Long, Integer> entry : itemsWithQuantity.entrySet()) {
            Long menuItemId = entry.getKey();
            Integer quantity = entry.getValue();

            // this validation
            if (quantity == null || quantity <= 0) {
                throw new BadRequestException("Quantity must be greater than 0 for menu item: " + menuItemId);
            }

            // Fetch MenuItem - use NotFoundException
            MenuItem item = menuItemRepository.findById(menuItemId)
                    .orElseThrow(() -> new NotFoundException("MenuItem", menuItemId));

            totalPrice += item.getPrice() * quantity;
            totalItemCount += quantity;

            for (int i = 0; i < quantity; i++) {
                orderedItems.add(item);
            }
        }

        // Create Order
        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setOrderedItems(orderedItems);
        order.setItemCount(totalItemCount);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PLACED);

        return orderRepository.save(order);
    }


    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order", orderId));
    }

    @Override
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order", orderId));

        OrderStatus currStatus = order.getStatus();

        if (currStatus == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("Cannot update order - already delivered");
        }

        if (currStatus == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Cannot update order - already cancelled");
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}