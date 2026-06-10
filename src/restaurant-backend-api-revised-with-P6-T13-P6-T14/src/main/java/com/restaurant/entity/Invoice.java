package com.restaurant.entity;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime;
@Entity @Table(name="invoices") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Invoice {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="invoice_id") private Long invoiceId;
 @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="order_id", nullable=false, unique=true) private Order order;
 @Column(name="invoice_number", nullable=false, unique=true, length=50) private String invoiceNumber;
 @Column(name="total_amount", nullable=false, precision=18, scale=2) private BigDecimal totalAmount;
 @Column(name="issued_at", nullable=false) private LocalDateTime issuedAt;
 @Column(name="pdf_url", length=500) private String pdfUrl;
 @PrePersist void prePersist(){ if(issuedAt==null) issuedAt=LocalDateTime.now(); }
}
