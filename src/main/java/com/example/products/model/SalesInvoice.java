package com.example.products.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sales_invoices")
public class SalesInvoice {
    
    public SalesInvoice() {}
    
    public SalesInvoice(Long id, Double totalAmount, Double totalProfit, PaymentMode paymentMode, LocalDateTime createdAt, Shop shop, List<SalesItem> salesItems) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.totalProfit = totalProfit;
        this.paymentMode = paymentMode;
        this.createdAt = createdAt;
        this.shop = shop;
        this.salesItems = salesItems;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double totalAmount;
    private Double totalProfit;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Shop shop;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<SalesItem> salesItems = new java.util.ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public Double getTotalProfit() { return totalProfit; }
    public void setTotalProfit(Double totalProfit) { this.totalProfit = totalProfit; }
    public PaymentMode getPaymentMode() { return paymentMode; }
    public void setPaymentMode(PaymentMode paymentMode) { this.paymentMode = paymentMode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Shop getShop() { return shop; }
    public void setShop(Shop shop) { this.shop = shop; }
    public List<SalesItem> getSalesItems() { return salesItems; }
    public void setSalesItems(List<SalesItem> salesItems) { this.salesItems = salesItems; }
}
