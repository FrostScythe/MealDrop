package com.restaurantmanagement.order_api.dto.response;

import java.time.LocalTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RestaurantResponse {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Integer preparationTimeMinutes;
    private boolean isOpen;
    private String currentStatus;   // ← include the computed status string
    // getters & setters
}