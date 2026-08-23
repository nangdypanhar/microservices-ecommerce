package com.example.orderservice.controller;


import com.example.orderservice.dto.order.CreateOrderRequest;
import com.example.orderservice.dto.order.OrderResponse;
import com.example.orderservice.dto.order.UpdateOrderRequest;
import com.example.orderservice.dto.order.product.ProductResponse;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(order);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request) {

        OrderResponse order = orderService.createOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(
            @RequestParam Long userId) {

        List<OrderResponse> orders =
                orderService.getOrderByUserId(userId);

        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id){
        orderService.deleteOrder(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id,
            @RequestBody UpdateOrderRequest request) {

        OrderResponse order = orderService.updateOrder(id,request);
        return ResponseEntity.ok(order);
    }
}
