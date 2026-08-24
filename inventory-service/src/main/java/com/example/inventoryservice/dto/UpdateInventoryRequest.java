package com.example.inventoryservice.dto;

public record UpdateInventoryRequest(
        Long productId,
        Integer quantity,
        Integer reservedQuantity
) {
}



