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
                
                double costPrice = batch.getCostPrice();
                double profit = (sellingPrice - costPrice) * sellFromThisBatch;

                SalesItem salesItem = new SalesItem();
                salesItem.setInvoice(invoice);
                salesItem.setProduct(product);
                salesItem.setQuantity(sellFromThisBatch);
                salesItem.setSellingPrice(sellingPrice);
                salesItem.setCostPrice(costPrice);
                salesItem.setProfit(profit);

                invoice.getSalesItems().add(salesItem);
                
                // Update batch
                batch.setRemainingQuantity(batch.getRemainingQuantity() - sellFromThisBatch);
                batchRepository.save(batch);

                totalInvoiceAmount += (sellingPrice * sellFromThisBatch);
                totalInvoiceProfit += profit;
                quantityToSell -= sellFromThisBatch;
            }
        }

        invoice.setTotalAmount(totalInvoiceAmount);
        invoice.setTotalProfit(totalInvoiceProfit);

        return invoiceRepository.save(invoice);
    }
}
