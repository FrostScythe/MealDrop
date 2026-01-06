package com.restaurantmanagement.order_api.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Long Id) {
        super(Id+" not found");
    }
}