package com.example.inventoryservice.dto;

public record ReserveInventoryRequest(
        Long productId,
        Integer quantity
) {
}