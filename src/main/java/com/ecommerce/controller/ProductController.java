package com.ecommerce.controller;

import com.ecommerce.model.ApiResponse;
import com.ecommerce.model.ProductDTO;
import com.ecommerce.model.ProductRequest;
import com.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(
        value = "/v1",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(final ProductService productService){
        this.productService = productService;
    }

    @PostMapping(value = "/product/create")
    @Operation(summary = "This endpoint is responsible for creating a Product")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestBody ProductRequest productRequest){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(productService.createProduct(productRequest), "Product created successfully."));
    }

    @PutMapping(value = "/product/{id}")
    @Operation(summary = "This endpoint is responsible for updating a Product")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable Long Id, @RequestBody ProductRequest productRequest) throws Exception{
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(productService.updateProduct(Id, productRequest), "Product updated successfully."));
    }

    @GetMapping(value = "/product/{id}")
    @Operation(summary = "This endpoint is responsible for getting details of Product based on Id")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long Id) throws Exception {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(Id), "Product retrieved successfully"));
    }

    @GetMapping(value = "/product")
    @Operation(summary = "This endpoint is responsible for getting list of all products")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts(){
        return ResponseEntity.ok(ApiResponse.success(productService.getAllActiveProducts(), "Products retrieved successfully"));
    }

    @PatchMapping(value = "/{id}/stock")
    @Operation(summary = "This endpoint is responsible for updating stock of a product")
    public ResponseEntity<ApiResponse<ProductDTO>> updateStock(@PathVariable Long Id, @RequestParam Integer quantity) throws Exception{
        return ResponseEntity.ok(ApiResponse.success(productService.updateStock(Id, quantity), "Updated stock successfully"));
    }

    @DeleteMapping(value = "/product/{id}")
    @Operation(summary = "This endpoint is responsible for deleting a product")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long Id) throws Exception{
        productService.deleteProduct(Id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Product deleted successfully")
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(
                ApiResponse.success(productService.getProductsByCategory(category), "Products retrieved successfully")
        );
    }
}
