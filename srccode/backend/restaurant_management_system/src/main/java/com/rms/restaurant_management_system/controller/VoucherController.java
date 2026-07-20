package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.dto.request.ChangeVoucherStatusRequest;
import com.rms.restaurant_management_system.dto.request.CreateVoucherRequest;
import com.rms.restaurant_management_system.dto.request.UpdateVoucherRequest;
import com.rms.restaurant_management_system.dto.request.ValidateVoucherRequest;
import com.rms.restaurant_management_system.dto.response.ValidateVoucherResponse;
import com.rms.restaurant_management_system.dto.response.VoucherResponse;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.repository.UserRepository;
import com.rms.restaurant_management_system.service.interfaces.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class VoucherController {

    private final VoucherService voucherService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public VoucherResponse createVoucher(@Valid @RequestBody CreateVoucherRequest request) {
        return voucherService.createVoucher(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public VoucherResponse updateVoucher(@PathVariable Long id, @Valid @RequestBody UpdateVoucherRequest request) {
        return voucherService.updateVoucher(id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public VoucherResponse changeVoucherStatus(@PathVariable Long id, @Valid @RequestBody ChangeVoucherStatusRequest request) {
        return voucherService.changeVoucherStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteVoucher(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return "Voucher deleted successfully";
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<VoucherResponse> getAllVouchers() {
        return voucherService.getAllVouchers();
    }

    @GetMapping("/active")
    public List<VoucherResponse> getActiveVouchers() {
        return voucherService.getActiveVouchers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public VoucherResponse getVoucherById(@PathVariable Long id) {
        return voucherService.getVoucherById(id);
    }

    @PostMapping("/validate")
    public ValidateVoucherResponse validateVoucher(
            @Valid @RequestBody ValidateVoucherRequest request,
            org.springframework.security.core.Authentication authentication) {
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            userId = user.getUserId();
        }
        return voucherService.validateVoucher(request, userId);
    }
}
