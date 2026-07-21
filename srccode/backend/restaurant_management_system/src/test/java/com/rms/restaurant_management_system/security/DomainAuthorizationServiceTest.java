package com.rms.restaurant_management_system.security;

import com.rms.restaurant_management_system.entity.Role;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.repository.FeedbackRepository;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DomainAuthorizationServiceTest {
    @Mock OrderRepository orderRepository;
    @Mock ReservationRepository reservationRepository;
    @Mock FeedbackRepository feedbackRepository;
    DomainAuthorizationService authorization;

    @BeforeEach
    void setUp() {
        authorization = new DomainAuthorizationService(orderRepository, reservationRepository, feedbackRepository);
    }

    @Test
    void customerCanOnlyReadOwnedOrder() {
        var authentication = authentication("CUSTOMER", 7L);
        when(orderRepository.existsByOrderIdAndUserUserId(11L, 7L)).thenReturn(true);
        when(orderRepository.existsByOrderIdAndUserUserId(12L, 7L)).thenReturn(false);

        assertThat(authorization.canAccessOrder(11L, authentication)).isTrue();
        assertThat(authorization.canAccessOrder(12L, authentication)).isFalse();
    }

    @Test
    void staffCanAccessOrderWithoutOwnershipLookup() {
        assertThat(authorization.canAccessOrder(11L, authentication("STAFF", 2L))).isTrue();
        verifyNoInteractions(orderRepository);
    }

    @Test
    void customerCanOnlyReadOwnedReservation() {
        var authentication = authentication("CUSTOMER", 7L);
        when(reservationRepository.existsByReservationIdAndUserUserId(21L, 7L)).thenReturn(true);
        assertThat(authorization.canAccessReservation(21L, authentication)).isTrue();
        assertThat(authorization.canAccessReservation(22L, authentication)).isFalse();
    }

    @Test
    void customerCannotDeleteAnotherCustomersFeedback() {
        var authentication = authentication("CUSTOMER", 7L);
        when(feedbackRepository.existsByFeedbackIdAndUserUserId(31L, 7L)).thenReturn(false);
        assertThat(authorization.canDeleteFeedback(31L, authentication)).isFalse();
    }

    @Test
    void adminCanDeleteAnyFeedback() {
        assertThat(authorization.canDeleteFeedback(31L, authentication("ADMIN", 1L))).isTrue();
        verifyNoInteractions(feedbackRepository);
    }

    private UsernamePasswordAuthenticationToken authentication(String role, Long userId) {
        User user = User.builder().userId(userId).email("user@example.com")
                .role(Role.builder().roleName(role).build()).build();
        return new UsernamePasswordAuthenticationToken(user, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    }
}
