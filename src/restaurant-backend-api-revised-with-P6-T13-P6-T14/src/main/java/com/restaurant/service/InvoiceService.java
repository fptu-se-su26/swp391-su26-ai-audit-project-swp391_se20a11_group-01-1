package com.restaurant.service;
import com.restaurant.dto.invoice.InvoiceResponse; import com.restaurant.dto.order.OrderItemResponse; import com.restaurant.entity.*; import com.restaurant.exception.*; import com.restaurant.model.PaymentStatus; import com.restaurant.repository.*; import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.math.BigDecimal; import java.time.format.DateTimeFormatter; import java.util.List; import java.io.ByteArrayOutputStream; import com.lowagie.text.*; import com.lowagie.text.pdf.PdfWriter;
@Service @RequiredArgsConstructor
public class InvoiceService { private final InvoiceRepository invoiceRepository; private final OrderService orderService; private final PaymentRepository paymentRepository;
 @Transactional public InvoiceResponse generate(Long orderId){ Order order=orderService.getOrder(orderId); Payment payment=paymentRepository.findTopByOrder_OrderIdOrderByCreatedAtDesc(orderId).orElseThrow(()->new BadRequestException("Payment is required before invoice")); if(payment.getPaymentStatus()!=PaymentStatus.PAID) throw new BadRequestException("Invoice can be generated only after PaymentStatus = PAID"); Invoice invoice=invoiceRepository.findByOrder_OrderId(orderId).orElseGet(()->invoiceRepository.save(Invoice.builder().order(order).invoiceNumber(buildNumber(order)).totalAmount(order.getTotalAmount()).pdfUrl("/invoices/orders/"+orderId+"/pdf").build())); return toResponse(invoice); }
 public InvoiceResponse get(Long id){ return toResponse(invoiceRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Invoice not found"))); }
 public InvoiceResponse byOrder(Long orderId){ return toResponse(invoiceRepository.findByOrder_OrderId(orderId).orElseThrow(()->new ResourceNotFoundException("Invoice not found"))); }
 public byte[] pdfBytes(Long orderId){
  try{
   InvoiceResponse inv=byOrder(orderId); ByteArrayOutputStream out=new ByteArrayOutputStream(); Document doc=new Document(); PdfWriter.getInstance(doc,out); doc.open();
   doc.add(new Paragraph("RESTAURANT INVOICE")); doc.add(new Paragraph("Invoice: "+inv.getInvoiceNumber())); doc.add(new Paragraph("Order: "+inv.getOrderId())); doc.add(new Paragraph("Total: "+inv.getTotalAmount())); doc.add(new Paragraph(" "));
   for(OrderItemResponse i: inv.getItems()){ doc.add(new Paragraph("- "+i.getFoodName()+" x"+i.getQuantity()+" = "+i.getSubtotal())); }
   doc.close(); return out.toByteArray();
  } catch(Exception e){ throw new BadRequestException("Cannot generate invoice PDF: "+e.getMessage()); }
 }
 private String buildNumber(Order o){ return "INV-"+o.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"))+"-"+o.getOrderId(); }
 private InvoiceResponse toResponse(Invoice invoice){ Order o=invoice.getOrder(); List<OrderItemResponse> items=o.getItems().stream().map(i->OrderItemResponse.builder().orderItemId(i.getOrderItemId()).foodId(i.getFood().getFoodId()).foodName(i.getFood().getFoodName()).quantity(i.getQuantity()).unitPrice(i.getUnitPrice()).subtotal(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()))).itemStatus(String.valueOf(i.getItemStatus())).kitchenNote(i.getKitchenNote()).build()).toList(); return InvoiceResponse.builder().invoiceId(invoice.getInvoiceId()).orderId(o.getOrderId()).invoiceNumber(invoice.getInvoiceNumber()).totalAmount(invoice.getTotalAmount()).issuedAt(invoice.getIssuedAt()).pdfUrl(invoice.getPdfUrl()).items(items).build(); }
}
