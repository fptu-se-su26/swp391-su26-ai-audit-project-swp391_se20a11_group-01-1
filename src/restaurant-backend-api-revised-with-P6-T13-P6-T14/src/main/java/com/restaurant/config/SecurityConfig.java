package com.restaurant.config;
import com.restaurant.security.JwtAuthenticationFilter; import lombok.RequiredArgsConstructor; import org.springframework.context.annotation.*; import org.springframework.security.authentication.AuthenticationManager; import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.config.http.SessionCreationPolicy; import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.security.web.SecurityFilterChain; import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration @EnableMethodSecurity @RequiredArgsConstructor
public class SecurityConfig {
 private final JwtAuthenticationFilter jwtAuthenticationFilter;
 @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
 @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { return config.getAuthenticationManager(); }
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
  http.csrf(csrf->csrf.disable()).sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(auth->auth
    .requestMatchers("/auth/**","/foods/**","/categories/**","/health/**").permitAll()
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/staff/**").hasAnyRole("STAFF","ADMIN")
    .requestMatchers("/kitchen/**").hasAnyRole("KITCHEN","ADMIN")
    .anyRequest().authenticated())
   .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
  return http.build();
 }
}
