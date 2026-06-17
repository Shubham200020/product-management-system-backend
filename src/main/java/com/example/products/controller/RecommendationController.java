package com.example.products.controller;

import com.example.products.dto.DiscountRecommendationDTO;
import com.example.products.dto.RestockRecommendationDTO;
import com.example.products.dto.StockHealthSummaryDTO;
import com.example.products.model.InventoryBatch;
import com.example.products.model.Product;
import com.example.products.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/summary")
    public StockHealthSummaryDTO getStockHealthSummary(Principal principal) {
        List<Product> products = productRepository.findByShopOwnerEmail(principal.getName());
        LocalDate today = LocalDate.now();
        
        double totalPotentialLoss = 0.0;
        double recoverableAmount = 0.0;
        int highRiskItems = 0;
        int expiredItems = 0;
        double totalRiskWeight = 0.0;
        int batchCount = 0;

        for (Product p : products) {
            for (InventoryBatch b : p.getInventoryBatches()) {
                int stock = b.getRemainingQuantity() != null ? b.getRemainingQuantity() : 0;
                if (stock <= 0) continue;
                batchCount++;

                double expiryUrgency = calculateBatchExpiryUrgency(b, today);
                double stockPressure = calculateBatchStockPressure(b, p);
                double riskScore = (expiryUrgency * 0.40) + (stockPressure * 0.25);
                
                totalRiskWeight += riskScore;

                if (expiryUrgency >= 100.0) {
                    expiredItems += stock;
                    totalPotentialLoss += (b.getCostPrice() != null ? b.getCostPrice() : 0.0) * stock;
                } else if (riskScore > 35) {
                    highRiskItems += stock;
                    double potentialLossValue = (b.getCostPrice() != null ? b.getCostPrice() : 0.0) * stock;
                    totalPotentialLoss += potentialLossValue;
                    
                    // Recoverable = Price with recommended discount
                    double mrp = p.getMrp() != null ? p.getMrp() : 0.0;
                    double baseDiscount = 10.0;
                    double recDiscount = Math.max(5.0, Math.min(80.0, baseDiscount + (expiryUrgency/2.0)));
                    recoverableAmount += (mrp * (1 - (recDiscount/100.0))) * stock;
                }
            }
        }

        double healthScore = batchCount == 0 ? 100.0 : Math.max(0, 100.0 - (totalRiskWeight / batchCount));

        return new StockHealthSummaryDTO(
            Math.round(totalPotentialLoss * 100.0) / 100.0,
            Math.round(recoverableAmount * 100.0) / 100.0,
            highRiskItems,
            expiredItems,
            Math.round(healthScore * 10.0) / 10.0
        );
    }

    @GetMapping("/restock")
    public List<RestockRecommendationDTO> getRestockRecommendations(Principal principal) {
        List<Product> products = productRepository.findByShopOwnerEmail(principal.getName());
        List<RestockRecommendationDTO> recommendations = new ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();

        for (Product p : products) {
            if (!p.isActive()) continue;

            // ANALYTICAL LOGIC (Simulated AI/ML Predictive Analysis)
            // 1. Calculate Sales Velocity (units/day) - Mocked here for demonstration
            // In a real ML system, this would be derived from SalesHistory using Regression
            double salesVelocity = calculateSalesVelocity(p); 
            
            Integer stock = p.getAvailableStock() != null ? p.getAvailableStock() : 0;
            String urgency = null;
            String reason = null;

            // Predictive Urgency Calculation
            if (stock <= 0) {
                urgency = "HIGH";
                reason = "CRITICAL: Out of stock. Immediate restock required to prevent sales loss.";
            } else if (salesVelocity > 0) {
                double daysLeft = stock / salesVelocity;
                if (daysLeft <= 3) {
                    urgency = "HIGH";
                    reason = String.format("URGENT: Stock will deplete in ~%.1f days based on current sales velocity.", daysLeft);
                } else if (daysLeft <= 7) {
                    urgency = "MEDIUM";
                    reason = String.format("WARNING: Low stock. Predicted stock-out in ~%.1f days.", daysLeft);
                } else if ("LOW_STOCK".equals(p.getStockStatus())) {
                    urgency = "LOW";
                    reason = "System alert: Product flagged as low stock by inventory rules.";
                }
            } else if ("LOW_STOCK".equals(p.getStockStatus())) {
                urgency = "LOW";
                reason = "Inventory levels are below the safety threshold.";
            }

            if (urgency != null) {
                recommendations.add(new RestockRecommendationDTO(
                    p.getId(),
                    p.getName(),
                    p.getSku(),
                    stock,
                    urgency,
                    reason
                ));
            }
        }

        // Sort: HIGH > MEDIUM > LOW
        recommendations.sort((a, b) -> Integer.compare(getUrgencyRank(a.getUrgency()), getUrgencyRank(b.getUrgency())));

        return recommendations;
    }

    @GetMapping("/discounts")
    public List<DiscountRecommendationDTO> getDiscountRecommendations(Principal principal) {
        List<Product> products = productRepository.findByShopOwnerEmail(principal.getName());
        List<DiscountRecommendationDTO> recommendations = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Product p : products) {
            if (!p.isActive()) continue;

            for (InventoryBatch b : p.getInventoryBatches()) {
                Integer stock = b.getRemainingQuantity() != null ? b.getRemainingQuantity() : 0;
                if (stock <= 0) continue;

                // 1. Calculate Individual Risk Factors for THIS BATCH
                double expiryUrgency = calculateBatchExpiryUrgency(b, today);
                double stockPressure = calculateBatchStockPressure(b, p);
                double lowDemand = calculateLowDemandFactor(p); // Demand is still product-level
                double wasteHistory = calculateWasteHistoryFactor(p, today);

                // 2. Risk Score
                double riskScore = (expiryUrgency * 0.40) + (stockPressure * 0.25) + (lowDemand * 0.20) + (wasteHistory * 0.15);

                // 3. Recommended Discount
                double baseDiscount = 10.0;
                double expiryFactor = (expiryUrgency / 100.0) * 50.0;
                double overstockFactor = (stockPressure / 100.0) * 20.0;
                double demandStrength = (1.0 - (lowDemand / 100.0)) * 15.0;

                boolean isExpired = b.getExpiryDate() != null && !b.getExpiryDate().isAfter(today);
                double recommendedDiscount = isExpired ? 0.0 : Math.max(5.0, Math.min(80.0, baseDiscount + expiryFactor + overstockFactor - demandStrength));

                if (riskScore > 20 || isExpired) {
                    String urgency = isExpired ? "HIGH" : (riskScore > 60 ? "HIGH" : (riskScore > 35 ? "MEDIUM" : "LOW"));
                    
                    recommendations.add(new DiscountRecommendationDTO(
                        p.getId(),
                        b.getId(),
                        p.getName() + " (Batch: #" + b.getId() + ")",
                        p.getSku(),
                        stock,
                        Math.round(riskScore * 10.0) / 10.0,
                        Math.round(recommendedDiscount * 10.0) / 10.0,
                        Math.round(expiryUrgency * 10.0) / 10.0,
                        Math.round(stockPressure * 10.0) / 10.0,
                        Math.round(lowDemand * 10.0) / 10.0,
                        Math.round(wasteHistory * 10.0) / 10.0,
                        urgency,
                        isExpired
                    ));
                }
            }
        }

        recommendations.sort(Comparator.comparing(DiscountRecommendationDTO::getRiskScore).reversed());
        return recommendations;
    }

    private double calculateBatchExpiryUrgency(InventoryBatch b, LocalDate today) {
        if (b.getExpiryDate() == null) return 0.0;

        long daysLeft = ChronoUnit.DAYS.between(today, b.getExpiryDate());
        if (daysLeft <= 0) return 100.0;
        if (daysLeft > 30) return 0.0;
        
        return 100.0 - (daysLeft * (100.0 / 30.0));
    }

    private double calculateBatchStockPressure(InventoryBatch b, Product p) {
        double velocity = calculateSalesVelocity(p);
        Integer stock = b.getRemainingQuantity() != null ? b.getRemainingQuantity() : 0;
        
        if (velocity <= 0) return stock > 0 ? 80.0 : 0.0;
        
        double daysOfStock = stock / velocity;
        if (daysOfStock <= 7) return 10.0;
        if (daysOfStock >= 45) return 100.0;
        
        return ((daysOfStock - 7) / (45 - 7)) * 100.0;
    }

    private double calculateLowDemandFactor(Product p) {
        double velocity = calculateSalesVelocity(p);
        // Target velocity is 5 units/day for this simulation
        double targetVelocity = 5.0;
        if (velocity >= targetVelocity) return 0.0;
        return (1.0 - (velocity / targetVelocity)) * 100.0;
    }

    private double calculateWasteHistoryFactor(Product p, LocalDate today) {
        // Calculate based on expired batches that were never fully sold
        long expiredBatches = p.getInventoryBatches().stream()
                .filter(b -> b.getExpiryDate() != null && b.getExpiryDate().isBefore(today))
                .count();
        
        if (expiredBatches == 0) return 0.0;
        if (expiredBatches >= 3) return 100.0;
        return (expiredBatches / 3.0) * 100.0;
    }

    /**
     * Simulated ML Method: Analyzes historical sales to predict future demand velocity.
     */
    private double calculateSalesVelocity(Product p) {
        // In a real implementation, we'd query SalesItems for the last 30 days
        // Here we simulate a velocity based on ID parity and stock status for variety
        if ("OUT_OF_STOCK".equals(p.getStockStatus())) return 0.0;
        
        // Randomish but deterministic velocity for UI demonstration
        double baseVelocity = (p.getId() % 5) + 1.2; 
        return baseVelocity;
    }

    private int getUrgencyRank(String urgency) {
        switch (urgency) {
            case "HIGH": return 1;
            case "MEDIUM": return 2;
            case "LOW": return 3;
            default: return 4;
        }
    }
}
