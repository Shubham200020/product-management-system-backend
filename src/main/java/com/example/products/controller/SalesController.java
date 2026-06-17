package com.example.products.controller;

import com.example.products.dto.SaleRequest;
import com.example.products.model.SalesInvoice;
import com.example.products.repository.SalesInvoiceRepository;
import com.example.products.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    @Autowired
    private SalesService salesService;

    @Autowired
    private SalesInvoiceRepository salesInvoiceRepository;

    @GetMapping
    public List<SalesInvoice> getAllSales(Principal principal) {
        return salesInvoiceRepository.findByShopOwnerEmail(principal.getName());
    }

    @PostMapping("/create")
    public ResponseEntity<SalesInvoice> createSale(@RequestBody SaleRequest request, Principal principal) {
        SalesInvoice invoice = salesService.createSale(
                request.getShopId(),
                request.getPaymentMode(),
                request.getItems(),
                principal.getName()
        );
        return ResponseEntity.ok(invoice);
    }
}
