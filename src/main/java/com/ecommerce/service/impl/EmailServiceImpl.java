package com.ecommerce.service.impl;

import com.ecommerce.model.Order;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * Send order confirmation email
     */
    @Override
    public void sendOrderConfirmationEmail(Long orderId, Long userId) {
        log.info("📧 Sending order confirmation");

        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Product product = productRepository.findById(order.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            String subject = "Order Confirmation #" + orderId;
            String body = "Dear " + user.getUsername() + ",\n\n" +
                    "Thank you for your order!\n\n" +
                    "Order Details:\n" +
                    "- Order ID: " + orderId + "\n" +
                    "- Product: " + product.getName() + "\n" +
                    "- Quantity: " + order.getQuantity() + "\n" +
                    "- Total Price: $" + order.getTotalPrice() + "\n\n" +
                    "We will ship your order soon.\n\n" +
                    "Best regards,\n" +
                    "E-Commerce Team";

            sendEmail(user.getEmail(), subject, body);
            log.info("✅ Confirmation email sent");

        } catch (Exception e) {
            log.error("❌ Error sending confirmation: {}", e.getMessage());
        }
    }

    /**
     * Send order shipped email
     */
    @Override
    public void sendOrderShippedEmail(Long orderId, Long userId) {
        log.info("📧 Sending shipped email");

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String subject = "Your Order #" + orderId + " Has Been Shipped!";
            String body = "Dear " + user.getUsername() + ",\n\n" +
                    "Great news! Your order has been shipped!\n\n" +
                    "Order ID: " + orderId + "\n" +
                    "Tracking: TRACK-" + orderId + "-12345\n\n" +
                    "Your package will arrive within 3-5 business days.\n\n" +
                    "E-Commerce Team";

            sendEmail(user.getEmail(), subject, body);
            log.info("✅ Shipped email sent");

        } catch (Exception e) {
            log.error("❌ Error sending shipped email: {}", e.getMessage());
        }
    }

    /**
     * Send order delivered email
     */
    @Override
    public void sendOrderDeliveredEmail(Long orderId, Long userId) {
        log.info("📧 Sending delivered email");

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String subject = "Your Order #" + orderId + " Has Been Delivered!";
            String body = "Dear " + user.getUsername() + ",\n\n" +
                    "Your order has been delivered!\n\n" +
                    "Order ID: " + orderId + "\n\n" +
                    "Thank you for shopping with us!\n\n" +
                    "E-Commerce Team";

            sendEmail(user.getEmail(), subject, body);
            log.info("✅ Delivered email sent");

        } catch (Exception e) {
            log.error("❌ Error sending delivered email: {}", e.getMessage());
        }
    }

    /**
     * Generic method to send email
     */
    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@ecommerce.com");

            javaMailSender.send(message);
            log.info("✉️  Email sent to: {}", to);

        } catch (Exception e) {
            log.error("❌ Failed to send email: {}", e.getMessage());
        }
    }
}