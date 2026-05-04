package com.example.products.controller;

import com.example.products.model.Purchase;
import com.example.products.service.PurchaseService;
import com.example.products.service.PurchaseService.PurchaseItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public List<Purchase> getPurchases(Principal principal) {
        return purchaseService.getPurchasesByOwner(principal.getName());
    }

    @PostMapping("/create")
    public ResponseEntity<Purchase> createPurchase(@RequestBody PurchaseService.PurchaseRequest request, Principal principal) {
        return ResponseEntity.ok(purchaseService.createPurchase(request, principal.getName()));
    }
}
