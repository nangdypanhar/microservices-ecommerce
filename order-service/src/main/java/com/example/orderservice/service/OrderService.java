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
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    public OrderResponse createOrder(CreateOrderRequest request) {

        List<Long> productIds = request.items()
                .stream()
                .map(OrderItemRequest::productId)
                .toList();

        // Get all products in one request
        List<ProductResponse> products =
                productClient.getProducts(productIds);

        Map<Long, ProductResponse> productMap = products.stream()
                .collect(Collectors.toMap(
                        ProductResponse::id,
                        product -> product
                ));

        // Get all inventories in one request
        List<InventoryResponse> inventories =
                inventoryClient.getInventories(productIds);

        Map<Long, InventoryResponse> inventoryMap =
                inventories.stream()
                        .collect(Collectors.toMap(
                                InventoryResponse::productId,
                                inventory -> inventory
                        ));

        Order order = Order.builder()
                .userId(request.userId())
                .totalAmount(BigDecimal.ZERO)
                .orderStatus(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<OrderItem> items = request.items()
                .stream()
                .map(itemRequest -> {

                    Long productId = itemRequest.productId();

                    ProductResponse product =
                            productMap.get(productId);

                    if (product == null) {
                        throw new RuntimeException(
                                "Product not found: " + productId
                        );
                    }

                    InventoryResponse inventory =
                            inventoryMap.get(productId);

                    if (inventory == null) {
                        throw new RuntimeException(
                                "Inventory not found for product: "
                                        + productId
                        );
                    }

                    int availableQuantity =
                            inventory.quantity()
                                    - inventory.reservedQuantity();

                    if (itemRequest.quantity() > availableQuantity) {
                        throw new RuntimeException(
                                "Insufficient inventory for product: "
                                        + productId
                        );
                    }

                    // Reserve inventory
                    inventoryClient.reserveInventory(
                            new ReserveInventoryRequest(
                                    productId,
                                    itemRequest.quantity()
                            )
                    );

                    return OrderItem.builder()
                            .productId(product.id())
                            .quantity(itemRequest.quantity())
                            .unitPrice(product.price())
                            .order(order)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                })
                .toList();

        order.setItems(items);

        BigDecimal totalAmount = items.stream()
                .map(item ->
                        item.getUnitPrice()
                                .multiply(
                                        BigDecimal.valueOf(
                                                item.getQuantity()
                                        )
                                )
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
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
