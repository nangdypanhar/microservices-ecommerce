package com.example.orderservice.dto.order;

import com.example.orderservice.entity.OrderStatus;
import java.math.BigDecimal;

public record UpdateOrderRequest(
        BigDecimal totalAmount,
        OrderStatus orderStatus

) {
}