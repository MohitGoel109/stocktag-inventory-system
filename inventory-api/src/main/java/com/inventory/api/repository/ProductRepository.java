package com.inventory.api.repository;

import com.inventory.api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Product> searchByName(String term);

    @Query("SELECT p FROM Product p WHERE p.quantity <= :threshold ORDER BY p.quantity ASC")
    List<Product> findLowStock(int threshold);

    long countByCategoryId(Long categoryId);
}
