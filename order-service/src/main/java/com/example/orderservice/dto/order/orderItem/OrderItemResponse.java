package com.example.orderservice.dto.order.orderItem;


import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        Integer quantity,
        BigDecimal unitPrice
) {
}