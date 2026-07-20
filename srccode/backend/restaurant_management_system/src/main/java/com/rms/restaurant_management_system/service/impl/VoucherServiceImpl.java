package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.ValidateVoucherRequest;
import com.rms.restaurant_management_system.dto.request.VoucherRequest;
import com.rms.restaurant_management_system.dto.response.ValidateVoucherResponse;
import com.rms.restaurant_management_system.dto.response.VoucherResponse;
import com.rms.restaurant_management_system.entity.Voucher;
import com.rms.restaurant_management_system.repository.VoucherRepository;
import com.rms.restaurant_management_system.service.interfaces.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;

    @Override
    @Transactional
    public VoucherResponse createVoucher(VoucherRequest request) {
        if (voucherRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã voucher đã tồn tại");
        }

        Voucher voucher = Voucher.builder()
                .code(request.getCode().toUpperCase())
                .discount(request.getDiscount())
                .type(request.getType())
                .minOrder(request.getMinOrder())
                .total(request.getTotal())
                .active(request.getActive() != null ? request.getActive() : true)
                .expiry(request.getExpiry())
                .build();

        voucher = voucherRepository.save(voucher);
        return mapToResponse(voucher);
    }

    @Override
    @Transactional
    public VoucherResponse updateVoucher(Long id, VoucherRequest request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));

        Optional<Voucher> existing = voucherRepository.findByCode(request.getCode());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new RuntimeException("Mã voucher đã tồn tại");
        }

        voucher.setCode(request.getCode().toUpperCase());
        voucher.setDiscount(request.getDiscount());
        voucher.setType(request.getType());
        voucher.setMinOrder(request.getMinOrder());
        voucher.setTotal(request.getTotal());
        if (request.getActive() != null) {
            voucher.setActive(request.getActive());
        }
        voucher.setExpiry(request.getExpiry());

        voucher = voucherRepository.save(voucher);
        return mapToResponse(voucher);
    }

    @Override
    @Transactional
    public void deleteVoucher(Long id) {
        if (!voucherRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy voucher");
        }
        voucherRepository.deleteById(id);
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
    public ValidateVoucherResponse validateVoucher(ValidateVoucherRequest request) {
        Optional<Voucher> voucherOpt = voucherRepository.findByCode(request.getCode());

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

        if (voucher.getExpiry().isBefore(LocalDate.now())) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Mã voucher đã hết hạn")
                    .discountAmount(BigDecimal.ZERO)
                    .finalTotal(request.getOrderTotal())
                    .build();
        }

        if (voucher.getUsed() >= voucher.getTotal()) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Mã voucher đã hết lượt sử dụng")
                    .discountAmount(BigDecimal.ZERO)
                    .finalTotal(request.getOrderTotal())
                    .build();
        }

        if (request.getOrderTotal().compareTo(voucher.getMinOrder()) < 0) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Đơn hàng chưa đạt giá trị tối thiểu " + voucher.getMinOrder())
                    .discountAmount(BigDecimal.ZERO)
                    .finalTotal(request.getOrderTotal())
                    .build();
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if ("PERCENT".equalsIgnoreCase(voucher.getType())) {
            discountAmount = request.getOrderTotal().multiply(voucher.getDiscount()).divide(BigDecimal.valueOf(100));
        } else {
            discountAmount = voucher.getDiscount();
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
    public void applyVoucher(String code) {
        voucherRepository.findByCode(code).ifPresent(voucher -> {
            voucher.setUsed(voucher.getUsed() + 1);
            voucherRepository.save(voucher);
        });
    }

    private VoucherResponse mapToResponse(Voucher voucher) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .discount(voucher.getDiscount())
                .type(voucher.getType())
                .minOrder(voucher.getMinOrder())
                .used(voucher.getUsed())
                .total(voucher.getTotal())
                .active(voucher.getActive())
                .expiry(voucher.getExpiry())
                .build();
    }
}
