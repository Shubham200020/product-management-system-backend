package com.example.products.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockHealthSummaryDTO {
    private Double totalPotentialLoss;
    private Double recoverableAmount;
    private Integer highRiskItems;
    private Integer expiredItems;
    private Double overallHealthScore; // 0-100
}
