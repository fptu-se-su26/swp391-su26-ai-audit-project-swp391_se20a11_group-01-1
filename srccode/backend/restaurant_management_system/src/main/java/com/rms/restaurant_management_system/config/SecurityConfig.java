package com.rms.restaurant_management_system.config;

import com.rms.restaurant_management_system.error.*;
import com.rms.restaurant_management_system.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CorrelationIdFilter correlationIdFilter;
    private final ApiErrorWriter apiErrorWriter;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:3001}")
    private List<String> allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, CorrelationIdFilter correlationIdFilter,
                          ApiErrorWriter apiErrorWriter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.correlationIdFilter = correlationIdFilter;
        this.apiErrorWriter = apiErrorWriter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable()).httpBasic(basic -> basic.disable())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, exception) ->
                                apiErrorWriter.write(request, response, HttpStatus.UNAUTHORIZED,
                                        ErrorCode.AUTHENTICATION_REQUIRED,
                                        "Chưa đăng nhập hoặc phiên đăng nhập đã hết hạn"))
                        .accessDeniedHandler((request, response, exception) ->
                                apiErrorWriter.write(request, response, HttpStatus.FORBIDDEN,
                                        ErrorCode.ACCESS_DENIED, "Bạn không có quyền thực hiện thao tác này")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login",
                                "/api/auth/forgot-password", "/api/auth/reset-password", "/api/auth/refresh")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**", "/api/foods/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/payments/payos/webhook").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/categories/**", "/api/foods/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**", "/api/foods/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/foods/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**", "/api/foods/**").hasRole("ADMIN")
                        .requestMatchers("/api/dashboard/**").hasRole("ADMIN")
                        .requestMatchers("/api/kitchen/**").hasAnyRole("KITCHEN", "ADMIN")
                        .requestMatchers("/api/users/**", "/api/auth/**").authenticated()
                        .requestMatchers("/api/orders/**", "/api/reservations/**", "/api/feedbacks/**",
                                "/api/payments/**", "/api/tables/**").authenticated()
                        .anyRequest().denyAll())
                .addFilterBefore(correlationIdFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of(CorrelationIdFilter.HEADER));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
