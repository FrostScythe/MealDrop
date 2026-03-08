package com.restaurantmanagement.order_api.service.imp;

import com.restaurantmanagement.order_api.dto.request.PlaceOrderRequest;
import com.restaurantmanagement.order_api.dto.response.OrderItemResponse;
import com.restaurantmanagement.order_api.dto.response.OrderResponse;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp implements OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private MenuItemRepository menuItemRepository;

    // ─── Mapper ────────────────────────────────────────────
    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setUserName(order.getUser().getName());
        response.setRestaurantId(order.getRestaurant().getId());
        response.setRestaurantName(order.getRestaurant().getName());
        response.setItemCount(order.getItemCount());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        response.setOrderAt(order.getOrderAt());
        response.setDeliveryAt(order.getDeliveryAt());

        Map<Long, MenuItem> menuItemMap = menuItemRepository.findAllById(order.getOrderedItems().keySet())
                .stream()
                .collect(Collectors.toMap(MenuItem::getId, m -> m));

        List<OrderItemResponse> itemResponses = order.getOrderedItems()
                .entrySet()
                .stream()
                .map(entry -> {
                    MenuItem item = menuItemMap.get(entry.getKey());
                    if (item == null)
                        throw new NotFoundException("MenuItem", entry.getKey());
                    OrderItemResponse r = new OrderItemResponse(); // ← was MenuItemResponse
                    r.setId(item.getId());
                    r.setName(item.getName());
                    r.setDescription(item.getDescription());
                    r.setPrice(item.getPrice());
                    r.setImageUrl(item.getImageUrl());
                    r.setQuantity(entry.getValue());
                    // NOTE: no stockQuantity or available — not needed in order context
                    return r;
                })
                .collect(Collectors.toList());

        response.setOrderedItems(itemResponses);
        return response;
    }

    // ─── Place Order ───────────────────────────────────────
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OrderResponse placeOrder(Long userId, Long restaurantId,
                                    PlaceOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty())
            throw new BadRequestException("Order must contain at least one item");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant", restaurantId));

        restaurant.validateOperatingHours();

        double totalPrice = 0;
        int totalItemCount = 0;
        Map<Long, Integer> orderedItemsMap = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : request.getItems().entrySet()) {
            Long menuItemId = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity == null || quantity <= 0)
                throw new BadRequestException("Invalid quantity for item: " + menuItemId);

            MenuItem menuItem = menuItemRepository.findByIdWithLock(menuItemId)
                    .orElseThrow(() -> new NotFoundException("MenuItem", menuItemId));

            if (!menuItem.getRestaurant().getId().equals(restaurantId))
                throw new BadRequestException(
                        "MenuItem " + menuItemId + " does not belong to this restaurant");

            if (!menuItem.canOrder(quantity))
                throw new BadRequestException(
                        "Item '" + menuItem.getName() + "' only has " +
                                menuItem.getStockQuantity() + " left in stock");

            menuItem.reduceStock(quantity);
            menuItemRepository.save(menuItem);

            orderedItemsMap.put(menuItemId, quantity);

            totalPrice += menuItem.getPrice() * quantity;
            totalItemCount += quantity;
        }

        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setOrderedItems(orderedItemsMap);
        order.setItemCount(totalItemCount);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PLACED);

        return toResponse(orderRepository.save(order));
    }

    // ─── Other methods ─────────────────────────────────────
    @Override
    public OrderResponse getOrderById(Long orderId) {
        return toResponse(orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order", orderId)));
    }

    @Override
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUser_Id(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order", orderId));

        if (order.getStatus() == OrderStatus.DELIVERED)
            throw new InvalidOrderStateException("Cannot update order - already delivered");
        if (order.getStatus() == OrderStatus.CANCELLED)
            throw new InvalidOrderStateException("Cannot update order - already cancelled");

        if (newStatus == OrderStatus.CANCELLED)
            restoreInventory(order);

        if (newStatus == OrderStatus.DELIVERED)
            order.setDeliveryAt(LocalDateTime.now());

        order.setStatus(newStatus);
        return toResponse(orderRepository.save(order));
    }

    private void restoreInventory(Order order) {
        for (Map.Entry<Long, Integer> entry : order.getOrderedItems().entrySet()) {
            MenuItem menuItem = menuItemRepository.findByIdWithLock(entry.getKey())
                    .orElseThrow(() -> new NotFoundException("MenuItem", entry.getKey()));
            menuItem.restoreStock(entry.getValue());
            menuItemRepository.save(menuItem);
        }
    }
}