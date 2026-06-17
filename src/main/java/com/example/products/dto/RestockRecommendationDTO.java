package com.example.products.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RestockRecommendationDTO {
    private Long productId;
    private String productName;
    private String sku;
    private Integer currentStock;
    private String urgency; // HIGH, MEDIUM, LOW
    private String reason;

    // Explicit constructor to resolve compilation errors
    public RestockRecommendationDTO(Long productId, String productName, String sku, Integer currentStock, String urgency, String reason) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.currentStock = currentStock;
        this.urgency = urgency;
        this.reason = reason;
    }

    // Explicit getter for urgency to resolve compilation errors
    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    // Explicit getters/setters for other fields for robustness
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

