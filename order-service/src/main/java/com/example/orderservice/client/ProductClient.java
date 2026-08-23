package com.example.orderservice.client;

import com.example.orderservice.dto.order.product.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final RestClient productRestClient;

    public List<ProductResponse> getProducts(List<Long> productIds) {

        return productRestClient
                .post()
                .uri("/api/v1/products/batch")
                .body(productIds)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductResponse>>() {});
    }
}