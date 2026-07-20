package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.ChangeVoucherStatusRequest;
import com.rms.restaurant_management_system.dto.request.CreateVoucherRequest;
import com.rms.restaurant_management_system.dto.request.UpdateVoucherRequest;
import com.rms.restaurant_management_system.dto.request.ValidateVoucherRequest;
import com.rms.restaurant_management_system.dto.response.ValidateVoucherResponse;
import com.rms.restaurant_management_system.dto.response.VoucherResponse;
import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.Reservation;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.entity.Voucher;
import com.rms.restaurant_management_system.entity.VoucherUsage;
import com.rms.restaurant_management_system.enums.DiscountType;
import com.rms.restaurant_management_system.enums.VoucherUsageStatus;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.ReservationRepository;
import com.rms.restaurant_management_system.repository.UserRepository;
import com.rms.restaurant_management_system.repository.VoucherRepository;
import com.rms.restaurant_management_system.repository.VoucherUsageRepository;
import com.rms.restaurant_management_system.service.interfaces.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository voucherUsageRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public VoucherResponse createVoucher(CreateVoucherRequest request) {
        String normalizedCode = normalizeCode(request.getCode());
        validateDefinition(request.getDiscountType(), request.getDiscountValue(), request.getMaxDiscountAmount(),
                request.getStartAt(), request.getEndAt(), request.getUsageLimit(), 0);
        if (voucherRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new RuntimeException("Mã voucher đã tồn tại");
        }

        Voucher voucher = Voucher.builder()
                .code(normalizedCode)
                .name(request.getName())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minOrderAmount(request.getMinOrderAmount())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .usageLimit(request.getUsageLimit())
                .usageLimitPerUser(request.getUsageLimitPerUser())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        voucher = voucherRepository.save(voucher);
        return mapToResponse(voucher);
    }

    @Override
    @Transactional
    public VoucherResponse updateVoucher(Long id, UpdateVoucherRequest request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));

        validateDefinition(request.getDiscountType(), request.getDiscountValue(), request.getMaxDiscountAmount(),
                request.getStartAt(), request.getEndAt(), request.getUsageLimit(), voucher.getUsedCount());

        voucher.setName(request.getName());
        voucher.setDescription(request.getDescription());
        voucher.setDiscountType(request.getDiscountType());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMaxDiscountAmount(request.getMaxDiscountAmount());
        voucher.setMinOrderAmount(request.getMinOrderAmount());
        voucher.setStartAt(request.getStartAt());
        voucher.setEndAt(request.getEndAt());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setUsageLimitPerUser(request.getUsageLimitPerUser());

        voucher = voucherRepository.save(voucher);
        return mapToResponse(voucher);
    }

    @Override
    @Transactional
    public VoucherResponse changeVoucherStatus(Long id, ChangeVoucherStatusRequest request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));
        voucher.setActive(request.getActive());
        voucher = voucherRepository.save(voucher);
        return mapToResponse(voucher);
    }

    @Override
    @Transactional
    public void deleteVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));
        
        long usageCount = voucherUsageRepository.countByVoucherId(id);
        if (usageCount > 0) {
            voucher.setActive(false);
            voucherRepository.save(voucher);
        } else {
            voucherRepository.deleteById(id);
        }
    }

    @Override
    public List<VoucherResponse> getAllVouchers() {
        return voucherRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherResponse> getActiveVouchers() {
        return voucherRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VoucherResponse getVoucherById(Long id) {
        return voucherRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));
    }

    @Override
    public ValidateVoucherResponse validateVoucher(ValidateVoucherRequest request, Long userId) {
        Optional<Voucher> voucherOpt = voucherRepository.findByCodeIgnoreCase(normalizeCode(request.getCode()));

        if (voucherOpt.isEmpty()) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Mã voucher không tồn tại")
                    .discountAmount(BigDecimal.ZERO)
                    .finalTotal(request.getOrderTotal())
                    .build();
        }

        Voucher voucher = voucherOpt.get();

        if (!voucher.getActive()) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Mã voucher đã bị vô hiệu hóa")
                    .discountAmount(BigDecimal.ZERO)
                    .finalTotal(request.getOrderTotal())
                    .build();
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartAt())) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Mã voucher chưa đến thời gian áp dụng")
                    .discountAmount(BigDecimal.ZERO)
                    .finalTotal(request.getOrderTotal())
                    .build();
        }

        if (now.isAfter(voucher.getEndAt())) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Mã voucher đã hết hạn")
                    .discountAmount(BigDecimal.ZERO)
                    .finalTotal(request.getOrderTotal())
                    .build();
        }

        if (voucher.getUsedCount() >= voucher.getUsageLimit()) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Mã voucher đã hết lượt sử dụng")
                    .discountAmount(BigDecimal.ZERO)
                    .finalTotal(request.getOrderTotal())
                    .build();
        }

        int userUsageCount = 0;
        if (userId != null) {
            userUsageCount = voucherUsageRepository.countByVoucherIdAndUserUserIdAndStatusNot(
                    voucher.getId(), userId, VoucherUsageStatus.REVERSED);
        }
        
        if (userUsageCount >= voucher.getUsageLimitPerUser()) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Bạn đã hết lượt sử dụng mã voucher này")
                    .discountAmount(BigDecimal.ZERO)
                    .finalTotal(request.getOrderTotal())
                    .build();
        }

        if (request.getOrderTotal().compareTo(voucher.getMinOrderAmount()) < 0) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Đơn hàng chưa đạt giá trị tối thiểu " + voucher.getMinOrderAmount())
                    .discountAmount(BigDecimal.ZERO)
                    .finalTotal(request.getOrderTotal())
                    .build();
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (voucher.getDiscountType() == DiscountType.PERCENT) {
            discountAmount = request.getOrderTotal().multiply(voucher.getDiscountValue()).divide(BigDecimal.valueOf(100));
            if (voucher.getMaxDiscountAmount() != null && discountAmount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
                discountAmount = voucher.getMaxDiscountAmount();
            }
        } else {
            discountAmount = voucher.getDiscountValue();
        }

        // Prevent discount from exceeding order total
        if (discountAmount.compareTo(request.getOrderTotal()) > 0) {
            discountAmount = request.getOrderTotal();
        }

        BigDecimal finalTotal = request.getOrderTotal().subtract(discountAmount);

        return ValidateVoucherResponse.builder()
                .valid(true)
                .message("Áp dụng mã thành công")
                .discountAmount(discountAmount)
                .finalTotal(finalTotal)
                .build();
    }

    @Override
    @Transactional
    public void applyVoucher(String code, Long userId, Long orderId, Long reservationId, BigDecimal discountAmount) {
        if ((orderId == null) == (reservationId == null)) {
            throw new RuntimeException("Phải cung cấp chính xác một order hoặc reservation");
        }
        if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Số tiền giảm không hợp lệ");
        }

        String normalizedCode = normalizeCode(code);
        // Lock the voucher to serialize concurrent applications
        Voucher voucher = voucherRepository.findByCodeForUpdate(normalizedCode)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));

        LocalDateTime now = LocalDateTime.now();
        if (!voucher.getActive() || now.isBefore(voucher.getStartAt()) || now.isAfter(voucher.getEndAt())
                || voucher.getUsedCount() >= voucher.getUsageLimit()) {
            throw new RuntimeException("Voucher không khả dụng hoặc đã hết lượt sử dụng");
        }

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng áp dụng voucher"));
            int userUsageCount = voucherUsageRepository.countByVoucherIdAndUserUserIdAndStatusNot(
                    voucher.getId(), userId, VoucherUsageStatus.REVERSED);
            if (userUsageCount >= voucher.getUsageLimitPerUser()) {
                throw new RuntimeException("Bạn đã hết lượt sử dụng mã voucher này");
            }
        }

        Order order = null;
        if (orderId != null) {
            if (voucherUsageRepository.existsByOrderOrderIdAndStatus(orderId, VoucherUsageStatus.APPLIED)) {
                throw new RuntimeException("Order này đã được áp dụng voucher");
            }
            order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy order áp dụng voucher"));
        }
        
        Reservation reservation = null;
        if (reservationId != null) {
            if (voucherUsageRepository.existsByReservationReservationIdAndStatus(reservationId, VoucherUsageStatus.APPLIED)) {
                throw new RuntimeException("Reservation này đã được áp dụng voucher");
            }
            reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy reservation áp dụng voucher"));
        }

        voucher.setUsedCount(voucher.getUsedCount() + 1);
        voucherRepository.save(voucher);

        VoucherUsage usage = VoucherUsage.builder()
                .voucher(voucher)
                .user(user)
                .order(order)
                .reservation(reservation)
                .discountAmount(discountAmount)
                .status(VoucherUsageStatus.APPLIED)
                .build();
        
        voucherUsageRepository.save(usage);
    }
    
    @Override
    @Transactional
    public void reverseVoucher(String code, Long orderId, Long reservationId) {
        String normalizedCode = normalizeCode(code);
        VoucherUsage usage;
        if (orderId != null) {
            usage = voucherUsageRepository.findByVoucherCodeAndOrderOrderId(normalizedCode, orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lượt sử dụng voucher cho order này"));
        } else if (reservationId != null) {
            usage = voucherUsageRepository.findByVoucherCodeAndReservationReservationId(normalizedCode, reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lượt sử dụng voucher cho reservation này"));
        } else {
            throw new RuntimeException("Phải cung cấp order hoặc reservation để hoàn voucher");
        }

        if (usage.getStatus() == VoucherUsageStatus.REVERSED) {
            return;
        }

        Voucher voucher = voucherRepository.findByCodeForUpdate(normalizedCode)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
        usage.setStatus(VoucherUsageStatus.REVERSED);
        usage.setReversedAt(LocalDateTime.now());
        voucherUsageRepository.save(usage);
        if (voucher.getUsedCount() > 0) {
            voucher.setUsedCount(voucher.getUsedCount() - 1);
            voucherRepository.save(voucher);
        }
    }

    private String normalizeCode(String code) {
        return code == null ? "" : code.trim().toUpperCase();
    }

    private void validateDefinition(
            DiscountType discountType,
            BigDecimal discountValue,
            BigDecimal maxDiscountAmount,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Integer usageLimit,
            int usedCount) {
        if (startAt == null || endAt == null || !endAt.isAfter(startAt)) {
            throw new RuntimeException("Thời gian kết thúc phải sau thời gian bắt đầu");
        }
        if (discountType == DiscountType.PERCENT && discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("Voucher phần trăm không được vượt quá 100%");
        }
        if (discountType == DiscountType.FIXED && maxDiscountAmount != null) {
            throw new RuntimeException("Giảm tối đa chỉ áp dụng cho voucher phần trăm");
        }
        if (usageLimit < usedCount) {
            throw new RuntimeException("Giới hạn sử dụng không được nhỏ hơn số lượt đã dùng");
        }
    }

    private VoucherResponse mapToResponse(Voucher voucher) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .name(voucher.getName())
                .description(voucher.getDescription())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .maxDiscountAmount(voucher.getMaxDiscountAmount())
                .minOrderAmount(voucher.getMinOrderAmount())
                .startAt(voucher.getStartAt())
                .endAt(voucher.getEndAt())
                .usageLimit(voucher.getUsageLimit())
                .usageLimitPerUser(voucher.getUsageLimitPerUser())
                .usedCount(voucher.getUsedCount())
                .active(voucher.getActive())
                .version(voucher.getVersion())
                .createdAt(voucher.getCreatedAt())
                .updatedAt(voucher.getUpdatedAt())
                .build();
    }
}
