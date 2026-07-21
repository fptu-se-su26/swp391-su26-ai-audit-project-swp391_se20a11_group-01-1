package com.rms.restaurant_management_system.security;

import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.repository.FeedbackRepository;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("domainAuthorization")
@RequiredArgsConstructor
public class DomainAuthorizationService {

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    private final FeedbackRepository feedbackRepository;

    public boolean canAccessOrder(Long orderId, Authentication authentication) {
        if (isOperator(authentication)) {
            return true;
        }
        User user = principal(authentication);
        return user != null && orderRepository.existsByOrderIdAndUserUserId(orderId, user.getUserId());
    }

    public boolean canAccessReservation(Long reservationId, Authentication authentication) {
        if (isOperator(authentication)) {
            return true;
        }
        User user = principal(authentication);
        return user != null
                && reservationRepository.existsByReservationIdAndUserUserId(reservationId, user.getUserId());
    }

    public boolean canDeleteFeedback(Long feedbackId, Authentication authentication) {
        if (hasRole(authentication, "ADMIN")) {
            return true;
        }
        User user = principal(authentication);
        return user != null && feedbackRepository.existsByFeedbackIdAndUserUserId(feedbackId, user.getUserId());
    }

    private boolean isOperator(Authentication authentication) {
        return hasRole(authentication, "ADMIN") || hasRole(authentication, "STAFF");
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    private User principal(Authentication authentication) {
        return authentication != null && authentication.getPrincipal() instanceof User user ? user : null;
    }
}
