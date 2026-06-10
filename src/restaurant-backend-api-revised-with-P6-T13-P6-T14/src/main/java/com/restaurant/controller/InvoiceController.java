package com.restaurant.controller;
import com.restaurant.common.ApiResponse; import com.restaurant.dto.invoice.InvoiceResponse; import com.restaurant.service.InvoiceService; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/invoices") @RequiredArgsConstructor
public class InvoiceController { private final InvoiceService service;
 @PostMapping("/orders/{orderId}/generate") public ApiResponse<InvoiceResponse> generate(@PathVariable Long orderId){ return ApiResponse.ok("Invoice generated", service.generate(orderId)); }
 @GetMapping("/{id}") public ApiResponse<InvoiceResponse> get(@PathVariable Long id){ return ApiResponse.ok("Invoice detail", service.get(id)); }
 @GetMapping("/orders/{orderId}") public ApiResponse<InvoiceResponse> byOrder(@PathVariable Long orderId){ return ApiResponse.ok("Invoice by order", service.byOrder(orderId)); }
 @GetMapping("/orders/{orderId}/pdf") public ResponseEntity<byte[]> pdf(@PathVariable Long orderId){ byte[] data=service.pdfBytes(orderId); return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=invoice-"+orderId+".pdf").contentType(MediaType.APPLICATION_PDF).body(data); }
}
