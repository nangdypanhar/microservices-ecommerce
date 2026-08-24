package com.example.inventoryservice.mapper;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getQuantity(),
                inventory.getReservedQuantity(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }
}