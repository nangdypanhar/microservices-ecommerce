package com.example.orderservice.client;

import com.example.orderservice.dto.order.inventory.InventoryResponse;
import com.example.orderservice.dto.order.inventory.ReserveInventoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryClient {

    private final RestClient inventoryRestClient;

    public List<InventoryResponse> getInventories(
            List<Long> productIds
    ) {
        return inventoryRestClient
                .post()
                .uri("/api/v1/inventories/batch")
                .body(productIds)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public InventoryResponse reserveInventory(
            ReserveInventoryRequest request
    ) {
        return inventoryRestClient
                .post()
                .uri("/api/v1/inventories/reserve")
                .body(request)
                .retrieve()
                .body(InventoryResponse.class);
    }
}
