package com.example.orderservice.dto.order.inventory;

import java.time.LocalDateTime;

public record InventoryResponse(
        Long id,
        Long productId,
        Integer quantity,
        Integer reservedQuantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}