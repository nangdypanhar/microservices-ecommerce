package com.example.orderservice.dto.order;


import com.example.orderservice.dto.order.orderItem.OrderItemRequest;

import java.util.List;

public record CreateOrderRequest (
        Long userId,
        List<OrderItemRequest> items
){

}
