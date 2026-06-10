package com.restaurant.controller;
import com.restaurant.common.ApiResponse; import com.restaurant.dto.order.*; import com.restaurant.service.OrderService; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequiredArgsConstructor
public class OrderController { private final OrderService orderService;
 @PostMapping("/orders") public ApiResponse<OrderResponse> create(@RequestBody CreateOrderRequest r){ return ApiResponse.ok("Order created", orderService.createFromCart(r)); }
 @GetMapping("/orders") public ApiResponse<List<OrderResponse>> mine(){ return ApiResponse.ok("My orders", orderService.myOrders()); }
 @GetMapping("/orders/{id}") public ApiResponse<OrderResponse> detail(@PathVariable Long id){ return ApiResponse.ok("Order detail", orderService.myOrderDetail(id)); }
 @GetMapping("/admin/orders") public ApiResponse<List<OrderResponse>> adminList(){ return ApiResponse.ok("All orders", orderService.adminList()); }
 @GetMapping("/admin/orders/{id}") public ApiResponse<OrderResponse> adminDetail(@PathVariable Long id){ return ApiResponse.ok("Order detail", orderService.adminDetail(id)); }
 @PatchMapping("/admin/orders/{id}/status") public ApiResponse<OrderResponse> status(@PathVariable Long id,@Valid @RequestBody UpdateOrderStatusRequest r){ return ApiResponse.ok("Order status updated", orderService.updateStatus(id,r.getOrderStatus())); }
}
