package com.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.dto.auth.AuthResponse;
import com.restaurant.dto.auth.LoginRequest;
import com.restaurant.dto.auth.RegisterRequest;
import com.restaurant.dto.auth.UserProfileResponse;
import com.restaurant.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void registerShouldReturnCreated() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Nguyen Van A");
        request.setEmail("customer1@gmail.com");
        request.setPassword("123456");

        Mockito.when(authService.register(Mockito.any(RegisterRequest.class)))
                .thenReturn(AuthResponse.builder().token("token").tokenType("Bearer").build());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void loginShouldReturnOk() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("customer1@gmail.com");
        request.setPassword("123456");

        Mockito.when(authService.login(Mockito.any(LoginRequest.class)))
                .thenReturn(AuthResponse.builder().token("token").tokenType("Bearer").build());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "customer1@gmail.com")
    void meShouldReturnOk() throws Exception {
        Mockito.when(authService.getCurrentUser("customer1@gmail.com"))
                .thenReturn(UserProfileResponse.builder().email("customer1@gmail.com").build());

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk());
    }
}
