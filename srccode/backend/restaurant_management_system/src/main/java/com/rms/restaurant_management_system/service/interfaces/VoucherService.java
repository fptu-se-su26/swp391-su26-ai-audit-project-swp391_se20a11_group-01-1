package com.rms.restaurant_management_system.service.interfaces;

import com.rms.restaurant_management_system.dto.request.ValidateVoucherRequest;
import com.rms.restaurant_management_system.dto.request.VoucherRequest;
import com.rms.restaurant_management_system.dto.response.ValidateVoucherResponse;
import com.rms.restaurant_management_system.dto.response.VoucherResponse;

import java.util.List;

public interface VoucherService {
    VoucherResponse createVoucher(VoucherRequest request);
    
    VoucherResponse updateVoucher(Long id, VoucherRequest request);
    
    void deleteVoucher(Long id);
    
    List<VoucherResponse> getAllVouchers();
    
    List<VoucherResponse> getActiveVouchers();
    
    VoucherResponse getVoucherById(Long id);
    
    ValidateVoucherResponse validateVoucher(ValidateVoucherRequest request);
    
    void applyVoucher(String code);
}
