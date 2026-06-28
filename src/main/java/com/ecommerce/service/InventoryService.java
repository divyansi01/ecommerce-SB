package com.ecommerce.service;

public interface InventoryService {
    void reduceStock(Long productId, Integer quantity);
    void restoreStock(Long productId, Integer quantity);
}
