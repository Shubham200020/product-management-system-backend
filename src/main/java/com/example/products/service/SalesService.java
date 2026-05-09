package com.example.products.service;

import com.example.products.model.*;
import com.example.products.repository.*;
import com.example.products.dto.SaleItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SalesService {

    @Autowired
    private SalesInvoiceRepository invoiceRepository;

    @Autowired
    private InventoryBatchRepository batchRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Transactional
    public SalesInvoice createSale(Long shopId, PaymentMode paymentMode, List<SaleItemRequest> items, String currentUserEmail) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        if (!shop.getOwner().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Access denied: Shop does not belong to you");
        }

        SalesInvoice invoice = new SalesInvoice();
        invoice.setShop(shop);
        invoice.setPaymentMode(paymentMode);
        invoice.setTotalAmount(0.0);
        invoice.setTotalProfit(0.0);
        invoice.setSalesItems(new ArrayList<>());

        double totalInvoiceAmount = 0.0;
        double totalInvoiceProfit = 0.0;

        for (SaleItemRequest itemReq : items) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.getProductId()));

            int quantityToSell = itemReq.getQuantity();
            double sellingPrice = itemReq.getSellingPrice();

            // Find batches for FIFO
            List<InventoryBatch> batches = batchRepository.findByProductAndRemainingQuantityGreaterThanOrderByPurchasePurchaseDateAsc(product, 0);

            int availableStock = batches.stream().mapToInt(InventoryBatch::getRemainingQuantity).sum();
            if (availableStock < quantityToSell) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            for (InventoryBatch batch : batches) {
                if (quantityToSell <= 0) break;

                int sellFromThisBatch = Math.min(batch.getRemainingQuantity(), quantityToSell);
                
                // Smart Batch Pricing: If batch has a clearance discount, apply it automatically
                double batchSellingPrice = sellingPrice;
                double batchDiscount = 0.0;
                
                if (batch.getDiscountPercent() != null && batch.getDiscountPercent() > 0) {
                    batchDiscount = batch.getDiscountPercent();
                    // If the user hasn't already manually applied a deeper discount, use the batch's clearance price
                    double mrpPrice = product.getMrp() != null ? product.getMrp() : sellingPrice;
                    double clearancePrice = mrpPrice * (1 - (batchDiscount / 100.0));
                    batchSellingPrice = Math.min(sellingPrice, clearancePrice);
                }

                double costPrice = batch.getCostPrice();
                double profit = (batchSellingPrice - costPrice) * sellFromThisBatch;

                SalesItem salesItem = new SalesItem();
                salesItem.setInvoice(invoice);
                salesItem.setProduct(product);
                salesItem.setInventoryBatch(batch);
                salesItem.setQuantity(sellFromThisBatch);
                salesItem.setSellingPrice(batchSellingPrice);
                salesItem.setCostPrice(costPrice);
                salesItem.setProfit(profit);
                salesItem.setDiscountPercent(batchDiscount);

                invoice.getSalesItems().add(salesItem);
                
                // Update batch
                batch.setRemainingQuantity(batch.getRemainingQuantity() - sellFromThisBatch);
                batchRepository.save(batch);

                totalInvoiceAmount += (batchSellingPrice * sellFromThisBatch);
                totalInvoiceProfit += profit;
                quantityToSell -= sellFromThisBatch;
            }
        }

        invoice.setTotalAmount(totalInvoiceAmount);
        invoice.setTotalProfit(totalInvoiceProfit);

        return invoiceRepository.save(invoice);
    }
}
