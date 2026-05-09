package com.example.products.repository;

import com.example.products.model.InventoryBatch;
import com.example.products.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, Long> {
    // Find batches with remaining stock, ordered by ID (assuming sequential purchase) or purchase date
    List<InventoryBatch> findByProductAndRemainingQuantityGreaterThanOrderByPurchasePurchaseDateAsc(Product product, Integer quantity);

    @org.springframework.data.jpa.repository.Query("SELECT b FROM InventoryBatch b " +
            "LEFT JOIN FETCH b.product p " +
            "LEFT JOIN FETCH p.shop s " +
            "LEFT JOIN FETCH b.purchase pur " +
            "WHERE s.owner.email = :email")
    List<InventoryBatch> findAllByOwnerEmail(String email);
}
