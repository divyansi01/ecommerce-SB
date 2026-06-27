package com.ecommerce.service;

import com.ecommerce.model.ProductDTO;
import com.ecommerce.model.ProductRequest;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(final ProductRequest productRequest);

    ProductDTO updateProduct(final Long Id, final ProductRequest productRequest);

    ProductDTO getProductById(final Long Id);

    List<ProductDTO> getAllActiveProducts();

    List<ProductDTO> getProductsByCategory(String category);

    ProductDTO updateStock(final Long id, final Integer quantity);

    void deleteProduct(final Long id);
}
