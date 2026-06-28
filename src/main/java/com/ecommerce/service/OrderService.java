package com.ecommerce.service;

import com.ecommerce.model.OrderDTO;
import com.ecommerce.model.OrderRequest;
import com.ecommerce.model.OrderStatus;

public interface OrderService {
    OrderDTO createOrder(final OrderRequest orderRequest);
    OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus);
    OrderDTO shipOrder(Long orderId);
    OrderDTO deliverOrder(Long orderId);

    OrderDTO cancelOrder(Long orderId);
}
