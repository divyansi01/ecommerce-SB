package com.ecommerce.service;

import com.ecommerce.model.ProductDTO;
import com.ecommerce.model.ProductRequest;
import com.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ProductService Tests")
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Get Product By Id")
    public void getProductById() {
        ProductDTO product = productService.getProductById(1L);

        assertAll("productById",
                () -> assertNotNull(product),
                () -> assertEquals(product.getId(), 1L),
                () -> assertEquals(product.getName(), "Laptop"),
                () -> assertEquals(product.getPrice(), BigDecimal.valueOf(999.99)));
    }

    @Test
    @DisplayName("Get Product By Id - Verify Stock")
    public void getProductById_VerifyStock() {
        ProductDTO product = productService.getProductById(1L);

        assertAll("productStock",
                () -> assertNotNull(product),
                () -> assertEquals(product.getStockQuantity(), 50));
    }

    @Test
    @DisplayName("Get Product By Invalid Id - Should Throw Exception")
    public void getProductById_InvalidId() {
        assertThrows(Exception.class, () -> {
            productService.getProductById(999L);
        });
    }

    @Test
    @DisplayName("Get All Active Products")
    public void getAllActiveProducts() {
        List<ProductDTO> products = productService.getAllActiveProducts();

        assertAll("allActiveProducts",
                () -> assertNotNull(products),
                () -> assertTrue(products.size() > 0),
                () -> assertEquals(products.size(), 4),
                () -> assertTrue(products.stream()
                        .anyMatch(p -> p.getName().equals("Laptop"))));
    }

    @Test
    @DisplayName("Get All Active Products - Verify Data From H2")
    public void getAllActiveProducts_VerifyData() {
        List<ProductDTO> products = productService.getAllActiveProducts();

        assertAll("verifyTestData",
                () -> assertEquals(products.size(), 4),
                () -> assertTrue(products.stream().anyMatch(p -> p.getName().equals("Laptop"))),
                () -> assertTrue(products.stream().anyMatch(p -> p.getName().equals("Mouse"))),
                () -> assertTrue(products.stream().anyMatch(p -> p.getName().equals("Keyboard"))),
                () -> assertTrue(products.stream().anyMatch(p -> p.getName().equals("Book"))));
    }

    @Test
    @DisplayName("Get Products By Category")
    public void getProductsByCategory() {
        List<ProductDTO> electronics = productService.getProductsByCategory("Electronics");

        assertAll("productsByCategory",
                () -> assertNotNull(electronics),
                () -> assertEquals(electronics.size(), 3),
                () -> assertTrue(electronics.stream()
                        .allMatch(p -> p.getCategory().equals("Electronics"))));
    }

    @Test
    @DisplayName("Get Products By Category - Books")
    public void getProductsByCategory_Books() {
        List<ProductDTO> books = productService.getProductsByCategory("Books");

        assertAll("booksByCategory",
                () -> assertNotNull(books),
                () -> assertEquals(books.size(), 1),
                () -> assertEquals(books.get(0).getName(), "Book"));
    }

    @Test
    @DisplayName("Create Product")
    public void createProduct() {
        ProductRequest request = ProductRequest.builder()
                .name("Monitor")
                .description("4K Monitor")
                .price(BigDecimal.valueOf(399.99))
                .stockQuantity(25)
                .category("Electronics")
                .build();

        long initialCount = productRepository.count();

        ProductDTO createdProduct = productService.createProduct(request);

        assertAll("createProduct",
                () -> assertNotNull(createdProduct),
                () -> assertNotNull(createdProduct.getId()),
                () -> assertEquals(createdProduct.getName(), "Monitor"),
                () -> assertEquals(createdProduct.getPrice(), BigDecimal.valueOf(399.99)),
                () -> assertEquals(productRepository.count(), initialCount + 1));
    }

    @Test
    @DisplayName("Create Product - Verify in H2 Database")
    public void createProduct_VerifyInDatabase() {
        ProductRequest request = ProductRequest.builder()
                .name("Webcam")
                .description("1080p Webcam")
                .price(BigDecimal.valueOf(49.99))
                .stockQuantity(100)
                .category("Electronics")
                .build();

        ProductDTO createdProduct = productService.createProduct(request);

        ProductDTO fetchedProduct = productService.getProductById(createdProduct.getId());
        assertAll("verifyInDatabase",
                () -> assertNotNull(fetchedProduct),
                () -> assertEquals(fetchedProduct.getName(), "Webcam"),
                () -> assertEquals(fetchedProduct.getCategory(), "Electronics"));
    }

    @Test
    @DisplayName("Update Product")
    public void updateProduct() {
        Long productId = 2L;
        ProductRequest updateRequest = ProductRequest.builder()
                .name("Wireless Mouse")
                .description("Updated description")
                .price(BigDecimal.valueOf(39.99))
                .stockQuantity(150)
                .category("Electronics")
                .build();

        ProductDTO updatedProduct = productService.updateProduct(productId, updateRequest);

        assertAll("updateProduct",
                () -> assertNotNull(updatedProduct),
                () -> assertEquals(updatedProduct.getId(), productId),
                () -> assertEquals(updatedProduct.getName(), "Wireless Mouse"),
                () -> assertEquals(updatedProduct.getPrice(), BigDecimal.valueOf(39.99)),
                () -> assertEquals(updatedProduct.getStockQuantity(), 150));
    }

    @Test
    @DisplayName("Update Product - Verify in H2 Database")
    public void updateProduct_VerifyInDatabase() {
        Long productId = 3L;
        ProductRequest updateRequest = ProductRequest.builder()
                .name("Mechanical Keyboard")
                .price(BigDecimal.valueOf(99.99))
                .stockQuantity(100)
                .category("Electronics")
                .build();

        ProductDTO updatedProduct = productService.updateProduct(productId, updateRequest);
        ProductDTO fetchedProduct = productService.getProductById(productId);

        assertAll("verifyUpdateInDatabase",
                () -> assertEquals(updatedProduct.getPrice(), BigDecimal.valueOf(99.99)),
                () -> assertEquals(fetchedProduct.getPrice(), BigDecimal.valueOf(99.99)),
                () -> assertEquals(fetchedProduct.getName(), "Mechanical Keyboard"));
    }

    @Test
    @DisplayName("Delete Product")
    public void deleteProduct() {
        Long productId = 4L;
        long initialCount = productRepository.count();

        ProductDTO product = productService.getProductById(productId);
        assertNotNull(product);

        productService.deleteProduct(productId);

        assertAll("deleteProduct",
                () -> assertEquals(productRepository.count(), initialCount - 1),
                () -> assertThrows(Exception.class, () -> productService.getProductById(productId)));
    }

    @Test
    @DisplayName("Database has correct test data")
    public void verifyTestDataLoaded() {
        long totalProducts = productRepository.count();

        assertAll("testDataLoaded",
                () -> assertTrue(totalProducts >= 4),
                () -> assertTrue(productRepository.findByIsActiveTrue().size() >= 4));
    }
}