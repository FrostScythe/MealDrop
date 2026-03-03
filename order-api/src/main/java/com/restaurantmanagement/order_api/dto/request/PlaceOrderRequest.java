package com.restaurantmanagement.order_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlaceOrderRequest {

    @NotEmpty(message = "Order must contain at least one item")
    private Map
    @NotNull Long,
    @Min(1) Integer> items;

    // getter & setter
}