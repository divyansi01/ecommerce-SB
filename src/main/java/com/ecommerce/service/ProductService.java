package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.model.ProductDTO;
import com.ecommerce.model.ProductRequest;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(final ProductRequest productRequest);

    ProductDTO updateProduct(final Long Id, final ProductRequest productRequest) throws Exception;

    ProductDTO getProductById(final Long Id) throws Exception;

    List<ProductDTO> getAllActiveProducts();

    ProductDTO updateStock(final Long id, final Integer quantity) throws Exception;

    void deleteProduct(final Long id) throws Exception;
}
