package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.*;
import com.rms.restaurant_management_system.enums.OrderStatus;
import com.rms.restaurant_management_system.enums.PaymentMethod;
import com.rms.restaurant_management_system.enums.PaymentStatus;
import com.rms.restaurant_management_system.enums.ReservationStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class RepositoryPersistenceIntegrationTest {

    @Autowired PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired RefreshTokenRepository refreshTokenRepository;
    @Autowired ReservationRepository reservationRepository;
    @Autowired PaymentRepository paymentRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired UserRepository userRepository;
    @Autowired EntityManager entityManager;

    @Test
    void passwordResetTokenIsConsumedOnlyOnce() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        PasswordResetToken token = passwordResetTokenRepository.saveAndFlush(PasswordResetToken.builder()
                .email("reset@example.test")
                .tokenHash("a".repeat(64))
                .createdAt(now.minusMinutes(1))
                .expiresAt(now.plusMinutes(10))
                .build());

        assertThat(passwordResetTokenRepository.consumeIfValid(token.getId(), token.getTokenHash(), now, 5))
                .isEqualTo(1);
        assertThat(passwordResetTokenRepository.consumeIfValid(token.getId(), token.getTokenHash(), now.plusSeconds(1), 5))
                .isZero();
    }

    @Test
    void refreshTokenFamilyRevocationUpdatesOnlyActiveFamilyMembers() {
        User user = persistedUser("refresh-user");
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user).tokenHash("b".repeat(64)).familyId("family-a")
                .createdAt(now.minusMinutes(2)).expiresAt(now.plusDays(1)).build());
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user).tokenHash("c".repeat(64)).familyId("family-a")
                .createdAt(now.minusMinutes(1)).expiresAt(now.plusDays(1)).revokedAt(now.minusSeconds(1)).build());
        refreshTokenRepository.saveAndFlush(RefreshToken.builder()
                .user(user).tokenHash("d".repeat(64)).familyId("family-b")
                .createdAt(now).expiresAt(now.plusDays(1)).build());

        assertThat(refreshTokenRepository.revokeFamily("family-a", now)).isEqualTo(1);
        entityManager.clear();
        assertThat(refreshTokenRepository.findByTokenHash("b".repeat(64)).orElseThrow().getRevokedAt()).isEqualTo(now);
        assertThat(refreshTokenRepository.findByTokenHash("d".repeat(64)).orElseThrow().getRevokedAt()).isNull();
    }

    @Test
    void overlapQueryHonorsTimeBoundariesAndActiveStatuses() {
        LocalDateTime start = LocalDate.now().plusDays(1).atTime(18, 0);
        reservationRepository.saveAndFlush(reservation("RSV-1", 10L, start, start.plusHours(2), ReservationStatus.CONFIRMED));
        reservationRepository.saveAndFlush(reservation("RSV-2", 10L, start.plusHours(2), start.plusHours(3), ReservationStatus.PENDING));
        reservationRepository.saveAndFlush(reservation("RSV-3", 10L, start, start.plusHours(2), ReservationStatus.CANCELLED));

        assertThat(reservationRepository.countOverlapping(10L, start.plusMinutes(30), start.plusHours(1), null))
                .isEqualTo(1);
        assertThat(reservationRepository.countOverlapping(10L, start.plusHours(2), start.plusHours(3), null))
                .isEqualTo(1);
        assertThat(reservationRepository.countOverlapping(99L, start, start.plusHours(3), null))
                .isZero();
    }

    @Test
    void databaseRejectsSecondPaymentForSameOrder() {
        Order order = orderRepository.saveAndFlush(Order.builder()
                .orderCode("ORD-UNIQUE-PAYMENT")
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("250000.00"))
                .build());
        paymentRepository.saveAndFlush(payment(order, 101L));

        assertThatThrownBy(() -> paymentRepository.saveAndFlush(payment(order, 102L)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private User persistedUser(String suffix) {
        Role role = roleRepository.save(Role.builder().roleName("ROLE_" + suffix).build());
        return userRepository.saveAndFlush(User.builder()
                .username(suffix)
                .email(suffix + "@example.test")
                .passwordHash("not-a-real-password-hash")
                .role(role)
                .build());
    }

    private Reservation reservation(String code, Long tableId, LocalDateTime start,
                                    LocalDateTime end, ReservationStatus status) {
        return Reservation.builder()
                .reservationCode(code)
                .reservationDate(start.toLocalDate())
                .reservationTime(start.toLocalTime().toString())
                .numberOfGuests(2)
                .customerName("Integration Test")
                .customerPhone("0000000000")
                .assignedTableId(tableId)
                .startAt(start)
                .endAt(end)
                .status(status)
                .preOrderTotal(BigDecimal.ZERO)
                .build();
    }

    private Payment payment(Order order, Long payosOrderCode) {
        return Payment.builder()
                .order(order)
                .method(PaymentMethod.QR)
                .status(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .payosOrderCode(payosOrderCode)
                .build();
    }
}
