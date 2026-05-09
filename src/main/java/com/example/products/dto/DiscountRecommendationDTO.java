package com.example.products.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRecommendationDTO {
    private Long productId;
    private Long batchId;
    private String productName;
    private String sku;
    private Integer currentStock;
    private Double riskScore;
    private Double recommendedDiscount;
    
    // Risk components (for transparency/UI tooltips)
    private Double expiryUrgency;
    private Double stockPressure;
    private Double demandFactor;
    private Double wasteFactor;
    
    private String urgency; // HIGH, MEDIUM, LOW
    private boolean isExpired;

    public DiscountRecommendationDTO(Long productId, Long batchId, String productName, String sku, Integer currentStock, 
                                     Double riskScore, Double recommendedDiscount, Double expiryUrgency, 
                                     Double stockPressure, Double demandFactor, Double wasteFactor, String urgency, boolean isExpired) {
        this.productId = productId;
        this.batchId = batchId;
        this.productName = productName;
        this.sku = sku;
        this.currentStock = currentStock;
        this.riskScore = riskScore;
        this.recommendedDiscount = recommendedDiscount;
        this.expiryUrgency = expiryUrgency;
        this.stockPressure = stockPressure;
        this.demandFactor = demandFactor;
        this.wasteFactor = wasteFactor;
        this.urgency = urgency;
        this.isExpired = isExpired;
    }

    // Explicit getters for reliability
    public Long getProductId() { return productId; }
    public Long getBatchId() { return batchId; }
    public String getProductName() { return productName; }
    public String getSku() { return sku; }
    public Integer getCurrentStock() { return currentStock; }
    public Double getRiskScore() { return riskScore; }
    public String getUrgency() { return urgency; }
    public Double getRecommendedDiscount() { return recommendedDiscount; }
    public Double getExpiryUrgency() { return expiryUrgency; }
    public Double getStockPressure() { return stockPressure; }
    public Double getDemandFactor() { return demandFactor; }
    public Double getWasteFactor() { return wasteFactor; }
    public boolean isExpired() { return isExpired; }
}
