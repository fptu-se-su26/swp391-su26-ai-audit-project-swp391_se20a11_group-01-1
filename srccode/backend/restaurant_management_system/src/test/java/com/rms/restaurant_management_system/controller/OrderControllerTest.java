package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.dto.request.UpdateOrderItemStatusRequest;
import com.rms.restaurant_management_system.service.interfaces.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new OrderController(orderService)).build();
    }

    @Test
    void emptyKitchenStatusesReturnBadRequest() throws Exception {
        when(orderService.getKitchenItems(anyList())).thenThrow(
                new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "At least one order item status is required"
                )
        );

        mockMvc.perform(get("/api/orders/kitchen/items").queryParam("statuses", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidItemTransitionReturnsBadRequest() throws Exception {
        when(orderService.updateOrderItemStatus(
                eq(11L),
                any(UpdateOrderItemStatusRequest.class)
        )).thenThrow(new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid order item status transition"
        ));

        mockMvc.perform(patch("/api/orders/items/11/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CANCELLED\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownKitchenStatusReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/orders/kitchen/items")
                        .queryParam("statuses", "NOT_A_STATUS"))
                .andExpect(status().isBadRequest());
    }
}
