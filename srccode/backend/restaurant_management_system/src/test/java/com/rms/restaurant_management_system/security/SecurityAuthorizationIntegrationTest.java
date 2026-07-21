package com.rms.restaurant_management_system.security;

import com.rms.restaurant_management_system.config.SecurityConfig;
import com.rms.restaurant_management_system.controller.*;
import com.rms.restaurant_management_system.entity.Role;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.repository.*;
import com.rms.restaurant_management_system.service.interfaces.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import org.mockito.ArgumentCaptor;
import com.rms.restaurant_management_system.dto.request.OrderRequest;
import com.rms.restaurant_management_system.dto.request.ReservationRequest;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({OrderController.class, ReservationController.class, PaymentController.class,
        RestaurantTableController.class, FeedbackController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, DomainAuthorizationService.class})
@ActiveProfiles("test")
class SecurityAuthorizationIntegrationTest {
    @Autowired MockMvc mvc;

    @MockitoBean OrderService orderService;
    @MockitoBean ReservationService reservationService;
    @MockitoBean PaymentService paymentService;
    @MockitoBean RestaurantTableService tableService;
    @MockitoBean FeedbackService feedbackService;
    @MockitoBean OrderRepository orderRepository;
    @MockitoBean ReservationRepository reservationRepository;
    @MockitoBean FeedbackRepository feedbackRepository;
    @MockitoBean UserRepository userRepository;
    @MockitoBean JwtUtil jwtUtil;

    @Test
    void guestCannotReadProtectedOrder() throws Exception {
        mvc.perform(get("/api/orders/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void customerCannotListAllOrders() throws Exception {
        mvc.perform(get("/api/orders").with(authentication(actor("CUSTOMER", 7L))))
                .andExpect(status().isForbidden());
        verifyNoInteractions(orderService);
    }

    @Test
    void customerCanReadOwnedOrder() throws Exception {
        when(orderRepository.existsByOrderIdAndUserUserId(11L, 7L)).thenReturn(true);
        mvc.perform(get("/api/orders/11").with(authentication(actor("CUSTOMER", 7L))))
                .andExpect(status().isOk());
        verify(orderService).getOrderById(11L);
    }

    @Test
    void customerCannotReadAnotherCustomersOrder() throws Exception {
        mvc.perform(get("/api/orders/12").with(authentication(actor("CUSTOMER", 7L))))
                .andExpect(status().isForbidden());
        verifyNoInteractions(orderService);
    }

    @Test
    void customerCannotReadAnotherCustomersReservation() throws Exception {
        mvc.perform(get("/api/reservations/22").with(authentication(actor("CUSTOMER", 7L))))
                .andExpect(status().isForbidden());
        verifyNoInteractions(reservationService);
    }

    @Test
    void customerCannotSpoofOrderUserId() throws Exception {
        mvc.perform(post("/api/orders").with(authentication(actor("CUSTOMER", 7L)))
                        .contentType("application/json")
                        .content("{\"userId\":999,\"items\":[{\"foodId\":1,\"quantity\":1}]}"))
                .andExpect(status().isOk());
        ArgumentCaptor<OrderRequest> request = ArgumentCaptor.forClass(OrderRequest.class);
        verify(orderService).createOrder(request.capture());
        org.assertj.core.api.Assertions.assertThat(request.getValue().getUserId()).isEqualTo(7L);
    }

    @Test
    void customerCannotSpoofReservationUserId() throws Exception {
        mvc.perform(post("/api/reservations").with(authentication(actor("CUSTOMER", 7L)))
                        .contentType("application/json")
                        .content("{\"userId\":999,\"reservationDate\":\"2030-01-01\","
                                + "\"reservationTime\":\"18:00\",\"numberOfGuests\":2,"
                                + "\"customerName\":\"Customer\",\"customerPhone\":\"0900000000\"}"))
                .andExpect(status().isOk());
        ArgumentCaptor<ReservationRequest> request = ArgumentCaptor.forClass(ReservationRequest.class);
        verify(reservationService).createReservation(request.capture());
        org.assertj.core.api.Assertions.assertThat(request.getValue().getUserId()).isEqualTo(7L);
    }

    @Test
    void customerCannotUseManualPaymentEndpoint() throws Exception {
        mvc.perform(post("/api/payments/orders/11")
                        .with(authentication(actor("CUSTOMER", 7L)))
                        .contentType("application/json")
                        .content("{\"method\":\"CASH\"}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(paymentService);
    }

    @Test
    void customerCannotCreatePayOsPaymentForAnotherCustomersOrder() throws Exception {
        mvc.perform(post("/api/payments/orders/12/payos")
                        .with(authentication(actor("CUSTOMER", 7L))))
                .andExpect(status().isForbidden());
        verifyNoInteractions(paymentService);
    }

    @Test
    void staffCanUseManualPaymentEndpoint() throws Exception {
        mvc.perform(post("/api/payments/orders/11")
                        .with(authentication(actor("STAFF", 2L)))
                        .contentType("application/json")
                        .content("{\"method\":\"CASH\"}"))
                .andExpect(status().isOk());
        verify(paymentService).payOrder(eq(11L), any());
    }

    @Test
    void customerCannotAccessTableOperations() throws Exception {
        mvc.perform(get("/api/tables").with(authentication(actor("CUSTOMER", 7L))))
                .andExpect(status().isForbidden());
        verifyNoInteractions(tableService);
    }

    @Test
    void staffCannotCreateTableButAdminCan() throws Exception {
        String body = "{\"tableName\":\"T-99\",\"capacity\":4}";
        mvc.perform(post("/api/tables").with(authentication(actor("STAFF", 2L)))
                        .contentType("application/json").content(body))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/tables").with(authentication(actor("ADMIN", 1L)))
                        .contentType("application/json").content(body))
                .andExpect(status().isOk());
        verify(tableService).createTable(any());
    }

    @Test
    void correctedFeedbackRouteIsProtectedAndReachable() throws Exception {
        mvc.perform(get("/api/feedbacks").with(authentication(actor("CUSTOMER", 7L))))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/feedbacks").with(authentication(actor("ADMIN", 1L))))
                .andExpect(status().isOk());
        verify(feedbackService).getAllFeedbacks();
    }

    private UsernamePasswordAuthenticationToken actor(String role, Long userId) {
        User user = User.builder().userId(userId).email("user" + userId + "@example.com")
                .role(Role.builder().roleName(role).build()).isActive(true).build();
        return new UsernamePasswordAuthenticationToken(user, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    }
}
