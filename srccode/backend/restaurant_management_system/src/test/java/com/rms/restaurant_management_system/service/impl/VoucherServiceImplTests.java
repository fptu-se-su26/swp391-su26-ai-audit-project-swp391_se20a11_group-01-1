package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.CreateVoucherRequest;
import com.rms.restaurant_management_system.dto.request.ValidateVoucherRequest;
import com.rms.restaurant_management_system.dto.response.ValidateVoucherResponse;
import com.rms.restaurant_management_system.entity.Order;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoucherServiceImplTests {

    @Mock VoucherRepository voucherRepository;
    @Mock VoucherUsageRepository voucherUsageRepository;
    @Mock UserRepository userRepository;
    @Mock OrderRepository orderRepository;
    @Mock ReservationRepository reservationRepository;

    private VoucherServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new VoucherServiceImpl(
                voucherRepository, voucherUsageRepository, userRepository, orderRepository, reservationRepository);
    }

    @Test
    void validateVoucherAppliesPercentAndMaximumDiscount() {
        Voucher voucher = validVoucher();
        voucher.setDiscountValue(BigDecimal.valueOf(50));
        voucher.setMaxDiscountAmount(BigDecimal.valueOf(30_000));
        when(voucherRepository.findByCodeIgnoreCase("SALE50")).thenReturn(Optional.of(voucher));
        when(voucherUsageRepository.countByVoucherIdAndUserUserIdAndStatusNot(1L, 7L, VoucherUsageStatus.REVERSED))
                .thenReturn(0);

        ValidateVoucherResponse result = service.validateVoucher(
                ValidateVoucherRequest.builder().code(" sale50 ").orderTotal(BigDecimal.valueOf(100_000)).build(), 7L);

        assertTrue(result.isValid());
        assertEquals(0, BigDecimal.valueOf(30_000).compareTo(result.getDiscountAmount()));
        assertEquals(0, BigDecimal.valueOf(70_000).compareTo(result.getFinalTotal()));
    }

    @Test
    void createRejectsPercentAboveOneHundred() {
        CreateVoucherRequest request = CreateVoucherRequest.builder()
                .code("BAD")
                .name("Bad voucher")
                .discountType(DiscountType.PERCENT)
                .discountValue(BigDecimal.valueOf(101))
                .minOrderAmount(BigDecimal.ZERO)
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(1))
                .usageLimit(10)
                .usageLimitPerUser(1)
                .build();

        RuntimeException error = assertThrows(RuntimeException.class, () -> service.createVoucher(request));
        assertTrue(error.getMessage().contains("100%"));
        verifyNoInteractions(voucherRepository);
    }

    @Test
    void applyVoucherCreatesUsageAndConsumesExactlyOneSlot() {
        Voucher voucher = validVoucher();
        User user = mock(User.class);
        Order order = mock(Order.class);
        when(voucherRepository.findByCodeForUpdate("SALE50")).thenReturn(Optional.of(voucher));
        when(voucherUsageRepository.countByVoucherIdAndUserUserIdAndStatusNot(1L, 7L, VoucherUsageStatus.REVERSED))
                .thenReturn(0);
        when(voucherUsageRepository.existsByOrderOrderIdAndStatus(9L, VoucherUsageStatus.APPLIED)).thenReturn(false);
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(orderRepository.findById(9L)).thenReturn(Optional.of(order));

        service.applyVoucher("sale50", 7L, 9L, null, BigDecimal.valueOf(20_000));

        assertEquals(1, voucher.getUsedCount());
        ArgumentCaptor<VoucherUsage> usageCaptor = ArgumentCaptor.forClass(VoucherUsage.class);
        verify(voucherUsageRepository).save(usageCaptor.capture());
        assertSame(voucher, usageCaptor.getValue().getVoucher());
        assertSame(user, usageCaptor.getValue().getUser());
        assertSame(order, usageCaptor.getValue().getOrder());
        assertEquals(VoucherUsageStatus.APPLIED, usageCaptor.getValue().getStatus());
    }

    @Test
    void reverseVoucherIsIdempotent() {
        Voucher voucher = validVoucher();
        voucher.setUsedCount(1);
        VoucherUsage usage = VoucherUsage.builder().voucher(voucher).status(VoucherUsageStatus.APPLIED)
                .discountAmount(BigDecimal.TEN).build();
        when(voucherUsageRepository.findByVoucherCodeAndOrderOrderId("SALE50", 9L)).thenReturn(Optional.of(usage));
        when(voucherRepository.findByCodeForUpdate("SALE50")).thenReturn(Optional.of(voucher));

        service.reverseVoucher("sale50", 9L, null);
        service.reverseVoucher("sale50", 9L, null);

        assertEquals(VoucherUsageStatus.REVERSED, usage.getStatus());
        assertEquals(0, voucher.getUsedCount());
        verify(voucherRepository, times(1)).save(voucher);
    }

    private Voucher validVoucher() {
        return Voucher.builder()
                .id(1L)
                .code("SALE50")
                .name("Sale")
                .discountType(DiscountType.PERCENT)
                .discountValue(BigDecimal.TEN)
                .minOrderAmount(BigDecimal.ZERO)
                .startAt(LocalDateTime.now().minusHours(1))
                .endAt(LocalDateTime.now().plusHours(1))
                .usageLimit(10)
                .usageLimitPerUser(1)
                .usedCount(0)
                .active(true)
                .build();
    }
}
