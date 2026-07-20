package com.rms.restaurant_management_system.service.impl;


import com.rms.restaurant_management_system.dto.request.OrderItemRequest;
import com.rms.restaurant_management_system.dto.request.OrderRequest;
import com.rms.restaurant_management_system.dto.request.UpdateOrderStatusRequest;
import com.rms.restaurant_management_system.dto.response.OrderItemResponse;
import com.rms.restaurant_management_system.dto.response.OrderResponse;

import com.rms.restaurant_management_system.entity.Food;
import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.OrderItem;
import com.rms.restaurant_management_system.entity.RestaurantTable;
import com.rms.restaurant_management_system.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.rms.restaurant_management_system.dto.request.ValidateVoucherRequest;
import com.rms.restaurant_management_system.dto.response.ValidateVoucherResponse;

import com.rms.restaurant_management_system.enums.OrderStatus;
import com.rms.restaurant_management_system.enums.PaymentStatus;
import com.rms.restaurant_management_system.enums.TableStatus;

import com.rms.restaurant_management_system.repository.FoodRepository;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.PaymentRepository;
import com.rms.restaurant_management_system.repository.RestaurantTableRepository;
import com.rms.restaurant_management_system.repository.UserRepository;

import com.rms.restaurant_management_system.service.interfaces.OrderService;
import com.rms.restaurant_management_system.service.interfaces.VoucherService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {



    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final FoodRepository foodRepository;

    private final RestaurantTableRepository restaurantTableRepository;

    private final PaymentRepository paymentRepository;

    private final VoucherService voucherService;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = null;

        Long authUserId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            authUserId = ((User) authentication.getPrincipal()).getUserId();
        }

        // Customer QR không cần đăng nhập
        if (authUserId != null) {
            user = userRepository.findById(authUserId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        RestaurantTable table = null;

        if (request.getTableId() != null) {
            table = restaurantTableRepository.findByTableIdForUpdate(request.getTableId())
                    .orElseThrow(() -> new RuntimeException("Table not found"));

            if (table.getIsActive() == null || !table.getIsActive()) {
                throw new RuntimeException("Table is inactive");
            }
        }

        Order activeOrder = findAppendableActiveOrder(table);

        if (activeOrder != null) {
            appendItems(activeOrder, request.getItems());
            recalculateTotalAmount(activeOrder);
            
            if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
                
                String oldVoucher = activeOrder.getVoucherCode();
                if (oldVoucher != null && !oldVoucher.equals(request.getVoucherCode())) {
                    voucherService.reverseVoucher(oldVoucher, activeOrder.getOrderId(), null);
                }

                // To validate new voucher, we need the RAW total amount
                BigDecimal rawTotal = activeOrder.getItems().stream()
                        .map(OrderItem::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                ValidateVoucherResponse validation = voucherService.validateVoucher(
                        ValidateVoucherRequest.builder()
                                .code(request.getVoucherCode())
                                .orderTotal(rawTotal)
                                .build(),
                        authUserId
                );

                if (validation.isValid()) {
                    activeOrder.setVoucherCode(request.getVoucherCode());
                    activeOrder.setVoucherDiscountAmount(validation.getDiscountAmount());
                    activeOrder.setTotalAmount(validation.getFinalTotal());
                    
                    Order savedActiveOrder = orderRepository.save(activeOrder);
                    voucherService.applyVoucher(
                            request.getVoucherCode(),
                            authUserId,
                            savedActiveOrder.getOrderId(),
                            null,
                            validation.getDiscountAmount()
                    );
                    return mapToResponse(savedActiveOrder);
                } else {
                    throw new RuntimeException("Lỗi mã giảm giá: " + validation.getMessage());
                }
            }
            
            return mapToResponse(orderRepository.save(activeOrder));
        }

        String customerName = request.getCustomerName();

        if ((customerName == null || customerName.isBlank()) && user != null) {
            customerName = user.getUsername();
        }

        if ((customerName == null || customerName.isBlank()) && table != null) {
            customerName = "Khách " + table.getTableName();
        }

        Order order = Order.builder()
                .orderCode(generateOrderCode())
                .user(user)
                .tableId(table != null ? table.getTableId() : null)
                .tableName(table != null ? table.getTableName() : null)
                .customerName(customerName)
                .customerPhone(request.getCustomerPhone())
                .status(OrderStatus.PENDING)
                .note(request.getNote())
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        appendItems(order, request.getItems());
        recalculateTotalAmount(order);

        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            ValidateVoucherResponse validation = voucherService.validateVoucher(
                    ValidateVoucherRequest.builder()
                            .code(request.getVoucherCode())
                            .orderTotal(order.getTotalAmount())
                            .build(),
                    authUserId
            );

            if (validation.isValid()) {
                order.setVoucherCode(request.getVoucherCode());
                order.setVoucherDiscountAmount(validation.getDiscountAmount());
                order.setTotalAmount(validation.getFinalTotal());
                
                Order savedOrder = orderRepository.save(order);
                voucherService.applyVoucher(
                        request.getVoucherCode(),
                        authUserId,
                        savedOrder.getOrderId(),
                        null,
                        validation.getDiscountAmount()
                );
                order = savedOrder;
            } else {
                throw new RuntimeException("Lỗi mã giảm giá: " + validation.getMessage());
            }
        } else {
            order = orderRepository.save(order);
        }

        Order savedOrder = order;

        if (table != null) {
            table.setStatus(TableStatus.OCCUPIED);
            table.setCurrentOrderCode(savedOrder.getOrderCode());
            table.setReservedBy(customerName);
            restaurantTableRepository.save(table);
        }

        return mapToResponse(savedOrder);
    }

    private Order findAppendableActiveOrder(RestaurantTable table) {
        if (table == null
                || table.getCurrentOrderCode() == null
                || table.getCurrentOrderCode().isBlank()) {
            return null;
        }

        Order activeOrder = orderRepository
                .findByOrderCode(table.getCurrentOrderCode().trim())
                .orElse(null);

        if (activeOrder == null
                || activeOrder.getStatus() == OrderStatus.COMPLETED
                || activeOrder.getStatus() == OrderStatus.CANCELLED) {
            return null;
        }

        if (activeOrder.getStatus() == OrderStatus.PREPARING
                || activeOrder.getStatus() == OrderStatus.READY) {
            throw new RuntimeException(
                    "Cannot add items while the active table order is PREPARING or READY"
            );
        }

        if (activeOrder.getStatus() != OrderStatus.PENDING
                && activeOrder.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException(
                    "Cannot add items to the active table order in status "
                            + activeOrder.getStatus()
            );
        }

        return activeOrder;
    }

    private void appendItems(Order order, List<OrderItemRequest> itemRequests) {
        for (OrderItemRequest itemRequest : itemRequests) {
            Food food = foodRepository.findById(itemRequest.getFoodId())
                    .orElseThrow(() -> new RuntimeException("Food not found"));

            if (food.getIsAvailable() == null || !food.getIsAvailable()) {
                throw new RuntimeException("Food unavailable");
            }

            BigDecimal subtotal = food.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .foodId(food.getFoodId())
                    .foodName(food.getFoodName())
                    .unitPrice(food.getPrice())
                    .quantity(itemRequest.getQuantity())
                    .subtotal(subtotal)
                    .imageUrl(food.getImageUrl())
                    .emoji(food.getEmoji())
                    .build();

            order.getItems().add(item);
        }
    }

    private void recalculateTotalAmount(Order order) {
        BigDecimal totalAmount = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (order.getVoucherDiscountAmount() != null) {
            totalAmount = totalAmount.subtract(order.getVoucherDiscountAmount());
            if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                totalAmount = BigDecimal.ZERO;
            }
        }

        order.setTotalAmount(totalAmount);
    }

    @Override
    public List<OrderResponse> getAllOrders(){

        return orderRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public OrderResponse getOrderById(Long orderId){
        Order order =
                orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        )
                );
        return mapToResponse(order);
    }
    @Override
    public List<OrderResponse> getOrdersByCustomer(Long userId){
        return orderRepository
                .findByUserUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public List<OrderResponse> getOrdersByStatus(String status){
        OrderStatus orderStatus;
        try{
            orderStatus =
                    OrderStatus.valueOf(
                            status.toUpperCase()
                    );
        }catch(Exception e){
            throw new RuntimeException(
                    "Invalid order status"
            );
        }
        return orderRepository
                .findByStatusOrderByCreatedAtDesc(
                        orderStatus
                )
                .stream()
                .map(this::mapToResponse)
                .toList();

    }
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(

            Long orderId,

            UpdateOrderStatusRequest request

    ){
        Order order =
                findOrderForStatusUpdate(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        )
                );
        if (request.getStatus() == OrderStatus.CANCELLED) {
            validateOrderCanBeCancelled(order);
        }
        validateStatusTransition(

                order.getStatus(),

                request.getStatus()

        );
        order.setStatus(
                request.getStatus()
        );
        Order savedOrder =
                orderRepository.save(order);
        if(
                request.getStatus()
                        == OrderStatus.COMPLETED
                ||
                request.getStatus()
                        == OrderStatus.CANCELLED
        ){
            releaseTable(savedOrder);

        }
        return mapToResponse(savedOrder);
    }
    @Override
    @Transactional
    public void cancelOrder(Long orderId){
        Order order =
                findOrderForStatusUpdate(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        )
                );

        validateOrderCanBeCancelled(order);

        order.setStatus(
                OrderStatus.CANCELLED
        );
        Order savedOrder =
                orderRepository.save(order);
        releaseTable(savedOrder);
        
        if (savedOrder.getVoucherCode() != null && !savedOrder.getVoucherCode().isBlank()) {
            voucherService.reverseVoucher(savedOrder.getVoucherCode(), savedOrder.getOrderId(), null);
        }
    }
    private void releaseTable(Order order){
        if(order.getTableId() == null)
            return;
        RestaurantTable table =
                restaurantTableRepository
                        .findById(order.getTableId())
                        .orElse(null);
        if(table == null)

            return;

        if (table.getCurrentOrderCode() == null
                || !table.getCurrentOrderCode().equals(order.getOrderCode())) {
            return;
        }

        table.setStatus(
                TableStatus.EMPTY
        );
        table.setCurrentOrderCode(
                null
        );
        table.setReservedBy(
                null
        );
        restaurantTableRepository.save(table);
    }

    private void validateOrderCanBeCancelled(Order order) {
        if (order.getStatus() != OrderStatus.PENDING
                && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException(
                    "Only PENDING or CONFIRMED orders can be cancelled"
            );
        }

        boolean alreadyPaid = paymentRepository.findByOrderOrderId(order.getOrderId())
                .map(payment -> payment.getStatus() == PaymentStatus.PAID)
                .orElse(false);

        if (alreadyPaid) {
            throw new RuntimeException(
                    "Cannot cancel an order that has already been paid"
            );
        }
    }

    private java.util.Optional<Order> findOrderForStatusUpdate(Long orderId) {
        Order orderSnapshot = orderRepository.findById(orderId)
                .orElse(null);

        if (orderSnapshot == null) {
            return java.util.Optional.empty();
        }

        if (orderSnapshot.getTableId() != null) {
            restaurantTableRepository.findByTableIdForUpdate(orderSnapshot.getTableId())
                    .orElseThrow(() -> new RuntimeException("Table not found"));
        }

        return orderRepository.findByOrderId(orderId);
    }

    private void validateStatusTransition(
            OrderStatus current,
            OrderStatus next
    ){
        boolean valid =
                switch(current){
                    case PENDING ->
                            next == OrderStatus.CONFIRMED
                            ||
                            next == OrderStatus.CANCELLED;
                    case CONFIRMED ->
                            next == OrderStatus.PREPARING
                            ||
                            next == OrderStatus.CANCELLED;
                    case PREPARING ->
                            next == OrderStatus.READY;
                    case READY ->
                            next == OrderStatus.COMPLETED;
                    default -> false;
                };
        if(!valid){
            throw new RuntimeException(
                    "Invalid status transition"
            );
        }
    }
    private OrderResponse mapToResponse(Order order){
        List<OrderItemResponse> items =
                order.getItems()
                .stream()
                .map(item ->
                        new OrderItemResponse(
                                item.getOrderItemId(),
                                item.getFoodId(),
                                item.getFoodName(),
                                item.getUnitPrice(),
                                item.getQuantity(),
                                item.getSubtotal(),
                                item.getImageUrl(),
                                item.getEmoji()
                        )
                )
                .toList();
        return new OrderResponse(
                order.getOrderId(),
                order.getOrderCode(),
                order.getUser() != null
                        ?
                        order.getUser().getUserId()
                        :
                        null,
                order.getUser() != null
                        ?
                        order.getUser().getUsername()
                        :
                        order.getCustomerName(),
                order.getUser() != null
                        ?
                        order.getUser().getEmail()
                        :
                        null,
                order.getTableId(),
                order.getTableName(),
                order.getCustomerName(),
                order.getCustomerPhone(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getNote(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items
        );

    }
    private String generateOrderCode(){
        String time =
                java.time.LocalDateTime.now()
                .format(

                        DateTimeFormatter
                        .ofPattern(
                                "yyyyMMddHHmmss"
                        )

                );
        int random =

                (int)(Math.random()*9000)+1000;
        return "ORD-"
                + time
                + "-"
                + random;
    }


}
