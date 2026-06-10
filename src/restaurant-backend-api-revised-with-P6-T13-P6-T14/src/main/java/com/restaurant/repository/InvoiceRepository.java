package com.restaurant.repository;
import com.restaurant.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByOrder_OrderId(Long orderId);
    boolean existsByOrder_OrderId(Long orderId);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
