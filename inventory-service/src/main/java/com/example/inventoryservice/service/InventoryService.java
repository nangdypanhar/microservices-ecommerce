package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.CreateInventoryRequest;
import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.dto.UpdateInventoryRequest;
import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.mapper.InventoryMapper;
import com.example.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryResponse createInventory(
            CreateInventoryRequest request
    ) {

        LocalDateTime now = LocalDateTime.now();

        Inventory inventory = Inventory.builder()
                .productId(request.productId())
                .quantity(request.quantity())
                .reservedQuantity(0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Inventory savedInventory =
                inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(savedInventory);
    }

    public InventoryResponse getInventoryByProductId(Long productId) {

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory not found for product: " + productId
                        )
                );
        return inventoryMapper.toResponse(inventory);
    }


    public InventoryResponse getInventoryById(Long id) {

        Inventory inventory = inventoryRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory not found for product: " + id
                        )
                );
        return inventoryMapper.toResponse(inventory);
    }

    public InventoryResponse updateInventory(Long id, UpdateInventoryRequest request){
            Inventory inventory = inventoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Inventory not found with di : " + id));

            if (request.productId() !=null ){
                inventory.setProductId(request.productId());
            }

        if (request.quantity() !=null ){
            inventory.setQuantity(request.quantity());
        }

        if (request.reservedQuantity() !=null ){
            inventory.setReservedQuantity(request.reservedQuantity());
        }

        return inventoryMapper.toResponse(inventory);
    }

    public void deleteInventory(Long id){
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        inventoryRepository.delete(inventory);
    }
}
