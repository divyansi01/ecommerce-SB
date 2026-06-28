package com.ecommerce.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    private static final String TOPIC = "order-events";

    public void  publishOrderEvent(final OrderEvent orderEvent){
        try{
            logger.info("Publishing Order Event for Order id : {}", orderEvent.getOrderId());
            // Send message to Kafka topic
            kafkaTemplate.send(TOPIC, String.valueOf(orderEvent.getOrderId()), orderEvent);

            logger.info("Order Event Published Successfully");
        }catch (Exception e){
            logger.info("Failed to publish Order event: {}", e.getMessage());
        }
    }
}
