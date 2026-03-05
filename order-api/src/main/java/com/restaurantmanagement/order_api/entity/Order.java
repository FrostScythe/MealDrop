package com.restaurantmanagement.order_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

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

    // Many-to-many relationship with MenuItem through join table
    @ManyToMany
    @JoinTable(
            name = "order_menu_items",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_item_id")
    )
    private List<MenuItem> orderedItems;

    private int itemCount; // Can be calculated from orderedItems.size()

    private double totalPrice;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime orderAt;

    @LastModifiedDate
    private LocalDateTime deliveryAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // Getters and Setters
}