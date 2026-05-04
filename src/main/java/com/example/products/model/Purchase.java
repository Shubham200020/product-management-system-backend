package com.example.products.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "purchases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String supplier;
    private Double totalCost;
    private LocalDateTime purchaseDate;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    @Builder.Default
    private List<InventoryBatch> inventoryBatches = new java.util.ArrayList<>();

    public Shop getShop() { return shop; }
    public void setShop(Shop shop) { this.shop = shop; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
    public List<InventoryBatch> getInventoryBatches() { return inventoryBatches; }
    public void setInventoryBatches(List<InventoryBatch> inventoryBatches) { this.inventoryBatches = inventoryBatches; }
}
