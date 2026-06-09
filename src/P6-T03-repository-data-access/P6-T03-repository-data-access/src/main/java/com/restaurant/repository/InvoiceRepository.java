package com.restaurant.repository;

import com.restaurant.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceCode(String invoiceCode);

    Optional<Invoice> findByOrder_Id(Long orderId);

    List<Invoice> findByInvoiceStatus(String invoiceStatus);

    List<Invoice> findByIssuedAtBetween(LocalDateTime start, LocalDateTime end);
}
