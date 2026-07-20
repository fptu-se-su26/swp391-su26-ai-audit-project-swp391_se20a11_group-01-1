package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.dto.request.ValidateVoucherRequest;
import com.rms.restaurant_management_system.dto.request.VoucherRequest;
import com.rms.restaurant_management_system.dto.response.ValidateVoucherResponse;
import com.rms.restaurant_management_system.dto.response.VoucherResponse;
import com.rms.restaurant_management_system.service.interfaces.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class VoucherController {

    private final VoucherService voucherService;

    @PostMapping
    public VoucherResponse createVoucher(@Valid @RequestBody VoucherRequest request) {
        return voucherService.createVoucher(request);
    }

    @PutMapping("/{id}")
    public VoucherResponse updateVoucher(@PathVariable Long id, @Valid @RequestBody VoucherRequest request) {
        return voucherService.updateVoucher(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteVoucher(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return "Voucher deleted successfully";
    }

    @GetMapping
    public List<VoucherResponse> getAllVouchers() {
        return voucherService.getAllVouchers();
    }

    @GetMapping("/active")
    public List<VoucherResponse> getActiveVouchers() {
        return voucherService.getActiveVouchers();
    }

    @GetMapping("/{id}")
    public VoucherResponse getVoucherById(@PathVariable Long id) {
        return voucherService.getVoucherById(id);
    }

    @PostMapping("/validate")
    public ValidateVoucherResponse validateVoucher(@Valid @RequestBody ValidateVoucherRequest request) {
        return voucherService.validateVoucher(request);
    }
}
