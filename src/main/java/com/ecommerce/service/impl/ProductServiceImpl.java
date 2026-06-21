package com.ecommerce.service.impl;

import com.ecommerce.model.Product;
import com.ecommerce.model.ProductDTO;
import com.ecommerce.model.ProductRequest;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.ProductService;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    @CacheEvict(value = {"products, products_category"}, allEntries = true)
    public ProductDTO createProduct(final ProductRequest productRequest){
        logger.info("Creating new product: {}", productRequest.getName());
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .category(productRequest.getCategory())
                .price(productRequest.getPrice())
                .stockQuantity(productRequest.getStockQuantity())
                .isActive(true)
                .build();
        return convertToDTO(productRepository.save(product));
    }

    @Transactional
    @Override
    @CacheEvict(value = {"product, products, products_category"}, allEntries = true)
    public ProductDTO updateProduct(final Long Id, final ProductRequest productRequest) throws Exception{
        logger.info("Updating product with Id: {}",Id);

        Product existingProduct = productRepository.findById(Id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found"));

        Product updatedProduct = Product.builder()
                .id(existingProduct.getId())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .stockQuantity(productRequest.getStockQuantity())
                .category(productRequest.getCategory())
                .isActive(existingProduct.getIsActive())
                .createdAt(existingProduct.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        return convertToDTO(productRepository.save(updatedProduct));
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public ProductDTO getProductById(final Long Id) throws Exception {
        logger.info("Fetching Product details of Product Id: {}", Id);
        return convertToDTO(productRepository.findById(Id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found")));
    }

    @Override
    @Cacheable(value = "products", key = "'all_active'")
    public List<ProductDTO> getAllActiveProducts(){
        logger.info("Fetching List of Active Products");
        return productRepository.findByIsActiveTrue()
                .stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "products_category", key = "#category")
    public List<ProductDTO> getProductsByCategory(String category) {
        logger.info("Fetching products by category: {}", category);
        return productRepository.findActiveByCategoryOptimized(category)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    @CacheEvict(value = {"product", "products"}, allEntries = true)
    public ProductDTO updateStock(final Long id, final Integer quantity) throws Exception{
        logger.info("Updating stock details for Product Id: {}",id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found"));

        Product updatedProduct = Product.builder()
                .id(existingProduct.getId())
                .stockQuantity(quantity)
                .updatedAt(LocalDateTime.now())
                .build();

        return convertToDTO(productRepository.save(updatedProduct));
    }

    @Transactional
    @Override
    @CacheEvict(value = {"product", "products", "products_category"}, allEntries = true)
    public void deleteProduct(final Long id) throws Exception{
        logger.info("Deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found"));

        productRepository.delete(product);
    }

    private ProductDTO convertToDTO(final Product product){
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .stockQuantity(product.getStockQuantity())
                .isActive(product.getIsActive())
                .price(product.getPrice())
                .build();
    }

}
