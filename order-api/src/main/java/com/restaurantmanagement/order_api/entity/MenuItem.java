package com.restaurantmanagement.order_api.entity;

import com.restaurantmanagement.order_api.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "menu_item")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double price;

    // Inventory management fields
    @Column(nullable = false)
    private Integer stockQuantity = 20; // Default: 100 items in stock

    @Column(nullable = false)
    private boolean available = true; // Is item available for ordering?

    // Many menu items belong to one restaurant
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column
    private String imageUrl;

    // NEW: Business logic methods for inventory
    public boolean canOrder(int quantity) {
        return available && stockQuantity != null && stockQuantity >= quantity;
    }

    public void reduceStock(int quantity) {
        if (!canOrder(quantity)) {
            throw new BadRequestException(
                    "Insufficient stock for item: " + name +
                            ". Available: " + (stockQuantity != null ? stockQuantity : 0));
        }
        this.stockQuantity -= quantity;
        if (this.stockQuantity == 0) {
            this.available = false;
        }
    }

    public void restoreStock(int quantity) {
        this.stockQuantity += quantity;
        this.available = true;
    }
}