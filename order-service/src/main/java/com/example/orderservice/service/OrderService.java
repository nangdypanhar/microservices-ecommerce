package com.example.orderservice.service;

import com.example.orderservice.client.InventoryClient;
import com.example.orderservice.client.ProductClient;
import com.example.orderservice.dto.order.CreateOrderRequest;
import com.example.orderservice.dto.order.OrderResponse;
import com.example.orderservice.dto.order.UpdateOrderRequest;
import com.example.orderservice.dto.order.inventory.InventoryResponse;
import com.example.orderservice.dto.order.inventory.ReserveInventoryRequest;
import com.example.orderservice.dto.order.orderItem.OrderItemRequest;
import com.example.orderservice.dto.order.product.ProductResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderCreationService orderCreationService;

   public OrderResponse createOrder(CreateOrderRequest request){
       return orderCreationService.create(request);
   }

    public OrderResponse getOrderById(Long id) {
        Order order =  orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return orderMapper.toResponse(order);
    }

    public List<OrderResponse> getOrderByUserId(Long userId){
        List<Order> orderList = orderRepository.findByUserId(userId);
        return orderList.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public void deleteOrder(Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        orderRepository.delete(order);
    }

    public OrderResponse updateOrder(
            Long id,
            UpdateOrderRequest request) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found with id: " + id)
                );

        if (request.totalAmount() != null) {
            order.setTotalAmount(request.totalAmount());
        }

        if (request.orderStatus() != null) {
            order.setOrderStatus(request.orderStatus());
        }
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toResponse(updatedOrder);
    }

}
