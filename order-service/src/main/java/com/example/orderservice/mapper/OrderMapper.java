package com.example.orderservice.mapper;
import com.example.orderservice.dto.order.OrderResponse;
import com.example.orderservice.dto.order.orderItem.OrderItemResponse;
import com.example.orderservice.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                items
        );
    }
}