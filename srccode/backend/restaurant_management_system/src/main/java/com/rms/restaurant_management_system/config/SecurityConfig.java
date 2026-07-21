package com.rms.restaurant_management_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.config.http.SessionCreationPolicy;
import com.rms.restaurant_management_system.security.JwtAuthenticationFilter;

import java.util.List;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:3001}")
    private List<String> allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"Chưa đăng nhập hoặc phiên đã hết hạn\"}");
                        })
                        .accessDeniedHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"code\":\"FORBIDDEN\",\"message\":\"Bạn không có quyền thực hiện thao tác này\"}");
                        }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Auth API
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login",
                                "/api/auth/forgot-password", "/api/auth/reset-password", "/api/auth/refresh").permitAll()

                        // Category API
                        .requestMatchers(HttpMethod.GET, "/api/categories/**", "/api/foods/**").permitAll()

                        // Food API
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

                        // Tạm thời cho phép toàn bộ request khác để demo/dev
                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(allowedOrigins);

        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));
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
