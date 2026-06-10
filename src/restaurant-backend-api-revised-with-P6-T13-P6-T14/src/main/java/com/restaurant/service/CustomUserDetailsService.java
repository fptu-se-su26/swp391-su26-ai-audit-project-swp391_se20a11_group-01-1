package com.restaurant.service;
import com.restaurant.repository.UserRepository; import com.restaurant.security.SecurityUser; import lombok.RequiredArgsConstructor; import org.springframework.security.core.userdetails.UserDetails; import org.springframework.security.core.userdetails.UserDetailsService; import org.springframework.security.core.userdetails.UsernameNotFoundException; import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
 private final UserRepository userRepository;
 @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { return userRepository.findByEmail(email).map(SecurityUser::new).orElseThrow(() -> new UsernameNotFoundException("Email not found")); }
}
