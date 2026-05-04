package com.example.products.controller;

import com.example.products.dto.InventoryBatchReportDTO;
import com.example.products.model.InventoryBatch;
import com.example.products.model.Product;
import com.example.products.model.Shop;
import com.example.products.repository.ProductRepository;
import com.example.products.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private com.example.products.repository.InventoryBatchRepository inventoryBatchRepository;

    @GetMapping
    public List<Product> getAllProducts(Principal principal) {
        return productRepository.findByShopOwnerEmail(principal.getName());
    }

    @GetMapping("/report")
    public List<InventoryBatchReportDTO> getInventoryReport(Principal principal) {
        List<InventoryBatch> batches = inventoryBatchRepository.findAllByOwnerEmail(principal.getName());
        List<InventoryBatchReportDTO> report = new ArrayList<>();
        
        for (InventoryBatch b : batches) {
            Product p = b.getProduct();
            InventoryBatchReportDTO dto = new InventoryBatchReportDTO();
            dto.setProductName(p.getName());
            dto.setProductSku(p.getSku());
            dto.setShopName(p.getShop().getName());
            dto.setCategoryName(p.getCategory() != null ? p.getCategory().getName() : "N/A");
            dto.setPurchaseDate(b.getPurchase() != null ? b.getPurchase().getPurchaseDate() : null);
            dto.setExpiryDate(b.getExpiryDate());
            dto.setCostPrice(b.getCostPrice());
            dto.setInitialQuantity(b.getQuantity());
            dto.setRemainingQuantity(b.getRemainingQuantity());
            
            // Profit is product-wide in our current simplified model
            double productProfit = 0.0;
            if (p.getSalesItems() != null) {
                productProfit = p.getSalesItems().stream()
                    .mapToDouble(si -> si.getProfit() != null ? si.getProfit() : 0.0)
                    .sum();
            }
            dto.setTotalProfit(productProfit);
            report.add(dto);
        }
        return report;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOPKEEPER')")
    public ResponseEntity<Product> createProduct(@RequestBody Product product, Principal principal) {
        if (product.getShop() != null) {
            Shop shop = shopRepository.findById(product.getShop().getId())
                    .orElseThrow(() -> new RuntimeException("Shop not found"));
            if (!shop.getOwner().getEmail().equals(principal.getName())) {
                throw new RuntimeException("Access denied: Shop does not belong to you");
            }
        }
        return ResponseEntity.ok(productRepository.save(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOPKEEPER')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails, Principal principal) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Ensure the product belongs to the user
        if (!product.getShop().getOwner().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Access denied: You do not own this product");
        }
        
        product.setName(productDetails.getName());
        product.setSku(productDetails.getSku());
        product.setBrand(productDetails.getBrand());
        product.setType(productDetails.getType());
        product.setCategory(productDetails.getCategory());
        product.setShop(productDetails.getShop());
        product.setActive(productDetails.isActive());
        
        return ResponseEntity.ok(productRepository.save(product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SHOPKEEPER')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, Principal principal) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (!product.getShop().getOwner().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Access denied: You do not own this product");
        }
        
        productRepository.delete(product);
        return ResponseEntity.ok().build();
    }
}
