package com.example.products.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;
import java.util.Optional;

@Entity
@Table(name = "products")
public class Product {
    
    public Product() {}
    
    public Product(Long id, String name, String sku, String brand, ProductType type, boolean isActive, LocalDateTime createdAt, Category category, Shop shop, List<InventoryBatch> inventoryBatches, List<SalesItem> salesItems) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.brand = brand;
        this.type = type;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.category = category;
        this.shop = shop;
        this.inventoryBatches = inventoryBatches;
        this.salesItems = salesItems;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String sku;

    private String brand;

    @Enumerated(EnumType.STRING)
    private ProductType type;

    private boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Shop shop;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<InventoryBatch> inventoryBatches = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SalesItem> salesItems = new java.util.ArrayList<>();

    @Transient
    private Integer availableStock;

    @Transient
    private Double recommendedDiscount;

    public Double getRecommendedDiscount() {
        if (recommendedDiscount != null) return recommendedDiscount;
        // Basic logic fallback: if expired or near expiry, suggest high discount
        String status = getStockStatus();
        if ("EXPIRED".equals(status)) return 50.0;
        if ("NEAR_EXPIRY".equals(status)) return 20.0;
        return 0.0;
    }

    public void setRecommendedDiscount(Double recommendedDiscount) {
        this.recommendedDiscount = recommendedDiscount;
    }

    public Integer getAvailableStock() {
        try {
            if (inventoryBatches == null || !org.hibernate.Hibernate.isInitialized(inventoryBatches)) {
                return availableStock != null ? availableStock : 0;
            }
            return inventoryBatches.stream()
                    .mapToInt(InventoryBatch::getRemainingQuantity)
                    .sum();
        } catch (Exception e) {
            return availableStock != null ? availableStock : 0;
        }
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public Integer getTotalInitialStock() {
        try {
            if (inventoryBatches == null || !org.hibernate.Hibernate.isInitialized(inventoryBatches)) {
                return 0;
            }
            return inventoryBatches.stream()
                    .mapToInt(InventoryBatch::getQuantity)
                    .sum();
        } catch (Exception e) {
            return 0;
        }
    }

    public Double getStockPercentage() {
        Integer initial = getTotalInitialStock();
        if (initial == null || initial == 0) return 0.0;
        return (getAvailableStock() * 100.0) / initial;
    }

    public Integer getExpiredStock() {
        try {
            if (inventoryBatches == null || !org.hibernate.Hibernate.isInitialized(inventoryBatches)) return 0;
            java.time.LocalDate today = java.time.LocalDate.now();
            return inventoryBatches.stream()
                    .filter(b -> b.getRemainingQuantity() > 0 && b.getExpiryDate() != null && !b.getExpiryDate().isAfter(today))
                    .mapToInt(InventoryBatch::getRemainingQuantity)
                    .sum();
        } catch (Exception e) { return 0; }
    }

    public Integer getNearExpiryStock() {
        try {
            if (inventoryBatches == null || !org.hibernate.Hibernate.isInitialized(inventoryBatches)) return 0;
            java.time.LocalDate today = java.time.LocalDate.now();
            return inventoryBatches.stream()
                    .filter(b -> b.getRemainingQuantity() > 0 && 
                                b.getExpiryDate() != null && 
                                b.getExpiryDate().isAfter(today) &&
                                b.getDiscountPercent() != null && b.getDiscountPercent() > 0)
                    .mapToInt(InventoryBatch::getRemainingQuantity)
                    .sum();
        } catch (Exception e) { return 0; }
    }

    public Integer getFreshStock() {
        Integer total = getAvailableStock();
        if (total == null) return 0;
        return total - getExpiredStock() - getNearExpiryStock();
    }

    public Double getNextBatchDiscount() {
        try {
            if (inventoryBatches == null || !org.hibernate.Hibernate.isInitialized(inventoryBatches)) return 0.0;
            return inventoryBatches.stream()
                    .filter(b -> b.getRemainingQuantity() > 0)
                    .sorted(Comparator.comparing(b -> b.getPurchase() != null ? b.getPurchase().getPurchaseDate() : LocalDateTime.MIN))
                    .map(InventoryBatch::getDiscountPercent)
                    .findFirst()
                    .orElse(0.0);
        } catch (Exception e) { return 0.0; }
    }

    public String getStockStatus() {
        Integer totalAvailable = getAvailableStock();
        if (totalAvailable == null || totalAvailable <= 0) return "OUT_OF_STOCK";

        Integer expired = getExpiredStock();
        Integer freshStock = totalAvailable - expired;

        if (expired > 0) {
            return freshStock > 0 ? "PARTIALLY_EXPIRED" : "EXPIRED";
        }
        
        Double percentage = getStockPercentage();
        return percentage < 30.0 ? "LOW_STOCK" : "IN_STOCK";
    }

    @PrePersist
    public void generateSku() {
        if (this.sku == null || this.sku.isEmpty()) {
            this.sku = "PRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    private Double mrp;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public ProductType getType() { return type; }
    public void setType(ProductType type) { this.type = type; }
    public Double getMrp() { return mrp; }
    public void setMrp(Double mrp) { this.mrp = mrp; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Shop getShop() { return shop; }
    public void setShop(Shop shop) { this.shop = shop; }
    public List<InventoryBatch> getInventoryBatches() { return inventoryBatches; }
    public void setInventoryBatches(List<InventoryBatch> inventoryBatches) { this.inventoryBatches = inventoryBatches; }
    public List<SalesItem> getSalesItems() { return salesItems; }
    public void setSalesItems(List<SalesItem> salesItems) { this.salesItems = salesItems; }

    public Double getGrossProfit() {
        if (salesItems == null) return 0.0;
        return salesItems.stream()
                .mapToDouble(si -> si.getProfit() != null ? si.getProfit() : 0.0)
                .sum();
    }

    public Double getPotentialLoss() {
        if (inventoryBatches == null) return 0.0;
        java.time.LocalDate today = java.time.LocalDate.now();
        return inventoryBatches.stream()
                .filter(b -> b.getRemainingQuantity() > 0 && b.getExpiryDate() != null && b.getExpiryDate().isBefore(today))
                .mapToDouble(b -> (b.getCostPrice() != null ? b.getCostPrice() : 0.0) * b.getRemainingQuantity())
                .sum();
    }

    public Double getNetProfit() {
        return getGrossProfit() - getPotentialLoss();
    }
}
