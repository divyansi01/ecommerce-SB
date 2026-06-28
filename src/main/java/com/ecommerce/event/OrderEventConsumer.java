package com.ecommerce.event;

import com.ecommerce.service.EmailService;
import com.ecommerce.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final EmailService emailService;
    private final InventoryService inventoryService;

    /**
     * Email Service Consumer
     * Listens for order events and sends emails
     */
    @KafkaListener(
            topics = "order-events",
            groupId = "email-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderEventForEmail(OrderEvent event) {
        logger.info("📧 [Email Service] Received order event: {}", event.getOrderId());

        try {
            switch(event.getAction()) {
                case "ORDER_CREATED":
                    // Send order confirmation email
                    logger.info("   Sending order confirmation email");
                    emailService.sendOrderConfirmationEmail(
                            event.getOrderId(),
                            event.getUserId()
                    );
                    break;

                case "ORDER_SHIPPED":
                    // Send shipping notification email
                    logger.info("   Sending shipping notification");
                    emailService.sendOrderShippedEmail(event.getOrderId(), event.getUserId());
                    break;

                case "ORDER_DELIVERED":
                    // Send delivery confirmation email
                    logger.info("   Sending delivery confirmation");
                    emailService.sendOrderDeliveredEmail(event.getOrderId(), event.getUserId());
                    break;
            }
            logger.info("✅ Email event processed successfully");

        } catch (Exception e) {
            logger.error("❌ Failed to process email event: {}", e.getMessage());
        }
    }

    /**
     * Inventory Service Consumer
     * Listens for order events and updates stock
     */
    @KafkaListener(
            topics = "order-events",
            groupId = "inventory-service"
    )
    public void consumeOrderEventForInventory(OrderEvent event) {
        logger.info("📦 [Inventory Service] Received order event: {}", event.getOrderId());

        try {
            if ("ORDER_CREATED".equals(event.getAction())) {
                // Reduce stock quantity
                logger.info("   Reducing inventory for product: {}", event.getProductId());
                inventoryService.reduceStock(
                        event.getProductId(),
                        event.getQuantity()
                );
            }
            logger.info("✅ Inventory event processed successfully");

        } catch (Exception e) {
            logger.error("❌ Failed to process inventory event: {}", e.getMessage());
        }
    }
}
