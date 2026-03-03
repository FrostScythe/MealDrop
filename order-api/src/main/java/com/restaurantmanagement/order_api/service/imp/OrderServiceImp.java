package com.restaurantmanagement.order_api.service.imp;

import com.restaurantmanagement.order_api.entity.*;
import com.restaurantmanagement.order_api.exception.BadRequestException;
import com.restaurantmanagement.order_api.exception.InvalidOrderStateException;
import com.restaurantmanagement.order_api.exception.NotFoundException;
import com.restaurantmanagement.order_api.repository.MenuItemRepository;
import com.restaurantmanagement.order_api.repository.OrderRepository;
import com.restaurantmanagement.order_api.repository.RestaurantRepository;
import com.restaurantmanagement.order_api.repository.UserRepository;
import com.restaurantmanagement.order_api.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

import java.util.ArrayList;
import java.util.HashMap;
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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Order placeOrder(Long userId, Long restaurantId,
                            Map<Long, Integer> itemsWithQuantity) {

        // Validate input
        if (itemsWithQuantity == null || itemsWithQuantity.isEmpty()) {
            throw new BadRequestException("Order must contain at least one item");
        }

        // Fetch user & restaurant
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant", restaurantId));

        // NEW: Validate restaurant operating hours (throws RestaurantClosedException if closed)
        restaurant.validateOperatingHours();

        double totalPrice = 0;
        int totalItemCount = 0;
        List<MenuItem> orderedItemsList = new ArrayList<>();

        // Process each item with locking
        for (Map.Entry<Long, Integer> entry : itemsWithQuantity.entrySet()) {
            Long menuItemId = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity == null || quantity <= 0) {
                throw new BadRequestException("Invalid quantity for menu item: " + menuItemId);
            }

            // LOCK the menu item row
            MenuItem menuItem = menuItemRepository.findByIdWithLock(menuItemId)
                    .orElseThrow(() -> new NotFoundException("MenuItem", menuItemId));

            // Verify restaurant
            if (!menuItem.getRestaurant().getId().equals(restaurantId)) {
                throw new BadRequestException(
                        "MenuItem " + menuItemId + " does not belong to this restaurant");
            }

            // Check and reduce stock
            if (!menuItem.canOrder(quantity)) {
                throw new BadRequestException(
                        "Item '" + menuItem.getName() + "' is not available in requested quantity. " +
                                "Available: " + menuItem.getStockQuantity());
            }

            menuItem.reduceStock(quantity);
            menuItemRepository.save(menuItem);

            // Add to order
            for (int i = 0; i < quantity; i++) {
                orderedItemsList.add(menuItem);
            }

            totalPrice += menuItem.getPrice() * quantity;
            totalItemCount += quantity;
        }

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setOrderedItems(orderedItemsList);
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
        return orderRepository.findByUser_Id(userId);
    }

    @Override
    @Transactional
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

        // If cancelling, restore inventory
        if (newStatus == OrderStatus.CANCELLED && currStatus == OrderStatus.PLACED) {
            restoreInventory(order);
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    private void restoreInventory(Order order) {
        // Count quantities per item
        Map<Long, Integer> itemQuantities = new HashMap<>();
        for (MenuItem item : order.getOrderedItems()) {
            itemQuantities.merge(item.getId(), 1, Integer::sum);
        }

        // Restore stock
        for (Map.Entry<Long, Integer> entry : itemQuantities.entrySet()) {
            MenuItem menuItem = menuItemRepository.findByIdWithLock(entry.getKey())
                    .orElseThrow(() -> new NotFoundException("MenuItem", entry.getKey()));

            menuItem.setStockQuantity(menuItem.getStockQuantity() + entry.getValue());
            menuItem.setAvailable(true);
            menuItemRepository.save(menuItem);
        }
    }
}