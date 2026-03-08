package com.restaurantmanagement.order_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurantmanagement.order_api.exception.RestaurantClosedException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "restaurant")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    // Business hours fields
    @Column(nullable = false)
    private LocalTime openingTime;  // e.g., 09:00

    @Column(nullable = false)
    private LocalTime closingTime;  // e.g., 23:00

    @Column(nullable = false)
    private Integer preparationTimeMinutes = 30;  // Stop orders 30 mins before closing

    @Column(nullable = false)
    private boolean isOpen = true;  // Manual override (maintenance, etc.)

    // Relationships
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<MenuItem> menuItems;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> orders;

    // Business logic methods

    /**
     * Validates if restaurant can accept orders right now.
     * Throws RestaurantClosedException with detailed reason if closed.
     */
    public void validateOperatingHours() {
        // Check 1: Manual override (maintenance mode)
        if (!isOpen) {
            throw new RestaurantClosedException(
                    name,
                    openingTime,
                    closingTime,
                    getLastOrderTime(),
                    "Currently closed for maintenance. Please check back later."
            );
        }

        LocalTime now = LocalTime.now();
        LocalTime lastOrderTime = getLastOrderTime();

        // Check 2: Before opening time
        if (now.isBefore(openingTime)) {
            throw new RestaurantClosedException(
                    name,
                    openingTime,
                    closingTime,
                    lastOrderTime,
                    String.format("We open at %s. You can place orders then.", openingTime)
            );
        }

        // Check 3: After last order time
        if (now.isAfter(lastOrderTime)) {
            throw new RestaurantClosedException(
                    name,
                    openingTime,
                    closingTime,
                    lastOrderTime,
                    String.format("Kitchen is closed. Last order time was %s. We reopen tomorrow at %s.",
                            lastOrderTime, openingTime)
            );
        }

        // If we reach here, restaurant is open and accepting orders
    }

    /**
     * Check if restaurant is currently accepting orders (without throwing exception)
     * Useful for displaying restaurant status to users
     */
    public boolean isCurrentlyOpen() {
        if (!isOpen) {
            return false;
        }

        LocalTime now = LocalTime.now();
        LocalTime lastOrderTime = getLastOrderTime();

        return now.isAfter(openingTime) && now.isBefore(lastOrderTime);
    }

    /**
     * Get human-readable operating status
     */
    public String getOperatingStatus() {
        if (!isOpen) {
            return "Closed for maintenance";
        }

        LocalTime now = LocalTime.now();
        LocalTime lastOrderTime = getLastOrderTime();

        if (now.isBefore(openingTime)) {
            return String.format("Opens at %s", openingTime);
        } else if (now.isAfter(lastOrderTime)) {
            return String.format("Closed. Opens tomorrow at %s", openingTime);
        } else {
            return String.format("Open now. Last order at %s", lastOrderTime);
        }
    }

    /**
     * Calculate last order time (closing time - preparation time)
     */
    public LocalTime getLastOrderTime() {
        return closingTime.minusMinutes(preparationTimeMinutes);
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public Integer getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public void setPreparationTimeMinutes(Integer preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}