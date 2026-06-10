package com.restaurant.service;
import com.restaurant.dto.payment.*; import com.restaurant.entity.*; import com.restaurant.exception.*; import com.restaurant.model.*; import com.restaurant.repository.PaymentRepository; import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.time.LocalDateTime; import java.util.List;
@Service @RequiredArgsConstructor
public class PaymentService { private final PaymentRepository paymentRepository; private final OrderService orderService;
 @Transactional public PaymentResponse create(PaymentCreateRequest r){ Order order=orderService.getOrder(r.getOrderId()); Payment p=Payment.builder().order(order).paymentMethod(PaymentMethod.valueOf(r.getPaymentMethod().toUpperCase())).paymentStatus(PaymentStatus.PENDING).amount(order.getTotalAmount()).build(); order.setOrderStatus(OrderStatus.PENDING_PAYMENT); return toResponse(paymentRepository.save(p)); }
 public PaymentResponse get(Long id){ return toResponse(paymentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Payment not found"))); }
 public List<PaymentResponse> byOrder(Long orderId){ return paymentRepository.findByOrder_OrderId(orderId).stream().map(this::toResponse).toList(); }
 @Transactional public PaymentResponse confirm(Long id, PaymentConfirmRequest r){ Payment p=getPayment(id); if(p.getPaymentStatus()==PaymentStatus.PAID) throw new BadRequestException("Payment already paid"); p.setPaymentStatus(PaymentStatus.PAID); p.setPaidAt(LocalDateTime.now()); p.setTransactionCode(r==null?null:r.getTransactionCode()); p.getOrder().setOrderStatus(OrderStatus.CONFIRMED); return toResponse(paymentRepository.save(p)); }
 @Transactional public PaymentResponse fail(Long id){ Payment p=getPayment(id); p.setPaymentStatus(PaymentStatus.FAILED); return toResponse(paymentRepository.save(p)); }
 @Transactional public PaymentResponse refund(Long id){ Payment p=getPayment(id); p.setPaymentStatus(PaymentStatus.REFUNDED); return toResponse(paymentRepository.save(p)); }
 public Payment getPayment(Long id){ return paymentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Payment not found")); }
 PaymentResponse toResponse(Payment p){ return PaymentResponse.builder().paymentId(p.getPaymentId()).orderId(p.getOrder().getOrderId()).paymentMethod(String.valueOf(p.getPaymentMethod())).paymentStatus(String.valueOf(p.getPaymentStatus())).amount(p.getAmount()).transactionCode(p.getTransactionCode()).paidAt(p.getPaidAt()).createdAt(p.getCreatedAt()).build(); }
}
