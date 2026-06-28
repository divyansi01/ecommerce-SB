package com.ecommerce.service.impl;

import com.ecommerce.event.OrderEvent;
import com.ecommerce.event.OrderEventPublisher;
import com.ecommerce.model.*;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.OrderService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderEventPublisher orderEventPublisher;

    @Autowired
    public OrderServiceImpl(final OrderRepository orderRepository,
                            final ProductRepository productRepository,
                            final OrderEventPublisher orderEventPublisher){
        this.orderEventPublisher = orderEventPublisher;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public OrderDTO createOrder(final OrderRequest orderRequest){
        logger.info("Started Order Creation by User: {}", orderRequest.getUserId());

        Product product = productRepository.findById(orderRequest.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found"));

        if(product.getStockQuantity() < orderRequest.getQuantity()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient Stock");
        }

        Order order = Order.builder()
                .userId(orderRequest.getUserId())
                .productId(orderRequest.getProductId())
                .quantity(orderRequest.getQuantity())
                .totalPrice(product.getPrice().multiply(
                        new java.math.BigDecimal(orderRequest.getQuantity())))
                .status(OrderStatus.CREATED)
                .build();

        Order savedOrder = orderRepository.save(order);
        logger.info("Order created with id: {}", savedOrder.getId());

        OrderEvent event = OrderEvent.builder()
                .orderId(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .productId(savedOrder.getProductId())
                .quantity(savedOrder.getQuantity())
                .totalPrice(savedOrder.getTotalPrice())
                .status(savedOrder.getStatus().toString())
                .timestamp(LocalDateTime.now())
                .action("ORDER_CREATED")  // Other services listen for this
                .build();

        orderEventPublisher.publishOrderEvent(event);
        logger.info("Order Published to Kafka");

        return convertToDTO(savedOrder);
    }

    @Transactional
    @Override
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        logger.info("📝 Updating order {} to status: {}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        // Publish event for status change
        OrderEvent event = OrderEvent.builder()
                .orderId(updatedOrder.getId())
                .userId(updatedOrder.getUserId())
                .status(updatedOrder.getStatus().toString())
                .timestamp(LocalDateTime.now())
                .action("ORDER_STATUS_UPDATED")
                .build();

        orderEventPublisher.publishOrderEvent(event);

        return convertToDTO(updatedOrder);
    }

    @Transactional
    @Override
    public OrderDTO shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.SHIPPED);
        Order savedOrder = orderRepository.save(order);

        OrderEvent event = OrderEvent.builder()
                .orderId(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .status(savedOrder.getStatus().toString())
                .timestamp(LocalDateTime.now())
                .action("ORDER_SHIPPED")
                .build();

        orderEventPublisher.publishOrderEvent(event);
        return convertToDTO(savedOrder);
    }

    @Transactional
    @Override
    public OrderDTO deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.DELIVERED);
        Order savedOrder = orderRepository.save(order);

        OrderEvent event = OrderEvent.builder()
                .orderId(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .status(savedOrder.getStatus().toString())
                .timestamp(LocalDateTime.now())
                .action("ORDER_DELIVERED")
                .build();

        orderEventPublisher.publishOrderEvent(event);
        return convertToDTO(savedOrder);
    }

    @Transactional
    @Override
    public OrderDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        OrderEvent event = OrderEvent.builder()
                .orderId(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .productId(savedOrder.getProductId())
                .quantity(savedOrder.getQuantity())
                .status(savedOrder.getStatus().toString())
                .timestamp(LocalDateTime.now())
                .action("ORDER_CANCELLED")
                .build();

        orderEventPublisher.publishOrderEvent(event);
        return convertToDTO(savedOrder);
    }

    private OrderDTO convertToDTO(final Order order){
        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
