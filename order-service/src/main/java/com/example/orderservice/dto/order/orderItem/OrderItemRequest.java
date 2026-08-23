package com.example.orderservice.dto.order.orderItem;

public record OrderItemRequest(
        Long productId,
        Integer quantity
) {}