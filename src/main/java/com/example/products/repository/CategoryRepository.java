package com.example.products.repository;

import com.example.products.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByShopOwnerEmail(String email);
}
