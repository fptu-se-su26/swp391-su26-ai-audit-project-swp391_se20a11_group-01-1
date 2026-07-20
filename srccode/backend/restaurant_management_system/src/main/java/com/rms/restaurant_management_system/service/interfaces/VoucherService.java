package com.rms.restaurant_management_system.service.interfaces;

import com.rms.restaurant_management_system.dto.request.ChangeVoucherStatusRequest;
import com.rms.restaurant_management_system.dto.request.CreateVoucherRequest;
import com.rms.restaurant_management_system.dto.request.UpdateVoucherRequest;
import com.rms.restaurant_management_system.dto.request.ValidateVoucherRequest;
import com.rms.restaurant_management_system.dto.response.ValidateVoucherResponse;
import com.rms.restaurant_management_system.dto.response.VoucherResponse;

import java.math.BigDecimal;
import java.util.List;

public interface VoucherService {
    VoucherResponse createVoucher(CreateVoucherRequest request);
    
    VoucherResponse updateVoucher(Long id, UpdateVoucherRequest request);

    VoucherResponse changeVoucherStatus(Long id, ChangeVoucherStatusRequest request);
    
    void deleteVoucher(Long id);
    
    List<VoucherResponse> getAllVouchers();
    
    List<VoucherResponse> getActiveVouchers();
    
    VoucherResponse getVoucherById(Long id);
    
    ValidateVoucherResponse validateVoucher(ValidateVoucherRequest request, Long userId);
    
    void applyVoucher(String code, Long userId, Long orderId, Long reservationId, BigDecimal discountAmount);
    
    void reverseVoucher(String code, Long orderId, Long reservationId);
}
