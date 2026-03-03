package com.restaurantmanagement.order_api.entity;

import com.restaurantmanagement.order_api.exception.BadRequestException;
import jakarta.persistence.*;

@Entity
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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}