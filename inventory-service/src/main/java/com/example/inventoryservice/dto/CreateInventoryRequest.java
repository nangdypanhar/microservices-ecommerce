package com.example.inventoryservice.dto;

public record CreateInventoryRequest(
         Long productId,
         Integer quantity
){
}
