package com.restaurantmanagement.order_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    // Many orders belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many orders belong to one restaurant
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    // Stores menuItemId → quantity to properly handle ordering the same item multiple times.
    // @ManyToMany was removed because it uses a join table with a unique constraint on
    // (order_id, menu_item_id), which silently deduplicates rows when the same item
    // appears more than once, making orderedItems.size() ≠ itemCount.
    @ElementCollection
    @CollectionTable(name = "order_menu_items",
            joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "menu_item_id")
    @Column(name = "quantity")
    private Map<Long, Integer> orderedItems = new HashMap<>();

    private int itemCount; // Can be calculated from orderedItems.size()

    private double totalPrice;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime orderAt;

    @Column  // Set explicitly only when status transitions to DELIVERED
    private LocalDateTime deliveryAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // Getters and Setters
}