package com.example.orderservice.dto.order;

import com.example.orderservice.dto.order.orderItem.OrderItemResponse;
import com.example.orderservice.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse (
        Long id,
        Long userId,
        BigDecimal totalAmount,
        OrderStatus status,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
        ){

}