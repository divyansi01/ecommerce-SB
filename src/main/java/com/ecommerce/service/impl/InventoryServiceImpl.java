package com.ecommerce.service.impl;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;

    /**
     * Reduce stock when order is created
     */
    @Transactional
    @Override
    public void reduceStock(Long productId, Integer quantity) {
        log.info("📦 [InventoryService] Reducing stock for product: {}, quantity: {}",
                productId, quantity);

        try {
            // Find product
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Check if enough stock
            if (product.getStockQuantity() < quantity) {
                log.error("❌ Insufficient stock! Available: {}, Requested: {}",
                        product.getStockQuantity(), quantity);
                return;
            }

            // Reduce stock
            int newStock = product.getStockQuantity() - quantity;
            product.setStockQuantity(newStock);
            productRepository.save(product);

            log.info("✅ Stock reduced! New stock: {}", newStock);

        } catch (Exception e) {
            log.error("❌ Error reducing stock: {}", e.getMessage());
        }
    }

    /**
     * Restore stock when order is cancelled
     */
    @Transactional
    @Override
    public void restoreStock(Long productId, Integer quantity) {
        log.info("♻️ Restoring stock for product: {}, quantity: {}", productId, quantity);

        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            int newStock = product.getStockQuantity() + quantity;
            product.setStockQuantity(newStock);
            productRepository.save(product);

            log.info("✅ Stock restored! New stock: {}", newStock);

        } catch (Exception e) {
            log.error("❌ Error restoring stock: {}", e.getMessage());
        }
    }
}