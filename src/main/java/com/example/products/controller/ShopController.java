package com.example.products.controller;

import com.example.products.model.Shop;
import com.example.products.model.User;
import com.example.products.repository.ShopRepository;
import com.example.products.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Shop> getAllShops(Principal principal) {
        return shopRepository.findByOwnerEmail(principal.getName());
    }

    @PostMapping
    public ResponseEntity<Shop> createShop(@RequestBody Shop shop, Principal principal) {
        User owner = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        shop.setOwner(owner);
        return ResponseEntity.ok(shopRepository.save(shop));
    }
}
