package com.ecommerce.controller;

import com.ecommerce.model.ApiResponse;
import com.ecommerce.model.OrderDTO;
import com.ecommerce.model.OrderRequest;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(
        value = "/v1",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(final OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping(value = "/order/create")
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderService.createOrder(request), "Order created successfully"));
    }

    @PutMapping("/order/ship/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> shipOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.shipOrder(orderId), "Order shipped"));
    }

    @PutMapping("/order/deliver/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> deliverOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.deliverOrder(orderId), "Order delivered"));
    }

    @PutMapping("/order/cancel/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.cancelOrder(orderId), "Order cancelled"));
    }
}
