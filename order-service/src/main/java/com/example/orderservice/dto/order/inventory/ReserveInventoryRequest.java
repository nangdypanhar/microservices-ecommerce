package com.example.orderservice.dto.order.inventory;

public record ReserveInventoryRequest(
        Long productId,
        Integer quantity
) {
}