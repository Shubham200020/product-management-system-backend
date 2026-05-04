package com.example.products.service;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PurchaseItemRequest {
    private Long productId;
    private Integer quantity;
    private Double costPrice;
    private LocalDate expiryDate;
}
