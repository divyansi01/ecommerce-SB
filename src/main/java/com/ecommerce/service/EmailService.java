package com.ecommerce.service;

public interface EmailService {
    void sendOrderConfirmationEmail(Long orderId, Long userId);

    void sendOrderDeliveredEmail(Long orderId, Long userId);

    void sendOrderShippedEmail(Long orderId, Long userId);
}
