package com.example.products.controller;

import com.example.products.model.Category;
import com.example.products.model.Shop;
import com.example.products.repository.CategoryRepository;
import com.example.products.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ShopRepository shopRepository;

    @GetMapping
    public List<Category> getAllCategories(Principal principal) {
        return categoryRepository.findByShopOwnerEmail(principal.getName());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category, Principal principal) {
        // Validate that the shop belongs to the current user
        if (category.getShop() != null) {
            Shop shop = shopRepository.findById(category.getShop().getId())
                    .orElseThrow(() -> new RuntimeException("Shop not found"));
            if (!shop.getOwner().getEmail().equals(principal.getName())) {
                throw new RuntimeException("Access denied: Shop does not belong to you");
            }
        }
        return ResponseEntity.ok(categoryRepository.save(category));
    }
}
