package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.CreateInventoryRequest;
import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.dto.UpdateInventoryRequest;
import com.example.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("{id}")
    public ResponseEntity<InventoryResponse> getInventoryById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                inventoryService.getInventoryById(id)
        );
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(
            @RequestBody CreateInventoryRequest request
    ) {

        InventoryResponse inventory =
                inventoryService.createInventory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(inventory);
    }

    @GetMapping
    public ResponseEntity<InventoryResponse> getInventoryByProductId(
            @RequestParam Long productId
    ) {
        return ResponseEntity.ok(
                inventoryService.getInventoryByProductId(productId)
        );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id){
        inventoryService.deleteInventory(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> updateInventory(@PathVariable Long id,
                                                         @RequestBody UpdateInventoryRequest request) {

        InventoryResponse inventoryResponse = inventoryService.updateInventory(id,request);
        return ResponseEntity.ok(inventoryResponse);
    }



}