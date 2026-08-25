package com.example.orderservice.service;

import com.example.orderservice.client.InventoryClient;
import com.example.orderservice.client.ProductClient;
import com.example.orderservice.dto.order.CreateOrderRequest;
import com.example.orderservice.dto.order.OrderResponse;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderCreationService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    public OrderResponse create(CreateOrderRequest request) {

        List<Long> productIds = getProductIds(request);

        Map<Long, ProductResponse> productMap =
                getProductMap(productIds);

        Map<Long, InventoryResponse> inventoryMap =
                getInventoryMap(productIds);

        Order order = buildOrder(request);

        List<OrderItem> items =
                createOrderItems(
                        request,
                        order,
                        productMap,
                        inventoryMap
                );

        order.setItems(items);
        order.setTotalAmount(calculateTotal(items));

        return saveOrder(order);
    }

    private List<Long> getProductIds(
            CreateOrderRequest request
    ) {
        return request.items()
                .stream()
                .map(OrderItemRequest::productId)
                .toList();
    }

    private Map<Long, ProductResponse> getProductMap(
            List<Long> productIds
    ) {
        return productClient.getProducts(productIds)
                .stream()
                .collect(Collectors.toMap(
                        ProductResponse::id,
                        Function.identity()
                ));
    }

    private Map<Long, InventoryResponse> getInventoryMap(
            List<Long> productIds
    ) {
        return inventoryClient.getInventories(productIds)
                .stream()
                .collect(Collectors.toMap(
                        InventoryResponse::productId,
                        Function.identity()
                ));
    }

    private Order buildOrder(
            CreateOrderRequest request
    ) {
        LocalDateTime now = LocalDateTime.now();

        return Order.builder()
                .userId(request.userId())
                .totalAmount(BigDecimal.ZERO)
                .orderStatus(OrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private List<OrderItem> createOrderItems(
            CreateOrderRequest request,
            Order order,
            Map<Long, ProductResponse> productMap,
            Map<Long, InventoryResponse> inventoryMap
    ) {
        return request.items()
                .stream()
                .map(itemRequest ->
                        createOrderItem(
                                itemRequest,
                                order,
                                productMap,
                                inventoryMap
                        )
                )
                .toList();
    }

    private OrderItem createOrderItem(
            OrderItemRequest itemRequest,
            Order order,
            Map<Long, ProductResponse> productMap,
            Map<Long, InventoryResponse> inventoryMap
    ) {

        Long productId = itemRequest.productId();

        ProductResponse product =
                getProduct(productId, productMap);

        InventoryResponse inventory =
                getInventory(productId, inventoryMap);

        validateInventory(
                productId,
                itemRequest.quantity(),
                inventory
        );

        reserveInventory(
                productId,
                itemRequest.quantity()
        );

        LocalDateTime now = LocalDateTime.now();

        return OrderItem.builder()
                .productId(product.id())
                .quantity(itemRequest.quantity())
                .unitPrice(product.price())
                .order(order)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private ProductResponse getProduct(
            Long productId,
            Map<Long, ProductResponse> productMap
    ) {
        ProductResponse product =
                productMap.get(productId);

        if (product == null) {
            throw new RuntimeException(
                    "Product not found: " + productId
            );
        }

        return product;
    }

    private InventoryResponse getInventory(
            Long productId,
            Map<Long, InventoryResponse> inventoryMap
    ) {
        InventoryResponse inventory =
                inventoryMap.get(productId);

        if (inventory == null) {
            throw new RuntimeException(
                    "Inventory not found for product: "
                            + productId
            );
        }

        return inventory;
    }

    private void validateInventory(
            Long productId,
            Integer requestedQuantity,
            InventoryResponse inventory
    ) {
        int availableQuantity =
                inventory.quantity()
                        - inventory.reservedQuantity();

        if (requestedQuantity > availableQuantity) {
            throw new RuntimeException(
                    "Insufficient inventory for product: "
                            + productId
            );
        }
    }

    private void reserveInventory(
            Long productId,
            Integer quantity
    ) {
        inventoryClient.reserveInventory(
                new ReserveInventoryRequest(
                        productId,
                        quantity
                )
        );
    }

    private BigDecimal calculateTotal(
            List<OrderItem> items
    ) {
        return items.stream()
                .map(item ->
                        item.getUnitPrice()
                                .multiply(
                                        BigDecimal.valueOf(
                                                item.getQuantity()
                                        )
                                )
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private OrderResponse saveOrder(Order order) {

        Order savedOrder =
                orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }
}