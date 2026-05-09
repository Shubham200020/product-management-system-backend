package com.example.products.service;

import com.example.products.model.*;
import com.example.products.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Purchase createPurchase(PurchaseRequest request, String ownerEmail) {
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        if (!shop.getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Access denied: Shop does not belong to you");
        }

        Purchase purchase = new Purchase();
        purchase.setShop(shop);
        purchase.setSupplier(request.getSupplier());
        purchase.setPurchaseDate(LocalDateTime.now());
        
        double totalCost = 0.0;
        List<InventoryBatch> batches = new ArrayList<>();

        for (PurchaseItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.getProductId()));

            InventoryBatch batch = new InventoryBatch();
            batch.setProduct(product);
            batch.setPurchase(purchase);
            batch.setQuantity(itemReq.getQuantity());
            batch.setRemainingQuantity(itemReq.getQuantity());
            batch.setCostPrice(itemReq.getCostPrice());
            batch.setExpiryDate(itemReq.getExpiryDate());
            
            batches.add(batch);
            totalCost += (itemReq.getCostPrice() * itemReq.getQuantity());

            // Update product MRP if provided
            if (itemReq.getMrp() != null && itemReq.getMrp() > 0) {
                product.setMrp(itemReq.getMrp());
                productRepository.save(product);
            }
        }

        purchase.setTotalCost(totalCost);
        purchase.setInventoryBatches(batches);

        return purchaseRepository.save(purchase);
    }

    public List<Purchase> getPurchasesByOwner(String ownerEmail) {
        return purchaseRepository.findByShopOwnerEmail(ownerEmail);
    }

    // DTOs for request
    public static class PurchaseRequest {
        private Long shopId;
        private String supplier;
        private List<PurchaseItemRequest> items;

        public Long getShopId() { return shopId; }
        public void setShopId(Long shopId) { this.shopId = shopId; }
        public String getSupplier() { return supplier; }
        public void setSupplier(String supplier) { this.supplier = supplier; }
        public List<PurchaseItemRequest> getItems() { return items; }
        public void setItems(List<PurchaseItemRequest> items) { this.items = items; }
    }

    public static class PurchaseItemRequest {
        private Long productId;
        private Integer quantity;
        private Double costPrice;
        private Double mrp;
        private LocalDate expiryDate;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Double getCostPrice() { return costPrice; }
        public void setCostPrice(Double costPrice) { this.costPrice = costPrice; }
        public Double getMrp() { return mrp; }
        public void setMrp(Double mrp) { this.mrp = mrp; }
        public LocalDate getExpiryDate() { return expiryDate; }
        public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    }
}
