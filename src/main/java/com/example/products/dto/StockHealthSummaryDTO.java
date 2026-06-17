package com.example.products.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StockHealthSummaryDTO {
    private Double totalPotentialLoss;
    private Double recoverableAmount;
    private Integer highRiskItems;
    private Integer expiredItems;
    private Double overallHealthScore; // 0-100

    public StockHealthSummaryDTO(Double totalPotentialLoss, Double recoverableAmount, Integer highRiskItems, Integer expiredItems, Double overallHealthScore) {
        this.totalPotentialLoss = totalPotentialLoss;
        this.recoverableAmount = recoverableAmount;
        this.highRiskItems = highRiskItems;
        this.expiredItems = expiredItems;
        this.overallHealthScore = overallHealthScore;
    }
}

