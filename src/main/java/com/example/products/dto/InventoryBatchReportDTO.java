package com.example.products.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class InventoryBatchReportDTO {
    private String productName;
    private String productSku;
    private String shopName;
    private String categoryName;
    private LocalDateTime purchaseDate;
    private LocalDate expiryDate;
    private Double costPrice;
    private Integer initialQuantity;
    private Integer remainingQuantity;
    private Double grossProfit;
    private Double netProfit;
    private String stockStatus;
    private Double investment;
    private Double potentialLoss;
    private String supplierName;
    private Double mrp;
    private Double recommendedDiscount;
    private Double lossAfterDiscount;

    // Getters and Setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public Double getCostPrice() { return costPrice; }
    public void setCostPrice(Double costPrice) { this.costPrice = costPrice; }
    public Integer getInitialQuantity() { return initialQuantity; }
    public void setInitialQuantity(Integer initialQuantity) { this.initialQuantity = initialQuantity; }
    public Integer getRemainingQuantity() { return remainingQuantity; }
    public void setRemainingQuantity(Integer remainingQuantity) { this.remainingQuantity = remainingQuantity; }
    public Double getGrossProfit() { return grossProfit; }
    public void setGrossProfit(Double grossProfit) { this.grossProfit = grossProfit; }
    public Double getNetProfit() { return netProfit; }
    public void setNetProfit(Double netProfit) { this.netProfit = netProfit; }
    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }
    public Double getInvestment() { return investment; }
    public void setInvestment(Double investment) { this.investment = investment; }
    public Double getPotentialLoss() { return potentialLoss; }
    public void setPotentialLoss(Double potentialLoss) { this.potentialLoss = potentialLoss; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public Double getMrp() { return mrp; }
    public void setMrp(Double mrp) { this.mrp = mrp; }
    public Double getRecommendedDiscount() { return recommendedDiscount; }
    public void setRecommendedDiscount(Double recommendedDiscount) { this.recommendedDiscount = recommendedDiscount; }
    public Double getLossAfterDiscount() { return lossAfterDiscount; }
    public void setLossAfterDiscount(Double lossAfterDiscount) { this.lossAfterDiscount = lossAfterDiscount; }
}
