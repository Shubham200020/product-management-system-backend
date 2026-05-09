package com.example.products.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sales_items")
public class SalesItem {
    
    public SalesItem() {}
    
    public SalesItem(Long id, Integer quantity, Double sellingPrice, Double costPrice, Double profit, SalesInvoice invoice, Product product) {
        this.id = id;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
        this.costPrice = costPrice;
        this.profit = profit;
        this.invoice = invoice;
        this.product = product;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;
    private Double sellingPrice;
    private Double costPrice;
    private Double profit;
    private Double discountPercent = 0.0;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private SalesInvoice invoice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private InventoryBatch inventoryBatch;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(Double sellingPrice) { this.sellingPrice = sellingPrice; }
    public Double getCostPrice() { return costPrice; }
    public void setCostPrice(Double costPrice) { this.costPrice = costPrice; }
    public Double getProfit() { return profit; }
    public void setProfit(Double profit) { this.profit = profit; }
    public SalesInvoice getInvoice() { return invoice; }
    public void setInvoice(SalesInvoice invoice) { this.invoice = invoice; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public InventoryBatch getInventoryBatch() { return inventoryBatch; }
    public void setInventoryBatch(InventoryBatch inventoryBatch) { this.inventoryBatch = inventoryBatch; }
    public Double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Double discountPercent) { this.discountPercent = discountPercent; }
}
