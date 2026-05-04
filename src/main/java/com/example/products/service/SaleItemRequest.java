package com.example.products.service;

import lombok.Data;

@Data
public class SaleItemRequest {
    private Long productId;
    private Integer quantity;
    private Double sellingPrice;
}
