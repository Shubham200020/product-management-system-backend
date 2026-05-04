package com.example.products.repository;

import com.example.products.model.SalesInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SalesInvoiceRepository extends JpaRepository<SalesInvoice, Long> {
    List<SalesInvoice> findByShopOwnerEmail(String email);
}
