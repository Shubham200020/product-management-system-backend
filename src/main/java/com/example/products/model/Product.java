package com.example.products.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

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
}
