package com.example.products.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "inventory_batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;
    private Integer remainingQuantity;
    private Double costPrice;
    private LocalDate expiryDate;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Purchase purchase;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getRemainingQuantity() { return remainingQuantity; }
    public void setRemainingQuantity(Integer remainingQuantity) { this.remainingQuantity = remainingQuantity; }
    public Double getCostPrice() { return costPrice; }
    public void setCostPrice(Double costPrice) { this.costPrice = costPrice; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Purchase getPurchase() { return purchase; }
    public void setPurchase(Purchase purchase) { this.purchase = purchase; }

    @OneToMany(mappedBy = "inventoryBatch", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<SalesItem> salesItems = new java.util.ArrayList<>();

    public java.util.List<SalesItem> getSalesItems() { return salesItems; }
    public void setSalesItems(java.util.List<SalesItem> salesItems) { this.salesItems = salesItems; }

    public Double getGrossProfit() {
        if (salesItems == null) return 0.0;
        return salesItems.stream()
                .mapToDouble(si -> si.getProfit() != null ? si.getProfit() : 0.0)
                .sum();
    }

    private Double discountPercent = 0.0;
    public Double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Double discountPercent) { this.discountPercent = discountPercent; }
}
