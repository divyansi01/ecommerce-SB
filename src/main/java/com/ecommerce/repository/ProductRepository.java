package com.ecommerce.repository;

import com.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);

    List<Product> findByIsActiveTrue();

    Optional<Product> findByNameIgnoreCase(String name);

    @Query("""
SELECT p FROM Product p
WHERE LOWER(p.category) = LOWER(:category)
AND p.isActive = true
""")
    List<Product> findActiveByCategoryOptimized(String category);
}
